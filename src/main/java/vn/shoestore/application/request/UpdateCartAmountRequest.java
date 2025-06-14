package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
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
public class UpdateCartAmountRequest {
  @NotNull(message = "Mã sản phẩm không được để rỗng")
  private Long productPropertiesId;

  @Min(value = 1, message = "Số lượng sản phẩm không được nhỏ hơn 1")
  private Integer amount;
}
