package vn.shoestore.usecases.auth.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.INVALID_PASSWORD;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.LoginRequest;
import vn.shoestore.application.request.RegisterRequest;
import vn.shoestore.application.response.LoginResponse;
import vn.shoestore.domain.adapter.CartAdapter;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.adapter.UserRefreshTokenAdapter;
import vn.shoestore.domain.model.*;
import vn.shoestore.domain.service.UserService;
import vn.shoestore.infrastructure.configuration.authen.JwtTokenProvider;
import vn.shoestore.infrastructure.repository.entity.UserRoleEntity;
import vn.shoestore.infrastructure.repository.repository.UserRoleRepository;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.constants.ExceptionMessage;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.exceptions.NotAuthorizedException;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ObjectUtils;
import vn.shoestore.usecases.auth.IAuthUseCase;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class AuthUseCaseImpl implements IAuthUseCase {

  private final UserAdapter userAdapter;

  private final JwtTokenProvider jwtTokenProvider;

  private final PasswordEncoder passwordEncoder;

  private final UserRefreshTokenAdapter userRefreshTokenAdapter;
  private final UserService userService;

  private final CartAdapter cartAdapter;
  private final UserRoleRepository userRoleRepository;

  @Value("${vn.shoe_store.secret.jwt_expiration_ms}")
  private int jwtExpirationMs;

  private static final String PASSWORD_REGEX =
      "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";

  @Override
  @Transactional
  public LoginResponse login(LoginRequest request) throws IllegalAccessException {
    User user = userAdapter.getUserByUsername(request.getUsername());
    userService.enrichRole(Stream.of(user).filter(Objects::nonNull).toList());
    if (Objects.isNull(user)) {
      throw new NotAuthorizedException(ExceptionMessage.LOGIN_FAIL);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new NotAuthorizedException(ExceptionMessage.LOGIN_FAIL);
    }

    String token =
        jwtTokenProvider.generateJwtToken(user, ObjectUtils.convertUsingReflection(user));
    String refreshToken =
        jwtTokenProvider.generateRefreshToken(user, ObjectUtils.convertUsingReflection(user));
    processRefreshToken(user, refreshToken);

    if (Objects.isNull(user.getRoles()) || user.getRoles().isEmpty()) {
      user.setRoles(List.of(Role.builder().id(2L).name("USER_ROLE").build()));
    }

    return LoginResponse.builder()
        .accessToken(token)
        .refreshToken(refreshToken)
        .expiresIn(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000))
        .userInfo(user)
        .build();
  }

  @Override
  @Transactional
  public User register(RegisterRequest request) {
    User user = ModelMapperUtils.mapper(request, User.class);

    Pattern pattern = Pattern.compile(PASSWORD_REGEX);
    if (!pattern.matcher(request.getPassword()).matches()) {
      throw new InputNotValidException(INVALID_PASSWORD);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));

    User savedUser = userAdapter.save(user);

    createCart(savedUser);
    userRoleRepository.save(
        ModelMapperUtils.mapper(
            UserRole.builder().userId(savedUser.getId()).roleId(2L).build(), UserRoleEntity.class));
    return savedUser;
  }

  private void createCart(User savedUser) {
    Cart cart =
        Cart.builder()
            .userId(savedUser.getId())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

    cartAdapter.save(cart);
  }

  private void processRefreshToken(User user, String refreshToken) {
    List<UserRefreshToken> oldToken =
        userRefreshTokenAdapter.findAllByUserIdAndInvoke(user.getId(), false);
    oldToken.forEach(e -> e.setInvoke(true));
    userRefreshTokenAdapter.saveAll(oldToken);

    UserRefreshToken userRefreshToken =
        UserRefreshToken.builder()
            .userId(user.getId())
            .invoke(false)
            .refreshToken(passwordEncoder.encode(refreshToken))
            .build();

    userRefreshTokenAdapter.save(userRefreshToken);
  }
}
