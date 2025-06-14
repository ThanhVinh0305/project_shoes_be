package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_amounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAmountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_properties_id")
  private Long productPropertiesId;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "updated_date")
  private LocalDateTime updatedDate;
}
