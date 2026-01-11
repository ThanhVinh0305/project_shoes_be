package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ChatConversationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversationEntity, Long> {
  
  Optional<ChatConversationEntity> findBySessionId(String sessionId);
  
  List<ChatConversationEntity> findByUserId(Long userId);
  
  List<ChatConversationEntity> findByUserIdAndStatus(Long userId, String status);
  
  @Query("SELECT c FROM ChatConversationEntity c WHERE c.userId = :userId AND c.status = 'ACTIVE' ORDER BY c.lastMessageDate DESC")
  Optional<ChatConversationEntity> findActiveConversationByUserId(@Param("userId") Long userId);
  
  @Query("SELECT c FROM ChatConversationEntity c WHERE c.sessionId = :sessionId AND c.status = 'ACTIVE' ORDER BY c.lastMessageDate DESC")
  Optional<ChatConversationEntity> findActiveConversationBySessionId(@Param("sessionId") String sessionId);
}
