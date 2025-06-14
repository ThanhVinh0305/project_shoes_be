package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductAmountEntity;

@Repository
public interface ProductAmountRepository extends JpaRepository<ProductAmountEntity, Long> {
  List<ProductAmountEntity> findAllByProductPropertiesIdIn(List<Long> productPropertiesIds);

  @Query(value = "select * from product_amounts for update", nativeQuery = true)
  List<ProductAmountEntity> findAllByProductPropertiesIdInForUpdate(
      List<Long> productPropertiesIds);

  @Query(value = "select sum(amount) from product_amounts", nativeQuery = true)
  Integer getSumAmount();
}
