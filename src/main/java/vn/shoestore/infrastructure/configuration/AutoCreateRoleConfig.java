package vn.shoestore.infrastructure.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.shoestore.domain.adapter.RoleAdapter;
import vn.shoestore.domain.model.Role;
import vn.shoestore.shared.utils.ModelTransformUtils;

@Configuration
@RequiredArgsConstructor
public class AutoCreateRoleConfig {

  private final RoleAdapter roleAdapter;

  private static final String ADMIN = "ADMIN";
  private static final String USER = "ROLE_USER";

  @Bean
  public List<Role> createRole() {
    List<Role> allRoles = roleAdapter.getAll();
    List<String> allRoleStrings = ModelTransformUtils.getAttribute(allRoles, Role::getName);

    List<Role> savedRole = new ArrayList<>();
    if (!allRoleStrings.contains(ADMIN)) {
      savedRole.add(Role.builder().name(ADMIN).build());
    }

    if (!allRoleStrings.contains(USER)) {
      savedRole.add(Role.builder().name(USER).build());
    }

    if (savedRole.isEmpty()) return Collections.emptyList();
    roleAdapter.saveAll(savedRole);
    return savedRole;
  }
}
