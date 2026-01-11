package vn.shoestore.application.request.behavior;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackClickRequest {
  private Long productId;
  private String source; // "recommendation", "search", "similar", etc.
}
