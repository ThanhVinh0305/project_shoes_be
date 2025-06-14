package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.UserRefreshToken;

import java.util.List;

public interface UserRefreshTokenAdapter {
  List<UserRefreshToken> findAllByUserIdAndInvoke(Long userId, Boolean invoke);

  UserRefreshToken save(UserRefreshToken userRefreshToken);

  void saveAll(List<UserRefreshToken> userRefreshTokens);
}
