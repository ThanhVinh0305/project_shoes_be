package vn.shoestore.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

  /** The result of the response, true if successful and false otherwise */
  private boolean success;

  /** Custom response code */

  /** Response data */
  private T data;

  /** Response message */
  private String message;

  public static <T> BaseResponse<T> success(String message, T data) {
    return BaseResponse.<T>builder().success(true).message(message).data(data).build();
  }

  public static <T> BaseResponse<T> success(T data) {
    return BaseResponse.<T>builder()
        .success(true)
        .message("Thực hiện thành công")
        .data(data)
        .build();
  }

  public static <T> BaseResponse<T> fail(String message) {
    return BaseResponse.<T>builder().success(false).message(message).build();
  }
}
