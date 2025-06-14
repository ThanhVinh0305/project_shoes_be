package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "invoke")
  private Boolean invoke;
}
