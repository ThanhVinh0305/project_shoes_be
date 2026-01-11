package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for storing computed user preferences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {
  private Long id;
  
  private Long userId;
  
  /**
   * Preference types: BRAND, CATEGORY, PRICE_RANGE, COLOR, GENDER, STYLE
   */
  private String preferenceType;
  
  /**
   * The value of the preference (e.g., brand_id, color name, price range)
   */
  private String preferenceValue;
  
  /**
   * Preference score (0-100)
   */
  private BigDecimal preferenceScore;
  
  private LocalDateTime lastUpdated;
  
  private LocalDateTime createdDate;
}
