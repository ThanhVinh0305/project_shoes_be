package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);

  List<UserEntity> findAllByIdIn(List<Long> ids);

  @Query(
      value =
          """
                      select u.* from users u
                      left join user_roles ur on u.id = ur.user_id
                      where ur.role_id is null or ur.role_id = 2
                  """,
      nativeQuery = true)
  Page<UserEntity> findAllUserNotAdmin(Pageable pageable);
}
