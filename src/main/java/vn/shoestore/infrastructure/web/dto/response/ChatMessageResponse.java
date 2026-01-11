package vn.shoestore.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
  
  private Long id;
  private Long conversationId;
  private String senderType; // USER, BOT, ADMIN
  private String messageType; // TEXT, IMAGE, PRODUCT_LINK
  private String messageText;
  private LocalDateTime createdDate;
  private String metadata; // JSON string
}
