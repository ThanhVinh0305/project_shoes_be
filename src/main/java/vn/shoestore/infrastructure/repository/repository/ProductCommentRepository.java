package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductCommentEntity;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductCommentEntity, Long> {
  List<ProductCommentEntity> findAllByProductId(Long productId);

  ProductCommentEntity findAllByProductIdAndUserId(Long productId , Long userId);
}
