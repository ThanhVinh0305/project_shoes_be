package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.UserRefreshTokenEntity;

import java.util.List;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
  List<UserRefreshTokenEntity> findAllByUserIdAndInvoke(Long userId, Boolean invoke);
}
