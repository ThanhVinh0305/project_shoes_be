package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IAuthController;
import vn.shoestore.application.request.LoginRefreshTokenRequest;
import vn.shoestore.application.request.LoginRequest;
import vn.shoestore.application.request.RegisterRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.LoginResponse;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.auth.IAuthUseCase;
import vn.shoestore.usecases.auth.ILoginRefreshTokenUseCase;

@Component
@RequiredArgsConstructor
public class AuthControllerImpl implements IAuthController {
  private final IAuthUseCase iAuthUseCase;
  private final ILoginRefreshTokenUseCase iLoginRefreshTokenUseCase;

  @Override
  public ResponseEntity<BaseResponse<LoginResponse>> login(LoginRequest request)
      throws IllegalAccessException {
    return ResponseFactory.success(iAuthUseCase.login(request));
  }

  @Override
  public ResponseEntity<BaseResponse<User>> register(RegisterRequest request) {
    return ResponseFactory.success(iAuthUseCase.register(request));
  }

  @Override
  public ResponseEntity<BaseResponse<LoginResponse>> refreshTokenLogin(
      LoginRefreshTokenRequest request) throws IllegalAccessException {
    return ResponseFactory.success(iLoginRefreshTokenUseCase.login(request));
  }
}
