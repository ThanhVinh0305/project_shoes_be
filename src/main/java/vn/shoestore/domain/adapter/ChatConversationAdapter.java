package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ChatConversation;

import java.util.List;
import java.util.Optional;

public interface ChatConversationAdapter {
  
  ChatConversation save(ChatConversation conversation);
  
  Optional<ChatConversation> findById(Long id);
  
  Optional<ChatConversation> findBySessionId(String sessionId);
  
  List<ChatConversation> findByUserId(Long userId);
  
  Optional<ChatConversation> findActiveConversationByUserId(Long userId);
  
  Optional<ChatConversation> findActiveConversationBySessionId(String sessionId);
  
  void closeConversation(Long conversationId);
}
