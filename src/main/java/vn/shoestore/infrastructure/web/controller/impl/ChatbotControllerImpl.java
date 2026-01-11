package vn.shoestore.infrastructure.web.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.domain.model.ChatConversation;
import vn.shoestore.domain.model.ChatMessage;
import vn.shoestore.core.use_case.IChatbotUseCase;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.infrastructure.web.controller.IChatbotController;
import vn.shoestore.infrastructure.web.dto.request.SendMessageRequest;
import vn.shoestore.infrastructure.web.dto.request.StartConversationRequest;
import vn.shoestore.infrastructure.web.dto.response.ChatMessageResponse;
import vn.shoestore.infrastructure.web.dto.response.ConversationResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Log4j2
public class ChatbotControllerImpl implements IChatbotController {
  
  private final IChatbotUseCase chatbotUseCase;
  
  @Override
  @PostMapping("/start")
  public ConversationResponse startConversation(@Valid @RequestBody StartConversationRequest request) {
    log.info("Starting conversation for userId: {}, sessionId: {}", request.getUserId(), request.getSessionId());
    
    ChatConversation conversation = chatbotUseCase.startConversation(
        request.getUserId(),
        request.getSessionId()
    );
    
    return ModelMapperUtils.mapper(conversation, ConversationResponse.class);
  }
  
  @Override
  @PostMapping("/send")
  public ChatMessageResponse sendMessage(@Valid @RequestBody SendMessageRequest request) {
    log.info("Sending message to conversationId: {}", request.getConversationId());
    
    // Nếu chưa có conversationId → tạo mới
    Long conversationId = request.getConversationId();
    if (conversationId == null) {
      ChatConversation conversation = chatbotUseCase.startConversation(
          request.getUserId(),
          request.getSessionId()
      );
      conversationId = conversation.getId();
    }
    
    ChatMessage botResponse = chatbotUseCase.sendMessage(
        conversationId,
        request.getMessage(),
        request.getUserId()
    );
    
    return ModelMapperUtils.mapper(botResponse, ChatMessageResponse.class);
  }
  
  @Override
  @GetMapping("/conversation/{id}")
  public ConversationResponse getConversation(@PathVariable("id") Long conversationId) {
    log.info("Getting conversation history for id: {}", conversationId);
    
    List<ChatMessage> messages = chatbotUseCase.getConversationHistory(conversationId);
    
    ConversationResponse response = new ConversationResponse();
    response.setId(conversationId);
    
    // Map messages
    List<ChatMessageResponse> messageResponses = messages.stream()
        .map(msg -> ModelMapperUtils.mapper(msg, ChatMessageResponse.class))
        .collect(Collectors.toList());
    
    response.setMessages(messageResponses);
    
    return response;
  }
  
  @Override
  @PostMapping("/faq/{faqId}/helpful")
  public void markFAQAsHelpful(@PathVariable("faqId") Long faqId) {
    log.info("Marking FAQ as helpful: {}", faqId);
    chatbotUseCase.markFAQAsHelpful(faqId);
  }
  
  @Override
  @PostMapping("/conversation/{id}/close")
  public void closeConversation(@PathVariable("id") Long conversationId) {
    log.info("Closing conversation: {}", conversationId);
    chatbotUseCase.closeConversation(conversationId);
  }
}
