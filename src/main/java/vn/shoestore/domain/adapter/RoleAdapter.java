package vn.shoestore.domain.adapter;

import java.util.List;
import vn.shoestore.domain.model.Role;
import vn.shoestore.domain.model.UserRole;

public interface RoleAdapter {
  List<Role> findAllByIdIn(List<Long> ids);

  List<UserRole> findAllByUserIdIn(List<Long> userIds);

  List<Role> getAll();

  void saveAll(List<Role> roles);
}
