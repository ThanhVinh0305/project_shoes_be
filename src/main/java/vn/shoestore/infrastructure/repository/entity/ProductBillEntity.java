package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBillEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "bill_id")
  private Long billId;

  @Column(name = "product_properties_id")
  private Long productPropertiesId;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "price")
  private Double price;

  @Column(name = "promotion_price")
  private Double promotionPrice;

  @Column(name = "promotion_id")
  private Long promotionId;
}
