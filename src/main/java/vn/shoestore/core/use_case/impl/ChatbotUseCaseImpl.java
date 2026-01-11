package vn.shoestore.core.use_case.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.shoestore.domain.adapter.ChatAnalyticsAdapter;
import vn.shoestore.domain.adapter.ChatConversationAdapter;
import vn.shoestore.domain.adapter.ChatFAQAdapter;
import vn.shoestore.domain.adapter.ChatMessageAdapter;
import vn.shoestore.domain.model.*;
import vn.shoestore.core.use_case.IChatbotUseCase;
import vn.shoestore.infrastructure.service.GeminiAIService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatbotUseCaseImpl implements IChatbotUseCase {
  
  private final ChatConversationAdapter conversationAdapter;
  private final ChatMessageAdapter messageAdapter;
  private final ChatFAQAdapter faqAdapter;
  private final ChatAnalyticsAdapter analyticsAdapter;
  private final GeminiAIService geminiAIService;
  
  private static final String SYSTEM_PROMPT = """
      Bạn là trợ lý ảo thông minh của một cửa hàng giày dép trực tuyến.
      Nhiệm vụ của bạn:
      - Tư vấn sản phẩm giày dép (Nike, Adidas, Puma, Converse...)
      - Hỗ trợ thông tin về size giày (35-45)
      - Giải đáp về giá cả, khuyến mãi
      - Hướng dẫn đặt hàng, thanh toán
      - Thông tin giao hàng và chính sách đổi trả
      
      Phong cách giao tiếp:
      - Thân thiện, nhiệt tình
      - Trả lời ngắn gọn, dễ hiểu
      - Sử dụng emoji phù hợp
      - Luôn đề xuất sản phẩm cụ thể
      """;
  
  @Override
  public ChatConversation startConversation(Long userId, String sessionId) {
    // Kiểm tra xem đã có conversation đang active chưa
    Optional<ChatConversation> existing = userId != null 
        ? conversationAdapter.findActiveConversationByUserId(userId)
        : conversationAdapter.findActiveConversationBySessionId(sessionId);
    
    if (existing.isPresent()) {
      log.info("Found existing active conversation: {}", existing.get().getId());
      return existing.get();
    }
    
    // Tạo conversation mới
    ChatConversation conversation = ChatConversation.builder()
        .userId(userId)
        .sessionId(sessionId != null ? sessionId : UUID.randomUUID().toString())
        .status("ACTIVE")
        .startDate(LocalDateTime.now())
        .lastMessageDate(LocalDateTime.now())
        .build();
    
    ChatConversation saved = conversationAdapter.save(conversation);
    log.info("Started new conversation: {}", saved.getId());
    
    // Track event
    trackEvent("CONVERSATION_STARTED", saved.getId(), userId);
    
    return saved;
  }
  
  @Override
  public ChatMessage sendMessage(Long conversationId, String userMessage, Long userId) {
    // Lưu user message
    ChatMessage userMsg = ChatMessage.builder()
        .conversationId(conversationId)
        .senderType("USER")
        .messageType("TEXT")
        .messageText(userMessage)
        .createdDate(LocalDateTime.now())
        .build();
    messageAdapter.save(userMsg);
    
    // Track event
    trackEvent("MESSAGE_SENT", conversationId, userId);
    
    // 1. Thử tìm FAQ match
    String botResponse = findFAQMatch(userMessage);
    
    // 2. Nếu không có FAQ match → gọi Gemini AI
    if (botResponse == null) {
      botResponse = generateAIResponse(conversationId, userMessage);
    }
    
    // Lưu bot response
    ChatMessage botMsg = ChatMessage.builder()
        .conversationId(conversationId)
        .senderType("BOT")
        .messageType("TEXT")
        .messageText(botResponse)
        .createdDate(LocalDateTime.now())
        .build();
    ChatMessage savedBotMsg = messageAdapter.save(botMsg);
    
    // Track event
    trackEvent("BOT_RESPONDED", conversationId, userId);
    
    return savedBotMsg;
  }
  
  @Override
  public List<ChatMessage> getConversationHistory(Long conversationId) {
    return messageAdapter.findByConversationId(conversationId);
  }
  
  @Override
  public void closeConversation(Long conversationId) {
    conversationAdapter.closeConversation(conversationId);
    log.info("Closed conversation: {}", conversationId);
  }
  
  @Override
  public void markFAQAsHelpful(Long faqId) {
    faqAdapter.incrementHelpfulCount(faqId);
    trackEvent("FAQ_HELPFUL", null, null);
  }
  
  /**
   * Tìm kiếm FAQ phù hợp với câu hỏi
   */
  private String findFAQMatch(String userMessage) {
    List<ChatFAQ> faqs = faqAdapter.searchByQuestion(userMessage);
    
    if (!faqs.isEmpty()) {
      ChatFAQ bestMatch = faqs.get(0);
      
      // Tăng view count
      faqAdapter.incrementViewCount(bestMatch.getId());
      
      log.info("FAQ matched: {} for question: {}", bestMatch.getQuestion(), userMessage);
      trackEvent("FAQ_MATCHED", null, null);
      
      return bestMatch.getAnswer();
    }
    
    return null;
  }
  
  /**
   * Generate AI response sử dụng Gemini
   */
  private String generateAIResponse(Long conversationId, String userMessage) {
    try {
      // Lấy lịch sử hội thoại gần đây (10 messages cuối)
      List<ChatMessage> recentMessages = messageAdapter.findRecentMessages(conversationId, 10);
      
      // Build conversation history cho Gemini
      List<Map<String, String>> history = recentMessages.stream()
          .map(msg -> {
            Map<String, String> m = new HashMap<>();
            m.put("role", msg.getSenderType().equals("USER") ? "user" : "model");
            m.put("text", msg.getMessageText());
            return m;
          })
          .collect(Collectors.toList());
      
      // Gọi Gemini AI
      String aiResponse = geminiAIService.sendMessageWithContext(
          userMessage, 
          history, 
          SYSTEM_PROMPT
      );
      
      log.info("AI response generated for message: {}", userMessage);
      trackEvent("AI_RESPONSE_GENERATED", conversationId, null);
      
      return aiResponse;
      
    } catch (Exception e) {
      log.error("Error generating AI response: {}", e.getMessage(), e);
      return "Xin lỗi, tôi đang gặp sự cố. Vui lòng thử lại sau hoặc liên hệ hotline: 1900-xxxx";
    }
  }
  
  /**
   * Track analytics event
   */
  private void trackEvent(String eventType, Long conversationId, Long userId) {
    try {
      ChatAnalytics analytics = ChatAnalytics.builder()
          .conversationId(conversationId)
          .userId(userId)
          .eventType(eventType)
          .eventDate(LocalDateTime.now())
          .build();
      
      analyticsAdapter.trackEvent(analytics);
    } catch (Exception e) {
      log.error("Error tracking event: {}", e.getMessage());
    }
  }
}
