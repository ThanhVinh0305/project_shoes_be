package vn.shoestore.usecases.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.LoginRefreshTokenRequest;
import vn.shoestore.application.response.LoginResponse;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.adapter.UserRefreshTokenAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.domain.model.UserRefreshToken;
import vn.shoestore.infrastructure.configuration.authen.JwtTokenProvider;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.exceptions.NotAuthorizedException;
import vn.shoestore.shared.utils.ObjectUtils;
import vn.shoestore.usecases.auth.ILoginRefreshTokenUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static vn.shoestore.shared.constants.ExceptionMessage.LOGIN_FAIL;
import static vn.shoestore.shared.constants.ExceptionMessage.REFRESH_TOKEN_NOT_VALID;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class LoginRefreshTokenUseCaseImpl implements ILoginRefreshTokenUseCase {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserAdapter userAdapter;
  private final UserRefreshTokenAdapter userRefreshTokenAdapter;
  private final PasswordEncoder passwordEncoder;

  @Value("${vn.shoe_store.secret.jwt_expiration_ms}")
  private int jwtExpirationMs;

  @Override
  @Transactional
  public LoginResponse login(LoginRefreshTokenRequest request) throws IllegalAccessException {
    boolean isValidToken = jwtTokenProvider.validateJwtToken(request.getToken());
    if (!isValidToken) {
      throw new NotAuthorizedException(REFRESH_TOKEN_NOT_VALID);
    }

    User tokenUser =
        userAdapter.getUserByUsername(jwtTokenProvider.getUsernameByToken(request.getToken()));

    if (Objects.isNull(tokenUser)) {
      throw new NotAuthorizedException(LOGIN_FAIL);
    }

    List<UserRefreshToken> userRefreshTokenList =
        userRefreshTokenAdapter.findAllByUserIdAndInvoke(tokenUser.getId(), false);

    if (userRefreshTokenList.isEmpty()) {
      throw new NotAuthorizedException(REFRESH_TOKEN_NOT_VALID);
    }

    UserRefreshToken currentRefreshToken = userRefreshTokenList.get(0);

    if (!passwordEncoder.matches(request.getToken(), currentRefreshToken.getRefreshToken())) {
      throw new NotAuthorizedException(REFRESH_TOKEN_NOT_VALID);
    }

    return LoginResponse.builder()
        .accessToken(
            jwtTokenProvider.generateJwtToken(
                tokenUser, ObjectUtils.convertUsingReflection(tokenUser)))
        .refreshToken(request.getToken())
        .expiresIn(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000))
        .userInfo(tokenUser)
        .build();
  }
}
