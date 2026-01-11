package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.UserBehaviorAdapter;
import vn.shoestore.domain.model.UserBehavior;
import vn.shoestore.infrastructure.repository.entity.UserBehaviorEntity;
import vn.shoestore.infrastructure.repository.repository.UserBehaviorRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.time.LocalDateTime;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class UserBehaviorAdapterImpl implements UserBehaviorAdapter {
  
  private final UserBehaviorRepository userBehaviorRepository;
  
  @Override
  public UserBehavior save(UserBehavior behavior) {
    UserBehaviorEntity entity = ModelMapperUtils.mapper(behavior, UserBehaviorEntity.class);
    UserBehaviorEntity saved = userBehaviorRepository.save(entity);
    return ModelMapperUtils.mapper(saved, UserBehavior.class);
  }
  
  @Override
  public List<UserBehavior> findByUserId(Long userId) {
    List<UserBehaviorEntity> entities = userBehaviorRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, UserBehavior.class);
  }
  
  @Override
  public List<UserBehavior> findByUserIdAndBehaviorType(Long userId, String behaviorType) {
    List<UserBehaviorEntity> entities = userBehaviorRepository.findByUserIdAndBehaviorType(userId, behaviorType);
    return ModelMapperUtils.mapList(entities, UserBehavior.class);
  }
  
  @Override
  public List<UserBehavior> findByUserIdAndCreatedDateBetween(Long userId, LocalDateTime start, LocalDateTime end) {
    List<UserBehaviorEntity> entities = userBehaviorRepository.findByUserIdAndCreatedDateBetween(userId, start, end);
    return ModelMapperUtils.mapList(entities, UserBehavior.class);
  }
  
  @Override
  public List<UserBehavior> findByProductId(Long productId) {
    List<UserBehaviorEntity> entities = userBehaviorRepository.findByProductId(productId);
    return ModelMapperUtils.mapList(entities, UserBehavior.class);
  }
  
  @Override
  public Long countByUserIdAndBehaviorType(Long userId, String behaviorType) {
    return userBehaviorRepository.countByUserIdAndBehaviorType(userId, behaviorType);
  }
}
