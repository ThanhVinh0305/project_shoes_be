package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.Recommendation;

import java.util.List;

public interface RecommendationAdapter {
  
  /**
   * Save a recommendation
   */
  Recommendation save(Recommendation recommendation);
  
  /**
   * Find recommendations by user ID
   */
  List<Recommendation> findByUserId(Long userId);
  
  /**
   * Find recommendations by user and type
   */
  List<Recommendation> findByUserIdAndType(Long userId, String type, int limit);
  
  /**
   * Find top recommendations by user (ordered by score desc)
   */
  List<Recommendation> findTopByUserId(Long userId, int limit);
  
  /**
   * Update recommendation as shown
   */
  void markAsShown(Long recommendationId);
  
  /**
   * Update recommendation as clicked
   */
  void markAsClicked(Long recommendationId);
  
  /**
   * Delete old recommendations by user ID
   */
  void deleteOldRecommendationsByUserId(Long userId, int daysToKeep);
  
  /**
   * Batch save recommendations
   */
  List<Recommendation> saveAll(List<Recommendation> recommendations);
  
  /**
   * Check if recommendation exists
   */
  boolean existsByUserIdAndProductId(Long userId, Long productId);
}
