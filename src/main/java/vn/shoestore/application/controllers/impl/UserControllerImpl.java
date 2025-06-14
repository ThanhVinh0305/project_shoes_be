package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IUserController;
import vn.shoestore.application.request.FindUserRequest;
import vn.shoestore.application.request.UpdateUserRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.UserResponse;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.user.IUserUseCase;

@Component
@RequiredArgsConstructor
public class UserControllerImpl implements IUserController {
  private final IUserUseCase iUserUseCase;

  @Override
  public ResponseEntity<BaseResponse<UserResponse>> findAllUser(FindUserRequest request) {
    return ResponseFactory.success(iUserUseCase.getUserByConditions(request));
  }

  @Override
  public ResponseEntity<BaseResponse<User>> findUserById(Long id) {
    return ResponseFactory.success(iUserUseCase.getUserById(id));
  }

  @Override
  public ResponseEntity<BaseResponse> delete(Long id) {
    iUserUseCase.deleteUser(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> active(Long id) {
    iUserUseCase.activeUser(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> update(UpdateUserRequest request) {
    iUserUseCase.updateUser(request);
    return ResponseFactory.success();
  }
}
