package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IAuthUserController;
import vn.shoestore.application.request.ChangePasswordRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.auth.IChangePasswordUseCase;
import vn.shoestore.usecases.auth.ILogOutUseCase;

@Component
@RequiredArgsConstructor
public class AuthUserControllerImpl implements IAuthUserController {
  private final IChangePasswordUseCase iChangePasswordUseCase;
  private final ILogOutUseCase iLogOutUseCase;

  @Override
  public ResponseEntity<BaseResponse> changePassword(ChangePasswordRequest request) {
    iChangePasswordUseCase.changePassword(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> logout() {
    iLogOutUseCase.logout();
    return ResponseFactory.success();
  }
}
