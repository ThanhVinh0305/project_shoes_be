package vn.shoestore.shared.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.domain.model.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ProductImportDTO {
  private Long productId;
  private Long productPropertiesId;
  private Integer size;
  private Integer amount;
  private String name;
  private Double importCost;
  private ProductResponse product;
}
