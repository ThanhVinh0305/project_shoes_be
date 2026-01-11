package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(name = "session_id", nullable = false)
  private String sessionId;
  
  @Column(name = "status")
  private String status;
  
  @Column(name = "started_date")
  private LocalDateTime startedDate;
  
  @Column(name = "last_message_date")
  private LocalDateTime lastMessageDate;
  
  @Column(name = "closed_date")
  private LocalDateTime closedDate;
  
  @PrePersist
  protected void onCreate() {
    if (startedDate == null) {
      startedDate = LocalDateTime.now();
    }
    if (lastMessageDate == null) {
      lastMessageDate = LocalDateTime.now();
    }
    if (status == null) {
      status = "ACTIVE";
    }
  }
  
  @PreUpdate
  protected void onUpdate() {
    lastMessageDate = LocalDateTime.now();
  }
}
