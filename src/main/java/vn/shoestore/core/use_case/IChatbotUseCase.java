package vn.shoestore.core.use_case;

import vn.shoestore.domain.model.ChatConversation;
import vn.shoestore.domain.model.ChatMessage;

import java.util.List;

public interface IChatbotUseCase {
  
  /**
   * Bắt đầu một cuộc hội thoại mới
   */
  ChatConversation startConversation(Long userId, String sessionId);
  
  /**
   * Gửi tin nhắn và nhận phản hồi từ chatbot
   */
  ChatMessage sendMessage(Long conversationId, String userMessage, Long userId);
  
  /**
   * Lấy lịch sử hội thoại
   */
  List<ChatMessage> getConversationHistory(Long conversationId);
  
  /**
   * Đóng cuộc hội thoại
   */
  void closeConversation(Long conversationId);
  
  /**
   * Đánh dấu FAQ là hữu ích
   */
  void markFAQAsHelpful(Long faqId);
}
