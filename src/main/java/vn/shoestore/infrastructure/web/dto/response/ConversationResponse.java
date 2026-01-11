package vn.shoestore.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
  
  private Long id;
  private Long userId;
  private String sessionId;
  private String status; // ACTIVE, CLOSED
  private LocalDateTime startDate;
  private LocalDateTime lastMessageDate;
  private LocalDateTime closedDate;
  
  private List<ChatMessageResponse> messages; // Optional - lịch sử tin nhắn
}
