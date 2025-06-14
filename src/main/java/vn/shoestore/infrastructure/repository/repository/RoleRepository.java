package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.RoleEntity;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  List<RoleEntity> findAllByIdIn(List<Long> ids);
}
