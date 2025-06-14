package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportTicketEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "description")
  private String description;

  @Column(name = "created_user_id")
  private Long createdUserId;

  @Column(name = "brand_id")
  private Long brandId;

  @Column(name = "status")
  private Integer status;

  @Column(name = "imported_time")
  private LocalDateTime importedTime;

  @Column(name = "imported_user_id")
  private Long importUserId;
}
