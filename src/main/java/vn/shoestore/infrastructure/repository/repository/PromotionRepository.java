package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.PromotionEntity;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

  @Query(
      value =
          """
        select * from promotions
          where (:keyword is null or promotion_name like concat('%' , :keyword, '%'))
    """,
      nativeQuery = true)
  Page<PromotionEntity> findAllByKeyword(String keyword, Pageable pageable);

  List<PromotionEntity> findAllByIdIn(List<Long> ids);
}
