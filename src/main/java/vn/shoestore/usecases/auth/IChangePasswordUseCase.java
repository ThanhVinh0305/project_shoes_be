package vn.shoestore.usecases.auth;

import vn.shoestore.application.request.ChangePasswordRequest;

public interface IChangePasswordUseCase {
  void changePassword(ChangePasswordRequest request);
}
