package vn.shoestore.application.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class PromotionRequest {
  private Long id;

  @NotNull(message = "Phần trăm giảm giá không được để trống")
  private Float percentDiscount;

  @NotNull(message = "Tên chương trình khuyến mãi không được để trống")
  private String promotionName;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
  @NotNull(message = "Ngày bắt đầu không được để trống")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
  @NotNull(message = "Ngày kết thúc không được để trống")
  private LocalDateTime endDate;

  @Builder.Default private List<Long> productIds = new ArrayList<>();
}
