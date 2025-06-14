package vn.shoestore.infrastructure.configuration.authen;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.shoestore.domain.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {
  private final String ACCESS_TOKEN_CLAIM = "access_token";

  @Value("${vn.shoe_store.secret}")
  private String jwtSecret;

  @Value("${vn.shoe_store.secret.jwt_expiration_ms}")
  private int jwtExpirationMs;

  @Value("${vn.shoe_store.secret.refresh_token_expiration_ms}")
  private int refreshTokenExpirationMs;

  public String generateJwtToken(User user, Map<String, Object> map) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setClaims(map)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  public String generateRefreshToken(User user, Map<String, Object> map) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setClaims(map)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + refreshTokenExpirationMs))
        .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser()
          .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
          .parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.warn("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.warn("JWT token was expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.warn("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
    }
    return false;
  }

  public String getUsernameByToken(String token) {
    Claims claims =
        Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody();
    return claims.get("username").toString();
  }
}
