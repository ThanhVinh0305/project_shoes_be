package vn.shoestore.usecases.logic.user;

import vn.shoestore.application.request.FindUserRequest;
import vn.shoestore.application.request.UpdateUserRequest;
import vn.shoestore.application.response.UserResponse;
import vn.shoestore.domain.model.User;

public interface IUserUseCase {
  UserResponse getUserByConditions(FindUserRequest request);

  User getUserById(Long id);

  void updateUser(UpdateUserRequest request);

  void deleteUser(Long id);

  void activeUser(Long id);
}
