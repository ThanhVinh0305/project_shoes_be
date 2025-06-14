package vn.shoestore.application.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.shared.dto.ProductCartDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class CartResponse {
  private Long id;
  private Long userId;
  @Builder.Default private Integer amount = 0;

  @Builder.Default private Double totalPrice = 0d;

  @Builder.Default private List<ProductCartDTO> products = new ArrayList<>();
}
