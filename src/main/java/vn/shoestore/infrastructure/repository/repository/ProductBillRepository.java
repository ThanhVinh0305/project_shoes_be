package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductBillEntity;

@Repository
public interface ProductBillRepository extends JpaRepository<ProductBillEntity, Long> {
  List<ProductBillEntity> findAllByBillIdIn(List<Long> billIds);
}
