package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductPromotionEntity;

import java.util.List;

@Repository
public interface ProductPromotionRepository extends JpaRepository<ProductPromotionEntity, Long> {
  List<ProductPromotionEntity> findAllByPromotionId(Long promotionId);
  List<ProductPromotionEntity> findAllByProductIdIn(List<Long> productIds);
}
