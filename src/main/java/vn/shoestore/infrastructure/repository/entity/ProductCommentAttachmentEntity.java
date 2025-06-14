package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_comment_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCommentAttachmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_comment_id", nullable = false)
  private Long productCommentId;

  @Column(name = "attachment", length = 1000)
  private String attachment;
}
