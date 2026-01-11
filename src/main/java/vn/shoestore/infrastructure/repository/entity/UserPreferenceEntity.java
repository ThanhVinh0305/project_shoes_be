package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id", nullable = false)
  private Long userId;
  
  @Column(name = "preference_type", nullable = false)
  private String preferenceType;
  
  @Column(name = "preference_value", nullable = false)
  private String preferenceValue;
  
  @Column(name = "preference_score", precision = 10, scale = 4)
  private BigDecimal preferenceScore;
  
  @Column(name = "last_updated")
  private LocalDateTime lastUpdated;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (lastUpdated == null) {
      lastUpdated = LocalDateTime.now();
    }
    if (preferenceScore == null) {
      preferenceScore = BigDecimal.ZERO;
    }
  }
  
  @PreUpdate
  protected void onUpdate() {
    lastUpdated = LocalDateTime.now();
  }
}
