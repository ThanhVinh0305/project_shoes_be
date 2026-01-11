package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ChatMessage;

import java.util.List;

public interface ChatMessageAdapter {
  
  ChatMessage save(ChatMessage message);
  
  List<ChatMessage> findByConversationId(Long conversationId);
  
  List<ChatMessage> findRecentMessages(Long conversationId, int limit);
}
