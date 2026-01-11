package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ProductViewEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductViewEntity, Long> {
  
  Optional<ProductViewEntity> findByUserIdAndProductId(Long userId, Long productId);
  
  List<ProductViewEntity> findByUserId(Long userId);
  
  @Query(value = "SELECT pv FROM ProductViewEntity pv WHERE pv.userId = :userId ORDER BY pv.viewCount DESC LIMIT :limit")
  List<ProductViewEntity> findTopViewedByUserId(@Param("userId") Long userId, @Param("limit") int limit);
  
  @Query(value = "SELECT pv FROM ProductViewEntity pv WHERE pv.userId = :userId ORDER BY pv.lastViewedDate DESC LIMIT :limit")
  List<ProductViewEntity> findRecentlyViewedByUserId(@Param("userId") Long userId, @Param("limit") int limit);
  
  List<ProductViewEntity> findByUserIdAndLastViewedDateAfter(Long userId, LocalDateTime after);
  
  @Query(value = "SELECT COALESCE(SUM(pv.viewCount), 0) FROM ProductViewEntity pv WHERE pv.productId = :productId")
  Long getTotalViewCountByProductId(@Param("productId") Long productId);
  
  @Query(value = """
      SELECT pv.productId 
      FROM ProductViewEntity pv 
      GROUP BY pv.productId 
      ORDER BY SUM(pv.viewCount) DESC 
      LIMIT :limit
      """)
  List<Long> findTopViewedProducts(@Param("limit") int limit);
}
