package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.UserRefreshTokenAdapter;
import vn.shoestore.domain.model.UserRefreshToken;
import vn.shoestore.infrastructure.repository.entity.UserRefreshTokenEntity;
import vn.shoestore.infrastructure.repository.repository.UserRefreshTokenRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class UserRefreshTokenAdapterImpl implements UserRefreshTokenAdapter {
  private final UserRefreshTokenRepository userRefreshTokenRepository;

  @Override
  public List<UserRefreshToken> findAllByUserIdAndInvoke(Long userId, Boolean invoke) {
    return ModelMapperUtils.mapList(
        userRefreshTokenRepository.findAllByUserIdAndInvoke(userId, invoke),
        UserRefreshToken.class);
  }

  @Override
  public UserRefreshToken save(UserRefreshToken userRefreshToken) {
    return ModelMapperUtils.mapper(
        userRefreshTokenRepository.save(
            ModelMapperUtils.mapper(userRefreshToken, UserRefreshTokenEntity.class)),
        UserRefreshToken.class);
  }

  @Override
  public void saveAll(List<UserRefreshToken> userRefreshTokens) {
    userRefreshTokenRepository.saveAll(
        ModelMapperUtils.mapList(userRefreshTokens, UserRefreshTokenEntity.class));
  }
}
