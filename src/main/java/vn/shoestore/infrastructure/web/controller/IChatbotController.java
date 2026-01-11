package vn.shoestore.infrastructure.web.controller;

import vn.shoestore.infrastructure.web.dto.request.SendMessageRequest;
import vn.shoestore.infrastructure.web.dto.request.StartConversationRequest;
import vn.shoestore.infrastructure.web.dto.response.ChatMessageResponse;
import vn.shoestore.infrastructure.web.dto.response.ConversationResponse;

import java.util.List;

public interface IChatbotController {
  
  /**
   * Bắt đầu cuộc hội thoại mới
   * POST /api/v1/chat/start
   */
  ConversationResponse startConversation(StartConversationRequest request);
  
  /**
   * Gửi tin nhắn và nhận phản hồi
   * POST /api/v1/chat/send
   */
  ChatMessageResponse sendMessage(SendMessageRequest request);
  
  /**
   * Lấy lịch sử hội thoại
   * GET /api/v1/chat/conversation/{id}
   */
  ConversationResponse getConversation(Long conversationId);
  
  /**
   * Đánh dấu FAQ là hữu ích
   * POST /api/v1/chat/faq/{faqId}/helpful
   */
  void markFAQAsHelpful(Long faqId);
  
  /**
   * Đóng cuộc hội thoại
   * POST /api/v1/chat/conversation/{id}/close
   */
  void closeConversation(Long conversationId);
}
