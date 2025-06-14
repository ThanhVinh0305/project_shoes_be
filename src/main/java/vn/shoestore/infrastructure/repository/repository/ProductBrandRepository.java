package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductBrandEntity;

import java.util.List;

@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrandEntity, Long> {
  List<ProductBrandEntity> findAllByProductIdIn(List<Long> productIds);
}
