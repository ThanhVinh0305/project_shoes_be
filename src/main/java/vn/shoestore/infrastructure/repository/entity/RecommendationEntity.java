package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id", nullable = false)
  private Long userId;
  
  @Column(name = "product_id", nullable = false)
  private Long productId;
  
  @Column(name = "recommendation_type", nullable = false)
  private String recommendationType;
  
  @Column(name = "recommendation_score", precision = 10, scale = 6)
  private BigDecimal recommendationScore;
  
  @Column(name = "reason", columnDefinition = "TEXT")
  private String reason;
  
  @Column(name = "is_shown")
  private Boolean isShown;
  
  @Column(name = "is_clicked")
  private Boolean isClicked;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (isShown == null) {
      isShown = false;
    }
    if (isClicked == null) {
      isClicked = false;
    }
    if (recommendationScore == null) {
      recommendationScore = BigDecimal.ZERO;
    }
  }
}
