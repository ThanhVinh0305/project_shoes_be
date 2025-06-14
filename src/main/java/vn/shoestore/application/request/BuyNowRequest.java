package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.shared.dto.BuyNowProductDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BuyNowRequest {
  @NotNull(message = "Địa chỉ không được để rỗng")
  private String address;

  @NotNull(message = "Số điện thoại không được để rỗng")
  private String phoneNumber;

  @NotNull(message = "Sản phẩm không được để rỗng")
  @NotEmpty(message = "Sản phẩm không được để trống")
  private List<@Valid BuyNowProductDTO> products;
}
