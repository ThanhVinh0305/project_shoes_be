package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "address", length = 64000)
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "is_online_transaction")
  private Boolean isOnlineTransaction;

  @Column(name = "status")
  private Integer status;

  @Column(name = "total")
  private Double total;
}
