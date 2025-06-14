package vn.shoestore.shared.exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import vn.shoestore.application.response.ExceptionResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class AppExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ExceptionResponse> unknownExceptionHandler(Exception exception) {
    log.error(" > ERROR: ", exception);
    return new ResponseEntity<>(
        ExceptionResponse.builder()
            .success(false)
            .message("Có lỗi xảy ra. Vui lòng thử lại")
            .build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {NotAuthorizedException.class, AccessDeniedException.class})
  public ResponseEntity<ExceptionResponse> notAuthorizedExceptionHandler(Exception exception) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().success(false).message(exception.getMessage()).build(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String errorMessage = error.getDefaultMessage();
              errors.put("message", errorMessage);
              errors.put("success", false);
            });
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(value = InputNotValidException.class)
  public ResponseEntity<ExceptionResponse> invalidInputHandler(Exception exception) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().success(false).message(exception.getMessage()).build(),
        HttpStatus.BAD_REQUEST);
  }
}
