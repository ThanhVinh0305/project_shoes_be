package vn.shoestore.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
  private Long id;

  private String name;

  private String code;

  private String description;

  private Long price;

  private String thumbnailImg;
}
