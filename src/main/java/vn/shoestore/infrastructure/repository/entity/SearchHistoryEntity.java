package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(name = "search_keyword", nullable = false)
  private String searchKeyword;
  
  @Column(name = "search_filters", columnDefinition = "JSON")
  private String searchFilters;
  
  @Column(name = "result_count")
  private Integer resultCount;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (resultCount == null) {
      resultCount = 0;
    }
  }
}
