package vn.shoestore.domain.adapter;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.shoestore.domain.model.User;

public interface UserAdapter {
  User getUserByUsername(String username);

  User save(User user);

  List<User> getUserByIdIn(List<Long> ids);

  Page<User> getAllUser(Pageable pageable);

  void deleteUserById(Long id);
}
