package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductCommentAttachmentEntity;

@Repository
public interface ProductCommentAttachmentRepository
    extends JpaRepository<ProductCommentAttachmentEntity, Long> {
  List<ProductCommentAttachmentEntity> findAllByProductCommentIdIn(List<Long> productCommentIds);
}
