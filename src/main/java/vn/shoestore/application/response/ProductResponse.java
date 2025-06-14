package vn.shoestore.application.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.domain.model.Category;
import vn.shoestore.domain.model.ProductAttachment;
import vn.shoestore.shared.dto.SizeAmountDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductResponse {
  private Long id;
  private String name;
  private String code;
  private String description;
  private Long price;
  @Builder.Default private Boolean isPromotion = false;
  private Long promotionId;
  private Double promotionPrice;
  private Brand brand;
  private Float percentDiscount;

  @Builder.Default private List<Category> categories = new ArrayList<>();
  @Builder.Default private List<ProductAttachment> images = new ArrayList<>();
  @Builder.Default private List<SizeAmountDTO> sizes = new ArrayList<>();
}
