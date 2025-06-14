package vn.shoestore.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.shoestore.shared.utils.ModelTransformUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
  private Long id;

  private String username;

  @JsonIgnore private String password;

  private String email;

  private String address;

  private String phoneNumber;

  private String firstName;

  private String lastName;

  @Builder.Default private Boolean active = true;

  @Builder.Default List<Role> roles = new ArrayList<>();

  public Boolean isAdmin() {
    if (Objects.isNull(roles) || roles.isEmpty()) return false;
    List<String> roleStrings = ModelTransformUtils.getAttribute(roles, Role::getName);
    return roleStrings.contains("ADMIN");
  }
}
