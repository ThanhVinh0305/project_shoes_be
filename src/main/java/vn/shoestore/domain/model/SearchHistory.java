package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking search history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {
  private Long id;
  
  /**
   * User ID (can be null for guest users)
   */
  private Long userId;
  
  private String searchKeyword;
  
  /**
   * Search filters as JSON (brand, category, price range, etc.)
   */
  private String searchFilters;
  
  /**
   * Number of results found
   */
  private Integer resultCount;
  
  private LocalDateTime createdDate;
}
