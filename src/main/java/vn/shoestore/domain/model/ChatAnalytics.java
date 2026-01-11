package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAnalytics {
  
  private Long id;
  
  private Long conversationId;
  
  private Long userId;
  
  private String eventType; // CONVERSATION_STARTED, MESSAGE_SENT, FAQ_MATCHED, etc.
  
  private String eventData; // JSON
  
  private Integer satisfactionRating; // 1-5
  
  private LocalDateTime createdDate;
  private LocalDateTime eventDate;
}
