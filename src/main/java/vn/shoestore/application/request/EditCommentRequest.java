package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class EditCommentRequest {
  @NotNull(message = "ID không được để trống")
  private Long id;

  @Min(value = 0, message = "Số sao không hợp lệ")
  @Max(value = 5, message = "Số sao không hợp lệ")
  @NotNull(message = "Đánh giá sao không được để trống")
  private Integer star;

  private List<String> attachments;

  @NotNull(message = "Bình luận không được để trống")
  private String comment;
}
