package vn.shoestore.shared.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.application.response.ProductResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ProductBilDTO {
  private Long productId;
  private Long productPropertiesId;
  private Integer size;
  private Double price;
  private Integer amount;
  private ProductResponse product;
}
