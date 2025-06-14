package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.shared.dto.ProductPropertiesAmountDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ImportProductRequest {
  private Long id;
  private String description;

  @NotNull(message = "Brand không được để rỗng")
  private Long brandId;

  @NotNull(message = "Sản phẩm nhập không được để rỗng")
  @NotEmpty(message = "Sản phẩm nhập không được để trống")
  private List<@Valid ProductPropertiesAmountDTO> productAmounts;

  @Builder.Default
  private Boolean isConfirm = false;
}
