package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for storing AI-generated recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {
  private Long id;
  
  private Long userId;
  
  private Long productId;
  
  /**
   * Recommendation types: COLLABORATIVE_FILTERING, CONTENT_BASED, HYBRID, TRENDING, SIMILAR_PRODUCTS
   */
  private String recommendationType;
  
  /**
   * Recommendation score (higher is better)
   */
  private BigDecimal recommendationScore;
  
  /**
   * Reason for recommendation (e.g., "Users also bought", "Similar products")
   */
  private String reason;
  
  /**
   * Whether the recommendation was shown to user
   */
  private Boolean isShown;
  
  /**
   * Whether user clicked on the recommendation
   */
  private Boolean isClicked;
  
  private LocalDateTime createdDate;
}
