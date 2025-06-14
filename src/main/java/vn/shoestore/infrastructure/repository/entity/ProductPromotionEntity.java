package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "promotion_id", nullable = false)
  private Long promotionId;

  @Column(name = "created_date")
  private LocalDateTime createdDate;
}
