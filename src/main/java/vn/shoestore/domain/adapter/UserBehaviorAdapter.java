package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.UserBehavior;

import java.time.LocalDateTime;
import java.util.List;

public interface UserBehaviorAdapter {
  
  /**
   * Save a new user behavior
   */
  UserBehavior save(UserBehavior behavior);
  
  /**
   * Find behaviors by user ID
   */
  List<UserBehavior> findByUserId(Long userId);
  
  /**
   * Find behaviors by user and behavior type
   */
  List<UserBehavior> findByUserIdAndBehaviorType(Long userId, String behaviorType);
  
  /**
   * Find behaviors by user within a date range
   */
  List<UserBehavior> findByUserIdAndCreatedDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
  
  /**
   * Find behaviors by product ID
   */
  List<UserBehavior> findByProductId(Long productId);
  
  /**
   * Count behaviors by type for a user
   */
  Long countByUserIdAndBehaviorType(Long userId, String behaviorType);
}
