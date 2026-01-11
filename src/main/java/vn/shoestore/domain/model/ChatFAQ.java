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
public class ChatFAQ {
  
  private Long id;
  
  private String question;
  
  private String answer;
  
  private String category; // SHIPPING, RETURNS, PRODUCTS, PAYMENT
  
  private String keywords; // JSON array
  
  private Integer priority;
  
  private Boolean isActive;
  
  private Integer viewCount;
  
  private Integer helpfulCount;
  
  private LocalDateTime createdDate;
  
  private LocalDateTime updatedDate;
}
