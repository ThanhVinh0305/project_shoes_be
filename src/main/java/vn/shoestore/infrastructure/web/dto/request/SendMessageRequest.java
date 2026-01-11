package vn.shoestore.infrastructure.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
  
  private Long conversationId;
  
  @NotBlank(message = "Message cannot be empty")
  private String message;
  
  private Long userId; // Nullable for guest users
  
  private String sessionId; // Required for guest users
}
