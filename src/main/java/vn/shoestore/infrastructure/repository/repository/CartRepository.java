package vn.shoestore.infrastructure.repository.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
  Optional<CartEntity> findByUserId(Long userId);
}
