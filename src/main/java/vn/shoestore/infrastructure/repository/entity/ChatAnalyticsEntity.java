package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatAnalyticsEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "conversation_id")
  private Long conversationId;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(name = "event_type", nullable = false)
  private String eventType;
  
  @Column(name = "event_data", columnDefinition = "JSON")
  private String eventData;
  
  @Column(name = "satisfaction_rating")
  private Integer satisfactionRating;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
  }
}
