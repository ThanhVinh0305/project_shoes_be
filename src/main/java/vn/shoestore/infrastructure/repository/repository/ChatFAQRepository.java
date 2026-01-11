package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ChatFAQEntity;

import java.util.List;

@Repository
public interface ChatFAQRepository extends JpaRepository<ChatFAQEntity, Long> {
  
  List<ChatFAQEntity> findByIsActiveTrue();
  
  List<ChatFAQEntity> findByCategory(String category);
  
  List<ChatFAQEntity> findByIsActiveTrueOrderByPriorityDescViewCountDesc();
  
  @Query("SELECT f FROM ChatFAQEntity f WHERE f.isActive = true AND LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<ChatFAQEntity> searchByQuestion(@Param("keyword") String keyword);
}
