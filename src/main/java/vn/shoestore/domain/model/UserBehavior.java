package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking user behaviors (VIEW, CLICK, SEARCH, ADD_TO_CART, PURCHASE, RATING)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehavior {
  private Long id;
  
  private Long userId;
  
  private Long productId;
  
  /**
   * Behavior types: VIEW, CLICK, SEARCH, ADD_TO_CART, PURCHASE, RATING
   */
  private String behaviorType;
  
  /**
   * Additional data stored as JSON (search keywords, rating value, etc.)
   */
  private String behaviorData;
  
  private LocalDateTime createdDate;
}
