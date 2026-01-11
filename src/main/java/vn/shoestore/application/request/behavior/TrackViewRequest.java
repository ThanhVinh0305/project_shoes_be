package vn.shoestore.application.request.behavior;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackViewRequest {
  private Long productId;
  private Integer viewDuration; // in seconds
}
