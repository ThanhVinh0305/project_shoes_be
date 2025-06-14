package vn.shoestore.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "import_ticket_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportTicketProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_properties_id")
  private Long productPropertiesId;

  @Column(name = "ticket_id")
  private Long ticketId;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "import_cost")
  private Double importCost;
}
