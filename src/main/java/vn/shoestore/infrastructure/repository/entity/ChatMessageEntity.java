package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "conversation_id", nullable = false)
  private Long conversationId;
  
  @Column(name = "sender_type", nullable = false)
  private String senderType;
  
  @Column(name = "sender_id")
  private Long senderId;
  
  @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
  private String messageText;
  
  @Column(name = "message_type")
  private String messageType;
  
  @Column(name = "metadata", columnDefinition = "JSON")
  private String metadata;
  
  @Column(name = "is_read")
  private Boolean isRead;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (isRead == null) {
      isRead = false;
    }
    if (messageType == null) {
      messageType = "TEXT";
    }
  }
}
