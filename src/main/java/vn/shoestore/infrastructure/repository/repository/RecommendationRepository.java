package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.RecommendationEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
  
  List<RecommendationEntity> findByUserId(Long userId);
  
  @Query(value = """
      SELECT r FROM RecommendationEntity r 
      WHERE r.userId = :userId AND r.recommendationType = :type 
      ORDER BY r.recommendationScore DESC 
      LIMIT :limit
      """)
  List<RecommendationEntity> findByUserIdAndType(
      @Param("userId") Long userId, 
      @Param("type") String type, 
      @Param("limit") int limit
  );
  
  @Query(value = """
      SELECT r FROM RecommendationEntity r 
      WHERE r.userId = :userId 
      ORDER BY r.recommendationScore DESC 
      LIMIT :limit
      """)
  List<RecommendationEntity> findTopByUserId(@Param("userId") Long userId, @Param("limit") int limit);
  
  @Modifying
  @Query("UPDATE RecommendationEntity r SET r.isShown = true WHERE r.id = :id")
  void markAsShown(@Param("id") Long id);
  
  @Modifying
  @Query("UPDATE RecommendationEntity r SET r.isClicked = true WHERE r.id = :id")
  void markAsClicked(@Param("id") Long id);
  
  @Modifying
  @Query(value = """
      DELETE FROM RecommendationEntity r 
      WHERE r.userId = :userId 
      AND r.createdDate < :cutoffDate
      """)
  void deleteOldRecommendationsByUserId(@Param("userId") Long userId, @Param("cutoffDate") LocalDateTime cutoffDate);
  
  boolean existsByUserIdAndProductId(Long userId, Long productId);
}
