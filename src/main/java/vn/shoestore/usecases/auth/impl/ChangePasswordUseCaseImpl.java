package vn.shoestore.usecases.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.shoestore.application.request.ChangePasswordRequest;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.exceptions.NotAuthorizedException;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.usecases.auth.IChangePasswordUseCase;

import java.util.Objects;

import static vn.shoestore.shared.constants.ExceptionMessage.*;

@UseCase
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements IChangePasswordUseCase {
  private final PasswordEncoder passwordEncoder;
  private final UserAdapter userAdapter;

  @Override
  public void changePassword(ChangePasswordRequest request) {
    CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();

    if (Objects.isNull(userDetails) || Objects.isNull(userDetails.getUser())) {
      throw new NotAuthorizedException(NOT_VALID_USER_DETAILS);
    }

    User user = userDetails.getUser();
    String currentPassword = user.getPassword();

    if (!passwordEncoder.matches(request.getOldPassword(), currentPassword)) {
      throw new NotAuthorizedException(OLD_PASSWORD_NOT_TRUE);
    }

    String hashPassword = passwordEncoder.encode(request.getNewPassword());
    user.setPassword(hashPassword);

    userAdapter.save(user);
  }
}
