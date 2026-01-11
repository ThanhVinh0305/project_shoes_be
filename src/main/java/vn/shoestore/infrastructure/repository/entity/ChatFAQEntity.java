package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_faq")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatFAQEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  @Column(name = "question", nullable = false, columnDefinition = "TEXT")
  private String question;
  
  @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
  private String answer;
  
  @Column(name = "category")
  private String category;
  
  @Column(name = "keywords", columnDefinition = "JSON")
  private String keywords;
  
  @Column(name = "priority")
  private Integer priority;
  
  @Column(name = "is_active")
  private Boolean isActive;
  
  @Column(name = "view_count")
  private Integer viewCount;
  
  @Column(name = "helpful_count")
  private Integer helpfulCount;
  
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;
  
  @PrePersist
  protected void onCreate() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
    if (updatedDate == null) {
      updatedDate = LocalDateTime.now();
    }
    if (isActive == null) {
      isActive = true;
    }
    if (priority == null) {
      priority = 0;
    }
    if (viewCount == null) {
      viewCount = 0;
    }
    if (helpfulCount == null) {
      helpfulCount = 0;
    }
  }
  
  @PreUpdate
  protected void onUpdate() {
    updatedDate = LocalDateTime.now();
  }
}
