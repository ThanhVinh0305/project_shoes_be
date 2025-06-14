package vn.shoestore.usecases.auth;

import vn.shoestore.application.request.LoginRefreshTokenRequest;
import vn.shoestore.application.response.LoginResponse;

public interface ILoginRefreshTokenUseCase {
  LoginResponse login(LoginRefreshTokenRequest request) throws IllegalAccessException;
}
