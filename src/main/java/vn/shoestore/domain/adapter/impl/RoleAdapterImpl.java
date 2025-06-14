package vn.shoestore.domain.adapter.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.RoleAdapter;
import vn.shoestore.domain.model.Role;
import vn.shoestore.domain.model.UserRole;
import vn.shoestore.infrastructure.repository.entity.RoleEntity;
import vn.shoestore.infrastructure.repository.repository.RoleRepository;
import vn.shoestore.infrastructure.repository.repository.UserRoleRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

@Adapter
@RequiredArgsConstructor
public class RoleAdapterImpl implements RoleAdapter {
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;

  @Override
  public List<Role> findAllByIdIn(List<Long> ids) {
    return ModelMapperUtils.mapList(roleRepository.findAllByIdIn(ids), Role.class);
  }

  @Override
  public List<UserRole> findAllByUserIdIn(List<Long> userIds) {
    return ModelMapperUtils.mapList(userRoleRepository.findAllByUserIdIn(userIds), UserRole.class);
  }

  @Override
  public List<Role> getAll() {
    return ModelMapperUtils.mapList(roleRepository.findAll(), Role.class);
  }

  @Override
  public void saveAll(List<Role> roles) {
    roleRepository.saveAll(ModelMapperUtils.mapList(roles , RoleEntity.class));
  }

}
