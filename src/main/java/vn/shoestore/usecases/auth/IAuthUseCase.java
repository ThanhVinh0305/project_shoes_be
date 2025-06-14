package vn.shoestore.usecases.auth;

import vn.shoestore.application.request.LoginRequest;
import vn.shoestore.application.request.RegisterRequest;
import vn.shoestore.application.response.LoginResponse;
import vn.shoestore.domain.model.User;

public interface IAuthUseCase {
  LoginResponse login(LoginRequest request) throws IllegalAccessException;

  User register(RegisterRequest request);
}
