package vn.shoestore.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class GeminiAIService {
  
  @Value("${google.gemini.api.key:}")
  private String apiKey;
  
  @Value("${google.gemini.model:gemini-2.0-flash}")
  private String model;
  
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/%s:generateContent";
  
  /**
   * Gửi message tới Gemini AI và nhận response
   */
  public String sendMessage(String userMessage, String systemPrompt) {
    try {
      // Nếu API key rỗng → trả về mock response
      if (apiKey == null || apiKey.isEmpty()) {
        log.warn("Gemini API key not configured. Returning mock response.");
        return getMockResponse(userMessage);
      }
      
      String url = String.format(GEMINI_API_URL, model) + "?key=" + apiKey;
      
      // Build request body
      Map<String, Object> requestBody = buildRequestBody(userMessage, systemPrompt);
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      
      HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
      
      // Call API
      ResponseEntity<String> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          request,
          String.class
      );
      
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return extractTextFromResponse(response.getBody());
      }
      
      log.error("Gemini API returned non-OK status: {}", response.getStatusCode());
      return getMockResponse(userMessage);
      
    } catch (Exception e) {
      log.error("Error calling Gemini API: {}", e.getMessage(), e);
      return getMockResponse(userMessage);
    }
  }
  
  /**
   * Gửi message với context (lịch sử hội thoại)
   */
  public String sendMessageWithContext(String userMessage, List<Map<String, String>> conversationHistory, String systemPrompt) {
    try {
      if (apiKey == null || apiKey.isEmpty()) {
        return getMockResponse(userMessage);
      }
      
      String url = String.format(GEMINI_API_URL, model) + "?key=" + apiKey;
      
      Map<String, Object> requestBody = buildRequestBodyWithHistory(userMessage, conversationHistory, systemPrompt);
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      
      HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
      
      ResponseEntity<String> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          request,
          String.class
      );
      
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return extractTextFromResponse(response.getBody());
      }
      
      return getMockResponse(userMessage);
      
    } catch (Exception e) {
      log.error("Error calling Gemini API with context: {}", e.getMessage());
      return getMockResponse(userMessage);
    }
  }
  
  private Map<String, Object> buildRequestBody(String userMessage, String systemPrompt) {
    Map<String, Object> body = new HashMap<>();
    
    // System instructions
    if (systemPrompt != null && !systemPrompt.isEmpty()) {
      Map<String, String> systemInstruction = new HashMap<>();
      Map<String, String> parts = new HashMap<>();
      parts.put("text", systemPrompt);
      systemInstruction.put("parts", objectMapper.convertValue(List.of(parts), String.class));
      body.put("system_instruction", systemInstruction);
    }
    
    // User message
    Map<String, Object> content = new HashMap<>();
    Map<String, String> part = new HashMap<>();
    part.put("text", userMessage);
    content.put("parts", List.of(part));
    
    body.put("contents", List.of(content));
    
    // Generation config
    Map<String, Object> config = new HashMap<>();
    config.put("temperature", 0.7);
    config.put("maxOutputTokens", 500);
    body.put("generationConfig", config);
    
    return body;
  }
  
  private Map<String, Object> buildRequestBodyWithHistory(String userMessage, List<Map<String, String>> history, String systemPrompt) {
    Map<String, Object> body = new HashMap<>();
    
    // System instructions
    if (systemPrompt != null && !systemPrompt.isEmpty()) {
      Map<String, Object> systemInstruction = new HashMap<>();
      Map<String, String> parts = new HashMap<>();
      parts.put("text", systemPrompt);
      systemInstruction.put("parts", List.of(parts));
      body.put("system_instruction", systemInstruction);
    }
    
    // Conversation history
    List<Map<String, Object>> contents = new java.util.ArrayList<>();
    
    // Add history
    if (history != null && !history.isEmpty()) {
      for (Map<String, String> msg : history) {
        Map<String, Object> content = new HashMap<>();
        content.put("role", msg.get("role")); // "user" or "model"
        Map<String, String> part = new HashMap<>();
        part.put("text", msg.get("text"));
        content.put("parts", List.of(part));
        contents.add(content);
      }
    }
    
    // Add current user message
    Map<String, Object> userContent = new HashMap<>();
    userContent.put("role", "user");
    Map<String, String> userPart = new HashMap<>();
    userPart.put("text", userMessage);
    userContent.put("parts", List.of(userPart));
    contents.add(userContent);
    
    body.put("contents", contents);
    
    // Generation config
    Map<String, Object> config = new HashMap<>();
    config.put("temperature", 0.7);
    config.put("maxOutputTokens", 500);
    body.put("generationConfig", config);
    
    return body;
  }
  
  private String extractTextFromResponse(String responseBody) {
    try {
      JsonNode root = objectMapper.readTree(responseBody);
      return root.path("candidates")
          .get(0)
          .path("content")
          .path("parts")
          .get(0)
          .path("text")
          .asText();
    } catch (Exception e) {
      log.error("Error parsing Gemini response: {}", e.getMessage());
      return "Xin lỗi, tôi gặp lỗi khi xử lý câu trả lời.";
    }
  }
  
  /**
   * Mock response khi API key chưa được cấu hình hoặc gặp lỗi
   */
  private String getMockResponse(String userMessage) {
    String lowerMessage = userMessage.toLowerCase();
    
    if (lowerMessage.contains("giày") || lowerMessage.contains("sản phẩm")) {
      return "Chúng tôi có nhiều mẫu giày đẹp! Bạn quan tâm đến loại giày nào? (Nike, Adidas, Puma...)";
    }
    
    if (lowerMessage.contains("size")) {
      return "Giày của chúng tôi có size từ 35 đến 45. Bạn cần size nào ạ?";
    }
    
    if (lowerMessage.contains("giá") || lowerMessage.contains("bao nhiêu")) {
      return "Giá sản phẩm dao động từ 500.000đ - 5.000.000đ tùy mẫu. Bạn muốn xem giày trong khoảng giá nào?";
    }
    
    if (lowerMessage.contains("giao hàng") || lowerMessage.contains("ship")) {
      return "Chúng tôi giao hàng toàn quốc trong 2-3 ngày. Miễn phí ship cho đơn từ 500k ạ!";
    }
    
    if (lowerMessage.contains("chào") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
      return "Xin chào! Tôi là trợ lý ảo của Shop Giày. Tôi có thể giúp gì cho bạn?";
    }
    
    return "Cảm ơn bạn đã liên hệ! Tôi có thể giúp bạn tìm sản phẩm, kiểm tra size, giá cả, hoặc thông tin giao hàng. Bạn cần hỗ trợ gì ạ?";
  }
}
