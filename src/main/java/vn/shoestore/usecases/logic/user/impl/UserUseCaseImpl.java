package vn.shoestore.usecases.logic.user.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.USER_NOT_FOUND;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.FindUserRequest;
import vn.shoestore.application.request.UpdateUserRequest;
import vn.shoestore.application.response.UserResponse;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.domain.service.UserService;
import vn.shoestore.infrastructure.repository.entity.UserRoleEntity;
import vn.shoestore.infrastructure.repository.repository.UserRoleRepository;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.usecases.logic.user.IUserUseCase;

@UseCase
@RequiredArgsConstructor
public class UserUseCaseImpl implements IUserUseCase {

  private final UserAdapter userAdapter;
  private final UserService userService;
  private final UserRoleRepository userRoleRepository;

  @Override
  public UserResponse getUserByConditions(FindUserRequest request) {
    Page<User> pageUser =
        userAdapter.getAllUser(PageRequest.of(request.getPage() - 1, request.getSize()));
    if (pageUser.isEmpty()) return UserResponse.builder().build();

    List<User> users = pageUser.getContent();
    userService.enrichRole(users);
    return UserResponse.builder().total(pageUser.getTotalElements()).data(users).build();
  }

  @Override
  public User getUserById(Long id) {
    List<User> users = userAdapter.getUserByIdIn(Collections.singletonList(id));
    if (users.isEmpty()) return null;
    userService.enrichRole(users);
    return users.get(0);
  }

  @Override
  @Transactional
  public void updateUser(UpdateUserRequest request) {
    List<User> users = userAdapter.getUserByIdIn(Collections.singletonList(request.getId()));
    if (users.isEmpty()) {
      throw new InputNotValidException(USER_NOT_FOUND);
    }
    User user = users.get(0);

    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setAddress(request.getAddress());

    userAdapter.save(user);

    if (Objects.nonNull(request.getIsAdmin()) && request.getIsAdmin()) {
      userRoleRepository.save(new UserRoleEntity(null, user.getId(), 1L));
    }
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    List<User> users = userAdapter.getUserByIdIn(Collections.singletonList(id));
    if (users.isEmpty()) {
      throw new InputNotValidException(USER_NOT_FOUND);
    }
    User user = users.get(0);
    user.setActive(false);
    userAdapter.save(user);
  }

  @Override
  @Transactional
  public void activeUser(Long id) {
    List<User> users = userAdapter.getUserByIdIn(Collections.singletonList(id));
    if (users.isEmpty()) {
      throw new InputNotValidException(USER_NOT_FOUND);
    }
    User user = users.get(0);
    user.setActive(true);
    userAdapter.save(user);
  }
}
