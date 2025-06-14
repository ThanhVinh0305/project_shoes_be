package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class CreateBillRequest {
  @NotNull(message = "Địa chỉ không được để rỗng")
  private String address;

  @NotNull(message = "Số điện thoại không được để rỗng")
  private String phoneNumber;

  @NotNull(message = "Sản phẩm không được để rỗng")
  @NotEmpty(message = "Sản phẩm không được để trống")
  private List<Long> productPropertiesIds = new ArrayList<>();
}
