package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.UserPreferenceEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, Long> {
  
  List<UserPreferenceEntity> findByUserId(Long userId);
  
  List<UserPreferenceEntity> findByUserIdAndPreferenceType(Long userId, String preferenceType);
  
  Optional<UserPreferenceEntity> findByUserIdAndPreferenceTypeAndPreferenceValue(
      Long userId, 
      String preferenceType, 
      String preferenceValue
  );
  
  @Query(value = """
      SELECT up FROM UserPreferenceEntity up 
      WHERE up.userId = :userId AND up.preferenceType = :type 
      ORDER BY up.preferenceScore DESC 
      LIMIT :limit
      """)
  List<UserPreferenceEntity> findTopByUserIdAndType(
      @Param("userId") Long userId, 
      @Param("type") String type, 
      @Param("limit") int limit
  );
  
  void deleteByUserId(Long userId);
}
