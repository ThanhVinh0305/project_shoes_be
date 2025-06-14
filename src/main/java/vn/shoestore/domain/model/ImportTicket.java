package vn.shoestore.domain.model;

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
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportTicket {
  private Long id;
  private LocalDateTime createdDate;
  private String description;
  private Long createdUserId;
  private Long brandId;
  private Integer status;
  private Long importUserId;
  private LocalDateTime importedTime;
}
