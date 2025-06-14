package vn.shoestore.shared.factory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.shoestore.application.response.BaseResponse;

public final class ResponseFactory {
  private ResponseFactory() {}

  public static <I> ResponseEntity<BaseResponse<I>> success(I data) {
    BaseResponse response =
        BaseResponse.builder().success(true).data(data).message("Thực hiện thành công").build();
    return ResponseEntity.ok(response);
  }

  public static ResponseEntity<BaseResponse> success() {
    BaseResponse response =
        BaseResponse.builder().success(true).message("Thực hiện thành công").build();
    return ResponseEntity.ok(response);
  }

  public static <I> ResponseEntity<BaseResponse<I>> success(I data, String message) {
    BaseResponse response =
        BaseResponse.builder().success(true).message(message).data(data).build();
    return ResponseEntity.ok(response);
  }

  public static <T> ResponseEntity<BaseResponse> error(HttpStatus httpStatus, String message) {
    BaseResponse response =
        BaseResponse.builder().success(false).message(message).data(null).build();
    return new ResponseEntity<>(response, httpStatus);
  }

  public static <T> ResponseEntity<BaseResponse> error(
      HttpStatus httpStatus, String message, T data) {
    BaseResponse response =
        BaseResponse.builder().success(false).message(message).data(data).build();
    return new ResponseEntity<>(response, httpStatus);
  }

  public static <I> ResponseEntity<BaseResponse<I>> errorWithStatusSuccess(I data, String message) {
    BaseResponse response =
        BaseResponse.builder().success(false).message(message).data(data).build();
    return ResponseEntity.ok(response);
  }

  public static <I> ResponseEntity<BaseResponse<I>> response(
      I data, boolean isSuccess, String message) {

    BaseResponse response =
        BaseResponse.builder().success(isSuccess).message(message).data(data).build();
    return ResponseEntity.ok(response);
  }

  public static <I> ResponseEntity<BaseResponse<I>> response(boolean isSuccess, String message) {
    BaseResponse response = BaseResponse.builder().success(isSuccess).message(message).build();
    return ResponseEntity.ok(response);
  }
}
