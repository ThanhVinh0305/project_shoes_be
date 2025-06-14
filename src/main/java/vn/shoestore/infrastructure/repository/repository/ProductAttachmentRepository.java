package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductAttachmentEntity;

import java.util.List;

@Repository
public interface ProductAttachmentRepository extends JpaRepository<ProductAttachmentEntity, Long> {
  List<ProductAttachmentEntity> findAllByProductIdIn(List<Long> productIds);
}
