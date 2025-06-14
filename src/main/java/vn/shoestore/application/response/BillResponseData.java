package vn.shoestore.application.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.shared.dto.ProductBilDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class BillResponseData {
  private Long id;
  private LocalDateTime createdDate;
  private Integer status;
  private String address;
  private String phoneNumber;
  private Double total;

  @Builder.Default private List<ProductBilDTO> products = new ArrayList<>();
}
