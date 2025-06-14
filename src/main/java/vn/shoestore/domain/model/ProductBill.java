package vn.shoestore.domain.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductBill {
  private Long id;
  private Long productPropertiesId;
  private Integer amount;
  private Long price;
  private Double promotionPrice;
  private Long promotionId;
  private Long billId;

  public Double getTotalPrice() {
    if (this.promotionPrice != null) return amount * this.promotionPrice;
    return (double) (amount * price);
  }
}
