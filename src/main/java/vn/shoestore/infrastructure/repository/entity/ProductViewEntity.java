package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(name = "product_id", nullable = false)
  private Long productId;
  
  @Column(name = "view_duration")
  private Integer viewDuration;
  
  @Column(name = "view_count")
  private Integer viewCount;
  
  @Column(name = "last_viewed_date")
  private LocalDateTime lastViewedDate;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (lastViewedDate == null) {
      lastViewedDate = LocalDateTime.now();
    }
    if (viewCount == null) {
      viewCount = 1;
    }
  }
  
  @PreUpdate
  protected void onUpdate() {
    lastViewedDate = LocalDateTime.now();
  }
}
