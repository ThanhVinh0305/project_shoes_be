package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductPropertiesEntity;

@Repository
public interface ProductPropertiesRepository extends JpaRepository<ProductPropertiesEntity, Long> {
  List<ProductPropertiesEntity> findAllByProductIdInAndIsAble(
      List<Long> productIds, Boolean isAble);

  List<ProductPropertiesEntity> findAllByProductIdInAndSizeInAndIsAble(
      List<Long> productIds, List<Integer> sizes, Boolean isAble);

  List<ProductPropertiesEntity> findAllByIdIn(List<Long> ids);

  @Query(value = "select * from product_properties for update", nativeQuery = true)
  List<ProductPropertiesEntity> findAllByIdInForUpdate(List<Long> ids);
}
