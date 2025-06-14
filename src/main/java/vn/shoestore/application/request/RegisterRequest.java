package vn.shoestore.application.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequest {
  @NotNull(message = "username cannot be null")
  private String username;

  @NotNull(message = "password cannot be null")
  private String password;

  @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = " Sai định dạng số email")
  @NotNull(message = "email cannot be null")
  private String email;

  @NotNull(message = "first_name cannot be null")
  private String firstName;

  @NotNull(message = "last_name cannot be null")
  private String lastName;

  @Pattern(
      regexp =
          "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$",
      message = " Sai định dạng số điện thoại")
  @NotNull(message = "phone_number cannot be null")
  private String phoneNumber;

  private String address;
}
