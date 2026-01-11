package vn.shoestore.usecases.logic.behavior;

public interface ITrackBehaviorUseCase {
  
  /**
   * Track product view behavior
   * @param userId User ID (can be null for guest)
   * @param productId Product ID
   * @param viewDuration Duration in seconds
   */
  void trackProductView(Long userId, Long productId, Integer viewDuration);
  
  /**
   * Track search behavior
   * @param userId User ID (can be null for guest)
   * @param keyword Search keyword
   * @param filters Search filters as JSON string
   * @param resultCount Number of results
   */
  void trackSearch(Long userId, String keyword, String filters, Integer resultCount);
  
  /**
   * Track click behavior
   * @param userId User ID
   * @param productId Product ID
   * @param source Click source (recommendation, search, similar, etc.)
   */
  void trackClick(Long userId, Long productId, String source);
  
  /**
   * Track add to cart behavior
   * @param userId User ID
   * @param productId Product ID
   */
  void trackAddToCart(Long userId, Long productId);
  
  /**
   * Track purchase behavior
   * @param userId User ID
   * @param productId Product ID
   */
  void trackPurchase(Long userId, Long productId);
}
