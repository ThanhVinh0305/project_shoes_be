package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id", nullable = false)
  private Long userId;
  
  @Column(name = "product_id", nullable = false)
  private Long productId;
  
  @Column(name = "behavior_type", nullable = false)
  private String behaviorType;
  
  @Column(name = "behavior_data", columnDefinition = "JSON")
  private String behaviorData;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
  }
}
