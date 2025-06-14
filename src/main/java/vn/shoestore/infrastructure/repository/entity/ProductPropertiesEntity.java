package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPropertiesEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_id")
  private Long productId;

  @Column(name = "size")
  private Integer size;

  @Column(name = "is_able")
  private Boolean isAble;
}
