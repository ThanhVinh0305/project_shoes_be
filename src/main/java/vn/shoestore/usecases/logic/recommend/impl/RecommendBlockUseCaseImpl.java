package vn.shoestore.usecases.logic.recommend.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.application.response.RecommendBlocksResponse;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;
import vn.shoestore.usecases.logic.recommend.IRecommendBlockUseCase;

@UseCase
@RequiredArgsConstructor
public class RecommendBlockUseCaseImpl implements IRecommendBlockUseCase {

  private final JdbcTemplate jdbcTemplate;
  private final IGetProductUseCase getProductUseCase;
  private final UserAdapter userAdapter;
  private final vn.shoestore.domain.adapter.UserPreferenceAdapter userPreferenceAdapter;

  @Override
  public RecommendBlocksResponse getBlocks(Long userId) {
    RecommendBlocksResponse.RecommendBlocksResponseBuilder builder =
        RecommendBlocksResponse.builder();

    Long userGenderId = null;
    if (userId != null) {
      List<User> users = userAdapter.getUserByIdIn(java.util.Collections.singletonList(userId));
      if (!users.isEmpty()) {
        User user = users.get(0);
        userGenderId = user.getGenderId();
      }
    }

    try {
      // Guest blocks (sale + random today)
      List<Long> saleIds = querySaleProducts(12, userGenderId);
      if (saleIds != null && !saleIds.isEmpty()) {
        builder.guestSale(fetchProducts(saleIds));
      }

      // Gợi ý hôm nay: random nhưng ưu tiên brands user quan tâm
      List<Long> randomIds = queryRandomProducts(12, userGenderId, userId);
      if (randomIds != null && !randomIds.isEmpty()) {
        builder.guestToday(fetchProducts(randomIds));
      }

      if (userId != null) {
        // Top viewed: sản phẩm có tổng lượt xem cao nhất (từ tất cả users)
        // Filter theo gender của user nếu có
        List<Long> topViewed = queryTopViewedProducts(12, userGenderId);
        if (topViewed != null && !topViewed.isEmpty()) {
          builder.userTopViewed(fetchProductsWithTotalViewCount(topViewed));
        }

        // Viewer Preferences: Gợi ý dựa trên user_preferences (top brands/products user quan tâm)
        List<Long> viewerPrefIds = queryViewerPreferences(userId, 12, userGenderId);
        if (viewerPrefIds != null && !viewerPrefIds.isEmpty()) {
          builder.viewerPreferences(fetchProducts(viewerPrefIds));
        }

        // Top search: TẠM THỜI BỎ - sẽ thêm sau
        builder.userTopSearch(new ArrayList<>()); // Trả về empty list tạm thời
      }
    } catch (Exception e) {
      // Log error và trả về response rỗng thay vì crash
      e.printStackTrace();
      // Vẫn build response với empty lists (đã có @Builder.Default)
    }

    return builder.build();
  }

  private List<ProductResponse> fetchProducts(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return new ArrayList<>();
    return getProductUseCase.findByIds(ids);
  }

  private List<ProductResponse> fetchProductsWithTotalViewCount(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return new ArrayList<>();
    List<ProductResponse> products = getProductUseCase.findByIds(ids);
    if (products == null || products.isEmpty()) return new ArrayList<>();
    
    try {
      // Lấy tổng view_count của sản phẩm (từ tất cả users)
      String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
      String sql = String.format(
          "SELECT product_id, SUM(view_count) as total_view_count FROM product_views WHERE product_id IN (%s) GROUP BY product_id",
          placeholders
      );
      
      var viewCountMap = jdbcTemplate.query(sql, ids.toArray(), (rs, i) -> {
        return new Object[] {rs.getLong("product_id"), rs.getInt("total_view_count")};
      }).stream().collect(java.util.stream.Collectors.toMap(
          arr -> (Long) arr[0],
          arr -> (Integer) arr[1]
      ));
      
      // Set view_count cho từng sản phẩm
      for (ProductResponse product : products) {
        if (product == null) continue;
        Integer viewCount = viewCountMap.get(product.getId());
        if (viewCount != null) {
          product.setViewCount(viewCount);
        }
      }
    } catch (Exception e) {
      // Nếu có lỗi, vẫn trả về products nhưng không có view_count
      e.printStackTrace();
    }
    
    return products;
  }

  private List<ProductResponse> fetchProductsWithSearchCount(Long userId, List<Long> ids) {
    if (ids == null || ids.isEmpty()) return new ArrayList<>();
    List<ProductResponse> products = getProductUseCase.findByIds(ids);
    if (products == null || products.isEmpty()) return new ArrayList<>();
    
    try {
      // Lấy search_count cho từng sản phẩm (tổng số lượt tìm kiếm keywords liên quan)
      // Lấy top keywords của user
      String topKeywordSql = """
          SELECT search_keyword, COUNT(*) as cnt
          FROM search_histories
          WHERE user_id = ?
          GROUP BY search_keyword
          HAVING COUNT(*) >= 40
          ORDER BY COUNT(*) DESC
          LIMIT 3
          """;
      
      var keywordCounts = jdbcTemplate.query(topKeywordSql, (rs, i) -> {
        return new Object[] {rs.getString("search_keyword"), rs.getInt("cnt")};
      }, userId);
      
      if (keywordCounts == null || keywordCounts.isEmpty()) return products;
      
      // Map product_id -> search_count (tổng số lượt tìm kiếm keywords match với tên sản phẩm)
      var searchCountMap = new java.util.HashMap<Long, Integer>();
      
      for (ProductResponse product : products) {
        if (product == null || product.getName() == null) continue;
        
        int totalSearchCount = 0;
        String productName = product.getName().toLowerCase();
        
        for (Object[] keywordCount : keywordCounts) {
          if (keywordCount == null || keywordCount.length < 2) continue;
          String keyword = ((String) keywordCount[0]);
          if (keyword == null) continue;
          int count = (Integer) keywordCount[1];
          
          if (productName.contains(keyword.toLowerCase())) {
            totalSearchCount += count;
          }
        }
        
        if (totalSearchCount > 0) {
          searchCountMap.put(product.getId(), totalSearchCount);
        }
      }
      
      // Set search_count cho từng sản phẩm
      for (ProductResponse product : products) {
        if (product == null) continue;
        Integer searchCount = searchCountMap.get(product.getId());
        if (searchCount != null) {
          product.setSearchCount(searchCount);
        }
      }
    } catch (Exception e) {
      // Nếu có lỗi, vẫn trả về products nhưng không có search_count
      // Log error nếu cần
    }
    
    return products;
  }

  private List<Long> querySaleProducts(int limit, Long userGenderId) {
    // Ưu tiên sản phẩm có thumbnail, filter theo gender nếu có user
    String genderFilter = "";
    List<Object> params = new ArrayList<>();
    
    if (userGenderId != null) {
      // User nữ (1) → sản phẩm nữ (1) hoặc unisex (3)
      // User nam (2) → sản phẩm nam (2) hoặc unisex (3)
      genderFilter = " AND (p.gender_id = ? OR p.gender_id = 3)";
      params.add(userGenderId);
    }
    
    String sql =
        """
        SELECT pp.product_id, MAX(pr.percent_discount) AS discount, MIN(pr.end_date) AS end_date
        FROM product_promotions pp
        JOIN promotions pr ON pp.promotion_id = pr.id
        JOIN products p ON pp.product_id = p.id
        WHERE NOW() BETWEEN pr.start_date AND pr.end_date
          AND (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '')
        """ + genderFilter + """
        GROUP BY pp.product_id
        ORDER BY discount DESC, end_date ASC
        LIMIT ?
        """;
    params.add(limit);
    try {
      List<Long> result = jdbcTemplate.query(sql, params.toArray(), (rs, i) -> rs.getLong("product_id"));
      return result != null ? result : new ArrayList<>();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private List<Long> queryRandomProducts(int limit, Long userGenderId, Long userId) {
    try {
      // Nếu user đã login và có preferences → Ưu tiên brands user thích
      if (userId != null) {
        List<vn.shoestore.domain.model.UserPreference> topBrands = 
            userPreferenceAdapter.findTopByUserIdAndType(userId, "BRAND", 3);
        
        if (!topBrands.isEmpty()) {
          // Lấy 70% từ brands user thích + 30% random
          int preferredCount = (int) (limit * 0.7);
          int randomCount = limit - preferredCount;
          
          List<Long> preferredProducts = queryProductsByPreferredBrands(topBrands, preferredCount, userGenderId);
          List<Long> randomProducts = queryPureRandomProducts(randomCount, userGenderId, preferredProducts);
          
          // Merge và shuffle
          List<Long> combined = new ArrayList<>(preferredProducts);
          combined.addAll(randomProducts);
          java.util.Collections.shuffle(combined);
          return combined;
        }
      }
      
      // Fallback: Random thuần túy (cho guest hoặc user chưa có preferences)
      return queryPureRandomProducts(limit, userGenderId, new ArrayList<>());
      
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
  
  private List<Long> queryProductsByPreferredBrands(List<vn.shoestore.domain.model.UserPreference> topBrands, int limit, Long userGenderId) {
    try {
      List<Long> brandIds = topBrands.stream()
          .map(pref -> {
            try {
              return Long.parseLong(pref.getPreferenceValue());
            } catch (NumberFormatException e) {
              return null;
            }
          })
          .filter(id -> id != null && id > 0)
          .collect(java.util.stream.Collectors.toList());
      
      if (brandIds.isEmpty()) return new ArrayList<>();
      
      String genderFilter = "";
      List<Object> params = new ArrayList<>();
      
      if (userGenderId != null) {
        genderFilter = " AND (p.gender_id = ? OR p.gender_id = 3)";
        params.add(userGenderId);
      }
      
      String brandPlaceholders = String.join(",", java.util.Collections.nCopies(brandIds.size(), "?"));
      String sql = "SELECT p.id FROM products p " +
          "JOIN product_brands pb ON p.id = pb.product_id " +
          "WHERE pb.brand_id IN (" + brandPlaceholders + ") " +
          "  AND (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '') " +
          genderFilter +
          " ORDER BY RAND() " +
          "LIMIT ?";
      
      List<Object> allParams = new ArrayList<>(brandIds);
      allParams.addAll(params);
      allParams.add(limit);
      
      List<Long> result = jdbcTemplate.query(sql, allParams.toArray(), (rs, i) -> rs.getLong(1));
      return result != null ? result : new ArrayList<>();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
  
  private List<Long> queryPureRandomProducts(int limit, Long userGenderId, List<Long> excludeIds) {
    try {
      String genderFilter = "";
      String exclusionFilter = "";
      List<Object> params = new ArrayList<>();
      
      if (userGenderId != null) {
        genderFilter = " AND (p.gender_id = ? OR p.gender_id = 3)";
        params.add(userGenderId);
      }
      
      if (!excludeIds.isEmpty()) {
        String placeholders = String.join(",", java.util.Collections.nCopies(excludeIds.size(), "?"));
        exclusionFilter = " AND p.id NOT IN (" + placeholders + ")";
        params.addAll(excludeIds);
      }
      
      String sql = "SELECT id FROM products p " +
          "WHERE (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '') " +
          genderFilter + exclusionFilter +
          " ORDER BY RAND() " +
          "LIMIT ?";
      params.add(limit);
      
      List<Long> result = jdbcTemplate.query(sql, params.toArray(), (rs, i) -> rs.getLong(1));
      return result != null ? result : new ArrayList<>();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private List<Long> queryTopViewedProducts(int limit, Long userGenderId) {
    try {
      // Lấy top sản phẩm có tổng lượt xem cao nhất (từ tất cả users)
      // Filter theo gender + unisex nếu có user
      // Chỉ lấy sản phẩm có tổng lượt xem >= 1900, ưu tiên có thumbnail
      String genderFilter = "";
      List<Object> params = new ArrayList<>();
      
      if (userGenderId != null) {
        // User nữ (1) → sản phẩm nữ (1) hoặc unisex (3)
        // User nam (2) → sản phẩm nam (2) hoặc unisex (3)
        genderFilter = " AND (p.gender_id = ? OR p.gender_id = 3)";
        params.add(userGenderId);
      }
      
      String sql = "SELECT pv.product_id, SUM(pv.view_count) as total_views " +
          "FROM product_views pv " +
          "JOIN products p ON pv.product_id = p.id " +
          "WHERE (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '') " +
          genderFilter +
          " GROUP BY pv.product_id " +
          "HAVING SUM(pv.view_count) >= 1900 " +
          "ORDER BY total_views DESC " +
          "LIMIT ?";
      params.add(limit);
      
      List<Long> result = jdbcTemplate.query(sql, params.toArray(), (rs, i) -> rs.getLong("product_id"));
      return result != null ? result : new ArrayList<>();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private List<Long> queryUserTopSearch(Long userId, int limit, Long userGenderId) {
    try {
      // Lấy top tên sản phẩm mà user đã tìm kiếm (>= 40 lượt)
      String topProductNameSql =
          """
          SELECT search_keyword as product_name
          FROM search_histories
          WHERE user_id = ?
          GROUP BY search_keyword
          HAVING COUNT(*) >= 40
          ORDER BY COUNT(*) DESC, MAX(created_date) DESC
          LIMIT ?
          """;
      List<String> productNames = jdbcTemplate.query(topProductNameSql, (rs, i) -> rs.getString(1), userId, limit);
      if (productNames == null || productNames.isEmpty()) return new ArrayList<>();

      // Tìm sản phẩm theo tên chính xác hoặc LIKE, filter theo gender, ưu tiên có thumbnail
      String genderFilter = "";
      List<Object> params = new ArrayList<>();
      
      if (userGenderId != null) {
        // User nữ (1) → sản phẩm nữ (1)
        // User nam (2) → sản phẩm nam (2)
        genderFilter = " AND p.gender_id = ?";
        params.add(userGenderId);
      }
      
      StringBuilder sb = new StringBuilder();
      sb.append("SELECT id FROM products p ");
      sb.append("WHERE (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '') ");
      sb.append(genderFilter);
      sb.append(" AND (");
      for (int i = 0; i < productNames.size(); i++) {
        if (i > 0) sb.append(" OR ");
        sb.append("p.name = ? OR p.name LIKE ?");
        params.add(productNames.get(i));
        params.add("%" + productNames.get(i) + "%");
      }
      sb.append(") LIMIT ").append(limit);

      List<Long> result = jdbcTemplate.query(sb.toString(), params.toArray(), (rs, i) -> rs.getLong(1));
      return result != null ? result : new ArrayList<>();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Gợi ý dựa trên user_preferences (AI-powered recommendations)
   * Gộp: Hành vi (CLICK, ADD_TO_CART) + Lượt xem + Tìm kiếm
   * Logic:
   * 1. Lấy top brands/products user quan tâm từ user_preferences (tất cả behaviors)
   * 2. Lấy top keywords user tìm kiếm → map sang brands/products
   * 3. Gợi ý sản phẩm từ các brands đó, filter theo gender
   * 4. Loại trừ sản phẩm user đã có preferences (tránh trùng lặp)
   * 5. Kết hợp cả brand preferences và product preferences với trọng số
   */
  private List<Long> queryViewerPreferences(Long userId, int limit, Long userGenderId) {
    try {
      // 1. Lấy top 3 brands user quan tâm nhất
      List<vn.shoestore.domain.model.UserPreference> topBrands = 
          userPreferenceAdapter.findTopByUserIdAndType(userId, "BRAND", 3);
      
      // 2. Lấy danh sách product IDs user đã có preference (để loại trừ)
      List<vn.shoestore.domain.model.UserPreference> userProducts = 
          userPreferenceAdapter.findByUserIdAndPreferenceType(userId, "PRODUCT");
      
      java.util.Set<Long> excludedProductIds = userProducts.stream()
          .map(pref -> {
            try {
              return Long.parseLong(pref.getPreferenceValue());
            } catch (NumberFormatException e) {
              return null;
            }
          })
          .filter(id -> id != null)
          .collect(java.util.stream.Collectors.toSet());

      if (topBrands.isEmpty()) {
        // Nếu chưa có preferences, trả về random products theo gender
        return queryRandomProducts(limit, userGenderId, userId);
      }

      // 3. Lấy brand IDs từ preferences
      List<Long> brandIds = topBrands.stream()
          .map(pref -> {
            try {
              return Long.parseLong(pref.getPreferenceValue());
            } catch (NumberFormatException e) {
              return null;
            }
          })
          .filter(id -> id != null && id > 0)
          .collect(java.util.stream.Collectors.toList());

      if (brandIds.isEmpty()) {
        return queryRandomProducts(limit, userGenderId, userId);
      }

      // 4. Query sản phẩm từ các brands đó
      String genderFilter = "";
      List<Object> params = new ArrayList<>();
      
      if (userGenderId != null) {
        genderFilter = " AND (p.gender_id = ? OR p.gender_id = 3)";
        params.add(userGenderId);
      }

      // Build exclusion filter
      String exclusionFilter = "";
      if (!excludedProductIds.isEmpty()) {
        String placeholders = String.join(",", java.util.Collections.nCopies(excludedProductIds.size(), "?"));
        exclusionFilter = " AND p.id NOT IN (" + placeholders + ")";
        params.addAll(excludedProductIds);
      }

      // Build brand filter
      String brandPlaceholders = String.join(",", java.util.Collections.nCopies(brandIds.size(), "?"));
      
      String sql = "SELECT p.id FROM products p " +
          "JOIN product_brands pb ON p.id = pb.product_id " +
          "WHERE pb.brand_id IN (" + brandPlaceholders + ") " +
          "  AND (p.thumbnail_img IS NOT NULL AND p.thumbnail_img != '') " +
          genderFilter + exclusionFilter +
          " ORDER BY RAND() " +
          "LIMIT ?";

      List<Object> allParams = new ArrayList<>();
      allParams.addAll(brandIds);
      allParams.addAll(params);
      allParams.add(limit);

      List<Long> result = jdbcTemplate.query(sql, allParams.toArray(), (rs, i) -> rs.getLong(1));
      return result != null ? result : new ArrayList<>();
      
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}

