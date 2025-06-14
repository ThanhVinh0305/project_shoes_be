package vn.shoestore.application.request;

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
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BrandRequest {
  private Long id;

  @NotNull(message = "name không được để rỗng")
  private String name;

  @NotNull(message = "phone_number không được để rỗng")
  private String phoneNumber;

  @NotNull(message = "address không được để rỗng")
  private String address;
}
