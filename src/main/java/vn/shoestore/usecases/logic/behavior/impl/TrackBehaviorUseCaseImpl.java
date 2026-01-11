package vn.shoestore.usecases.logic.behavior.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.shoestore.domain.adapter.ProductViewAdapter;
import vn.shoestore.domain.adapter.SearchHistoryAdapter;
import vn.shoestore.domain.adapter.UserBehaviorAdapter;
import vn.shoestore.domain.model.ProductView;
import vn.shoestore.domain.model.SearchHistory;
import vn.shoestore.domain.model.UserBehavior;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.usecases.logic.behavior.ITrackBehaviorUseCase;

import java.time.LocalDateTime;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class TrackBehaviorUseCaseImpl implements ITrackBehaviorUseCase {
  
  private final UserBehaviorAdapter userBehaviorAdapter;
  private final ProductViewAdapter productViewAdapter;
  private final SearchHistoryAdapter searchHistoryAdapter;
  
  @Override
  public void trackProductView(Long userId, Long productId, Integer viewDuration) {
    try {
      // Save to user_behaviors
      UserBehavior behavior = UserBehavior.builder()
          .userId(userId)
          .productId(productId)
          .behaviorType("VIEW")
          .behaviorData("{\"duration\":" + (viewDuration != null ? viewDuration : 0) + "}")
          .createdDate(LocalDateTime.now())
          .build();
      userBehaviorAdapter.save(behavior);
      
      // Update or create product_views
      if (userId != null) {
        Optional<ProductView> existingView = productViewAdapter.findByUserIdAndProductId(userId, productId);
        
        if (existingView.isPresent()) {
          // Update existing view
          ProductView view = existingView.get();
          view.setViewCount(view.getViewCount() + 1);
          view.setViewDuration((view.getViewDuration() != null ? view.getViewDuration() : 0) + 
                               (viewDuration != null ? viewDuration : 0));
          view.setLastViewedDate(LocalDateTime.now());
          productViewAdapter.save(view);
        } else {
          // Create new view
          ProductView newView = ProductView.builder()
              .userId(userId)
              .productId(productId)
              .viewDuration(viewDuration != null ? viewDuration : 0)
              .viewCount(1)
              .lastViewedDate(LocalDateTime.now())
              .createdDate(LocalDateTime.now())
              .build();
          productViewAdapter.save(newView);
        }
      }
      
      log.info("Tracked VIEW: userId={}, productId={}, duration={}", userId, productId, viewDuration);
    } catch (Exception e) {
      log.error("Error tracking product view", e);
    }
  }
  
  @Override
  public void trackSearch(Long userId, String keyword, String filters, Integer resultCount) {
    try {
      // Save to user_behaviors
      UserBehavior behavior = UserBehavior.builder()
          .userId(userId)
          .productId(null)
          .behaviorType("SEARCH")
          .behaviorData("{\"keyword\":\"" + keyword + "\",\"filters\":" + (filters != null ? filters : "null") + "}")
          .createdDate(LocalDateTime.now())
          .build();
      userBehaviorAdapter.save(behavior);
      
      // Save to search_histories
      SearchHistory searchHistory = SearchHistory.builder()
          .userId(userId)
          .searchKeyword(keyword)
          .searchFilters(filters)
          .resultCount(resultCount != null ? resultCount : 0)
          .createdDate(LocalDateTime.now())
          .build();
      searchHistoryAdapter.save(searchHistory);
      
      log.info("Tracked SEARCH: userId={}, keyword={}, results={}", userId, keyword, resultCount);
    } catch (Exception e) {
      log.error("Error tracking search", e);
    }
  }
  
  @Override
  public void trackClick(Long userId, Long productId, String source) {
    try {
      UserBehavior behavior = UserBehavior.builder()
          .userId(userId)
          .productId(productId)
          .behaviorType("CLICK")
          .behaviorData("{\"source\":\"" + source + "\"}")
          .createdDate(LocalDateTime.now())
          .build();
      userBehaviorAdapter.save(behavior);
      
      log.info("Tracked CLICK: userId={}, productId={}, source={}", userId, productId, source);
    } catch (Exception e) {
      log.error("Error tracking click", e);
    }
  }
  
  @Override
  public void trackAddToCart(Long userId, Long productId) {
    try {
      UserBehavior behavior = UserBehavior.builder()
          .userId(userId)
          .productId(productId)
          .behaviorType("ADD_TO_CART")
          .createdDate(LocalDateTime.now())
          .build();
      userBehaviorAdapter.save(behavior);
      
      log.info("Tracked ADD_TO_CART: userId={}, productId={}", userId, productId);
    } catch (Exception e) {
      log.error("Error tracking add to cart", e);
    }
  }
  
  @Override
  public void trackPurchase(Long userId, Long productId) {
    try {
      UserBehavior behavior = UserBehavior.builder()
          .userId(userId)
          .productId(productId)
          .behaviorType("PURCHASE")
          .createdDate(LocalDateTime.now())
          .build();
      userBehaviorAdapter.save(behavior);
      
      log.info("Tracked PURCHASE: userId={}, productId={}", userId, productId);
    } catch (Exception e) {
      log.error("Error tracking purchase", e);
    }
  }
}
