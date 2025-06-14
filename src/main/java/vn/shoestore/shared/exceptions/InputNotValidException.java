package vn.shoestore.shared.exceptions;

public class InputNotValidException extends RuntimeException {
  public InputNotValidException(String errorMsg) {
    super(errorMsg);
  }
}
