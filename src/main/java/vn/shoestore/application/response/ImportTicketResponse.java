package vn.shoestore.application.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.dto.ProductImportDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ImportTicketResponse {
  private Long id;
  private String description;
  private Integer status;

  private Brand brand;

  private User createdUser;
  private User importUser;

  private List<ProductImportDTO> products;

  private LocalDateTime createdDate;
  private LocalDateTime importedTime;
}
