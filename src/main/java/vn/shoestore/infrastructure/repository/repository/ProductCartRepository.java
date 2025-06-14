package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductCartEntity;

import java.util.List;

@Repository
public interface ProductCartRepository extends JpaRepository<ProductCartEntity, Long> {
  List<ProductCartEntity> findAllByCartId(Long cartId);
}
