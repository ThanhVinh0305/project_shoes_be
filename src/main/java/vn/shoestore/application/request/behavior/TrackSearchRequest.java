package vn.shoestore.application.request.behavior;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackSearchRequest {
  private String keyword;
  private String filters; // JSON string
  private Integer resultCount;
}
