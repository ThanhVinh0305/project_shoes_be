package vn.shoestore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRefreshToken {
  private Long id;

  private Long userId;

  private String refreshToken;

  private Boolean invoke;
}
