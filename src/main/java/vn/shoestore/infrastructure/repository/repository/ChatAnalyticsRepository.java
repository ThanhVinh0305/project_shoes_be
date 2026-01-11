package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ChatAnalyticsEntity;

import java.util.List;

@Repository
public interface ChatAnalyticsRepository extends JpaRepository<ChatAnalyticsEntity, Long> {
  
  List<ChatAnalyticsEntity> findByConversationId(Long conversationId);
  
  List<ChatAnalyticsEntity> findByUserId(Long userId);
  
  List<ChatAnalyticsEntity> findByEventType(String eventType);
}
