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
public class ChatMessage {
  
  private Long id;
  
  private Long conversationId;
  
  private String senderType; // USER, BOT, ADMIN
  
  private Long senderId; // user_id nếu sender = USER/ADMIN
  
  private String messageText;
  
  private String messageType; // TEXT, IMAGE, PRODUCT_LINK, QUICK_REPLY, BUTTON
  
  private String metadata; // JSON string
  
  private Boolean isRead;
  
  private LocalDateTime createdDate;
}
