package vn.shoestore.shared.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ProductPropertiesAmountDTO {
  @NotNull(message = "Mã sản phẩm không được để trống")
  private Long productId;

  @NotNull(message = "Số lượng không được để trống")
  private Integer amount;

  @NotNull(message = "Size không được để trống")
  private Integer size;

  @NotNull(message = "Giá nhập không được để trống")
  private Double importCost;
}
