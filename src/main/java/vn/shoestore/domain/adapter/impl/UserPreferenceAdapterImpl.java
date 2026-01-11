package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.UserPreferenceAdapter;
import vn.shoestore.domain.model.UserPreference;
import vn.shoestore.infrastructure.repository.entity.UserPreferenceEntity;
import vn.shoestore.infrastructure.repository.repository.UserPreferenceRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class UserPreferenceAdapterImpl implements UserPreferenceAdapter {
  
  private final UserPreferenceRepository userPreferenceRepository;
  
  @Override
  public UserPreference save(UserPreference preference) {
    UserPreferenceEntity entity = ModelMapperUtils.mapper(preference, UserPreferenceEntity.class);
    UserPreferenceEntity saved = userPreferenceRepository.save(entity);
    return ModelMapperUtils.mapper(saved, UserPreference.class);
  }
  
  @Override
  public List<UserPreference> findByUserId(Long userId) {
    List<UserPreferenceEntity> entities = userPreferenceRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, UserPreference.class);
  }
  
  @Override
  public List<UserPreference> findByUserIdAndPreferenceType(Long userId, String preferenceType) {
    List<UserPreferenceEntity> entities = userPreferenceRepository.findByUserIdAndPreferenceType(userId, preferenceType);
    return ModelMapperUtils.mapList(entities, UserPreference.class);
  }
  
  @Override
  public Optional<UserPreference> findByUserIdAndPreferenceTypeAndValue(Long userId, String type, String value) {
    Optional<UserPreferenceEntity> entityOpt = userPreferenceRepository.findByUserIdAndPreferenceTypeAndPreferenceValue(userId, type, value);
    return entityOpt.map(entity -> ModelMapperUtils.mapper(entity, UserPreference.class));
  }
  
  @Override
  public List<UserPreference> findTopByUserIdAndType(Long userId, String type, int limit) {
    List<UserPreferenceEntity> entities = userPreferenceRepository.findTopByUserIdAndType(userId, type, limit);
    return ModelMapperUtils.mapList(entities, UserPreference.class);
  }
  
  @Override
  public void deleteByUserId(Long userId) {
    userPreferenceRepository.deleteByUserId(userId);
  }
  
  @Override
  public List<UserPreference> saveAll(List<UserPreference> preferences) {
    List<UserPreferenceEntity> entities = ModelMapperUtils.mapList(preferences, UserPreferenceEntity.class);
    List<UserPreferenceEntity> saved = userPreferenceRepository.saveAll(entities);
    return ModelMapperUtils.mapList(saved, UserPreference.class);
  }
}
