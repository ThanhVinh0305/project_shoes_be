package vn.shoestore.domain.adapter.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.User;
import vn.shoestore.infrastructure.repository.entity.UserEntity;
import vn.shoestore.infrastructure.repository.repository.UserRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

@Adapter
@RequiredArgsConstructor
public class UserAdapterImpl implements UserAdapter {
  private final UserRepository userRepository;

  @Override
  public User getUserByUsername(String username) {
    Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
    return optionalUser
        .map(userEntity -> ModelMapperUtils.mapper(userEntity, User.class))
        .orElse(null);
  }

  @Override
  public User save(User user) {
    return ModelMapperUtils.mapper(
        userRepository.save(ModelMapperUtils.mapper(user, UserEntity.class)), User.class);
  }

  @Override
  public List<User> getUserByIdIn(List<Long> ids) {
    return ModelMapperUtils.mapList(userRepository.findAllByIdIn(ids), User.class);
  }

  @Override
  public Page<User> getAllUser(Pageable pageable) {
    return ModelMapperUtils.mapPage(userRepository.findAllUserNotAdmin(pageable), User.class);
  }

  @Override
  public void deleteUserById(Long id) {
    userRepository.deleteById(id);
  }
}
