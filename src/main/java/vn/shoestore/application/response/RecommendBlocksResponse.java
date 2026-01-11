package vn.shoestore.application.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecommendBlocksResponse {
  @Builder.Default private List<ProductResponse> guestSale = new ArrayList<>();
  @Builder.Default private List<ProductResponse> guestToday = new ArrayList<>();
  @Builder.Default private List<ProductResponse> userTopSearch = new ArrayList<>();
  @Builder.Default private List<ProductResponse> userTopViewed = new ArrayList<>();
  @Builder.Default private List<ProductResponse> viewerPreferences = new ArrayList<>();
}


