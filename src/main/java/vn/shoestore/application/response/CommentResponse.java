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

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class CommentResponse {
  private Long id;

  private Long userId;

  private String username;

  private String fullName;

  private Long productId;

  private Integer star;

  private LocalDateTime createdDate;

  private LocalDateTime updatedDate;

  private String comment;

  @Builder.Default private List<String> attachments = new ArrayList<>();

  @Builder.Default private Boolean canEdit = false;
}
