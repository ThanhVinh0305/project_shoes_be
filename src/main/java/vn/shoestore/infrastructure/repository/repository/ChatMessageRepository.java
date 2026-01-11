package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ChatMessageEntity;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
  
  List<ChatMessageEntity> findByConversationId(Long conversationId);
  
  List<ChatMessageEntity> findByConversationIdOrderByCreatedDateAsc(Long conversationId);
  
  @Query("SELECT m FROM ChatMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdDate DESC")
  List<ChatMessageEntity> findRecentMessages(@Param("conversationId") Long conversationId);
  
  @Query("SELECT m FROM ChatMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdDate DESC LIMIT :limit")
  List<ChatMessageEntity> findRecentMessagesWithLimit(@Param("conversationId") Long conversationId, @Param("limit") int limit);
}
