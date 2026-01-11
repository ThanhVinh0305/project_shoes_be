package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.UserPreference;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceAdapter {
  
  /**
   * Save or update a user preference
   */
  UserPreference save(UserPreference preference);
  
  /**
   * Find all preferences by user ID
   */
  List<UserPreference> findByUserId(Long userId);
  
  /**
   * Find preferences by user and type
   */
  List<UserPreference> findByUserIdAndPreferenceType(Long userId, String preferenceType);
  
  /**
   * Find preference by user, type, and value
   */
  Optional<UserPreference> findByUserIdAndPreferenceTypeAndValue(Long userId, String type, String value);
  
  /**
   * Find top preferences by user and type (ordered by score desc)
   */
  List<UserPreference> findTopByUserIdAndType(Long userId, String type, int limit);
  
  /**
   * Delete old preferences by user ID
   */
  void deleteByUserId(Long userId);
  
  /**
   * Batch save preferences
   */
  List<UserPreference> saveAll(List<UserPreference> preferences);
}
