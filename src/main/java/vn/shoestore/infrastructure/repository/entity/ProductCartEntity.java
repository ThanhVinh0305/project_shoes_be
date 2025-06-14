package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "cart_id", nullable = false)
  private Long cartId;

  @Column(name = "product_properties_id")
  private Long productPropertiesId;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "added_date")
  private LocalDateTime addedDate;
}
