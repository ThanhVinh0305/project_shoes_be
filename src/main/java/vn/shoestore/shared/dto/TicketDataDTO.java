package vn.shoestore.shared.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class TicketDataDTO {
  private Long id;
  private String createdBy;
  private String confirmBy;
  private Integer status;
  private Long importUserId;
  private Long createdUserId;
  private LocalDateTime importedTime;
  private LocalDateTime createdDate;
}
