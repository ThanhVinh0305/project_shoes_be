package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ProductView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductViewAdapter {
  
  /**
   * Save or update a product view
   */
  ProductView save(ProductView productView);
  
  /**
   * Find view by user and product
   */
  Optional<ProductView> findByUserIdAndProductId(Long userId, Long productId);
  
  /**
   * Find all views by user ID
   */
  List<ProductView> findByUserId(Long userId);
  
  /**
   * Find top viewed products by user (ordered by view_count desc)
   */
  List<ProductView> findTopViewedByUserId(Long userId, int limit);
  
  /**
   * Find recently viewed products by user
   */
  List<ProductView> findRecentlyViewedByUserId(Long userId, int limit);
  
  /**
   * Find views by user within date range
   */
  List<ProductView> findByUserIdAndLastViewedDateAfter(Long userId, LocalDateTime after);
  
  /**
   * Get total view count for a product (from all users)
   */
  Long getTotalViewCountByProductId(Long productId);
  
  /**
   * Get top viewed products globally
   */
  List<Long> findTopViewedProducts(int limit);
}
