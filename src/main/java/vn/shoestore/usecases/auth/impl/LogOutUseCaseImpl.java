package vn.shoestore.usecases.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.UserRefreshTokenAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.domain.model.UserRefreshToken;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.usecases.auth.ILogOutUseCase;

import java.util.List;
import java.util.Objects;

@UseCase
@RequiredArgsConstructor
public class LogOutUseCaseImpl implements ILogOutUseCase {
  private final UserRefreshTokenAdapter userRefreshTokenAdapter;

  @Override
  @Transactional
  public void logout() {
    CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();

    if (Objects.isNull(userDetails) || Objects.isNull(userDetails.getUser())) {
      return;
    }

    User user = userDetails.getUser();

    List<UserRefreshToken> oldToken =
        userRefreshTokenAdapter.findAllByUserIdAndInvoke(user.getId(), false);
    oldToken.forEach(e -> e.setInvoke(true));
    userRefreshTokenAdapter.saveAll(oldToken);
  }
}
