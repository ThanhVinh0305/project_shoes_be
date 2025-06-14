package vn.shoestore.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptDecryptService {

  @Value("${vn.shoe_store.secret}")
  private String jwtSecret;

  public String decrypt(String message) {
    try {
      Mac sha256HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256HMAC.init(secretKeySpec);

      byte[] rawHmac = sha256HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String encrypt(String message) {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
      SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");

      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(secretKeySpec);

      byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

      return Base64.getEncoder().encodeToString(hmacBytes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
