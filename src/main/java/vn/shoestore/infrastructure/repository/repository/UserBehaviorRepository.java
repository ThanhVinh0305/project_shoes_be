package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.UserBehaviorEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehaviorEntity, Long> {
  
  List<UserBehaviorEntity> findByUserId(Long userId);
  
  List<UserBehaviorEntity> findByUserIdAndBehaviorType(Long userId, String behaviorType);
  
  List<UserBehaviorEntity> findByUserIdAndCreatedDateBetween(
      Long userId, 
      LocalDateTime start, 
      LocalDateTime end
  );
  
  List<UserBehaviorEntity> findByProductId(Long productId);
  
  Long countByUserIdAndBehaviorType(Long userId, String behaviorType);
}
