package vn.shoestore.domain.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Bill {
  private Long id;
  private Long userId;
  private Long createdBy;
  private LocalDateTime createdDate;
  private String address;
  private String phoneNumber;
  private Boolean isOnlineTransaction;
  private Integer status;
  private Double total;
}
