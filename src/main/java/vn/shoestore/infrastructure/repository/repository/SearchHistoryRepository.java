package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.SearchHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {
  
  List<SearchHistoryEntity> findByUserId(Long userId);
  
  @Query(value = "SELECT sh FROM SearchHistoryEntity sh WHERE sh.userId = :userId ORDER BY sh.createdDate DESC LIMIT :limit")
  List<SearchHistoryEntity> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
  
  List<SearchHistoryEntity> findBySearchKeyword(String keyword);
  
  @Query(value = """
      SELECT sh.searchKeyword 
      FROM SearchHistoryEntity sh 
      GROUP BY sh.searchKeyword 
      ORDER BY COUNT(sh.searchKeyword) DESC 
      LIMIT :limit
      """)
  List<String> findTopKeywords(@Param("limit") int limit);
  
  @Query(value = """
      SELECT sh.searchKeyword 
      FROM SearchHistoryEntity sh 
      WHERE sh.userId = :userId 
      GROUP BY sh.searchKeyword 
      ORDER BY COUNT(sh.searchKeyword) DESC 
      LIMIT :limit
      """)
  List<String> findTopKeywordsByUserId(@Param("userId") Long userId, @Param("limit") int limit);
  
  Long countByUserIdAndCreatedDateAfter(Long userId, LocalDateTime after);
}
