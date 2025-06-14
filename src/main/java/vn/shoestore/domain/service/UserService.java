package vn.shoestore.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.shoestore.domain.adapter.RoleAdapter;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.Role;
import vn.shoestore.domain.model.User;
import vn.shoestore.domain.model.UserRole;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.shared.utils.SecurityUtils;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserAdapter userAdapter;
  private final RoleAdapter roleAdapter;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userAdapter.getUserByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    enrichRole(Collections.singletonList(user));
    return new CustomUserDetails(user);
  }

  public void enrichRole(List<User> users) {
    if (users == null || users.isEmpty()) return;
    List<Long> userIds = ModelTransformUtils.getAttribute(users, User::getId);
    List<UserRole> userRoles = roleAdapter.findAllByUserIdIn(userIds);

    List<Long> allRoleIds = ModelTransformUtils.getAttribute(userRoles, UserRole::getRoleId);

    List<Role> roles = roleAdapter.findAllByIdIn(allRoleIds);

    Map<Long, List<Long>> mapUserIdWithRoleIds =
        userRoles.stream()
            .collect(
                Collectors.groupingBy(
                    UserRole::getUserId,
                    Collectors.mapping(UserRole::getRoleId, Collectors.toList())));

    for (User user : users) {
      List<Long> roleIds = mapUserIdWithRoleIds.getOrDefault(user.getId(), Collections.emptyList());
      if (roleIds.isEmpty()) continue;
      List<Role> roleOfUsers = roles.stream().filter(e -> roleIds.contains(e.getId())).toList();
      user.setRoles(roleOfUsers);
    }
  }

  public boolean hasRole(String authRole) {
    User user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      return false;
    }

    List<Role> roles = user.getRoles();
    for (Role role : roles) {
      if (Objects.equals(role.getName(), authRole)) {
        return true;
      }
    }
    return false;
  }
}
