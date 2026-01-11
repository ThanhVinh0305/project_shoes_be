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
public class ChatConversation {
  
  private Long id;
  
  private Long userId; // NULL nếu là guest
  
  private String sessionId; // UUID cho guest users
  
  private String status; // ACTIVE, CLOSED, ARCHIVED
  
  private LocalDateTime startDate;
  
  private LocalDateTime lastMessageDate;
  
  private LocalDateTime closedDate;
}
