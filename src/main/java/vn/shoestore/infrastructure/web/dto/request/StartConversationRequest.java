package vn.shoestore.infrastructure.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartConversationRequest {
  
  private Long userId; // Nullable for guest users
  
  private String sessionId; // Required for guest users
}
