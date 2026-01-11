package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking detailed product views
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductView {
  private Long id;
  
  private Long userId;
  
  private Long productId;
  
  /**
   * Duration of view in seconds
   */
  private Integer viewDuration;
  
  /**
   * Number of times viewed
   */
  private Integer viewCount;
  
  private LocalDateTime lastViewedDate;
  
  private LocalDateTime createdDate;
}
