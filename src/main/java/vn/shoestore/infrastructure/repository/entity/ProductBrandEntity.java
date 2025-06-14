package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBrandEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_id")
  private Long productId;

  @Column(name = "brand_id")
  private Long brandId;
}
