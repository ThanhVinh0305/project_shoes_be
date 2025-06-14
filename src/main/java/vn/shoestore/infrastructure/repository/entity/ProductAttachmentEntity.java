package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttachmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_id")
  private Long productId;

  @Column(name = "attachment")
  private String attachment;
}
