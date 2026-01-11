package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.RecommendationAdapter;
import vn.shoestore.domain.model.Recommendation;
import vn.shoestore.infrastructure.repository.entity.RecommendationEntity;
import vn.shoestore.infrastructure.repository.repository.RecommendationRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.time.LocalDateTime;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class RecommendationAdapterImpl implements RecommendationAdapter {
  
  private final RecommendationRepository recommendationRepository;
  
  @Override
  public Recommendation save(Recommendation recommendation) {
    RecommendationEntity entity = ModelMapperUtils.mapper(recommendation, RecommendationEntity.class);
    RecommendationEntity saved = recommendationRepository.save(entity);
    return ModelMapperUtils.mapper(saved, Recommendation.class);
  }
  
  @Override
  public List<Recommendation> findByUserId(Long userId) {
    List<RecommendationEntity> entities = recommendationRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, Recommendation.class);
  }
  
  @Override
  public List<Recommendation> findByUserIdAndType(Long userId, String type, int limit) {
    List<RecommendationEntity> entities = recommendationRepository.findByUserIdAndType(userId, type, limit);
    return ModelMapperUtils.mapList(entities, Recommendation.class);
  }
  
  @Override
  public List<Recommendation> findTopByUserId(Long userId, int limit) {
    List<RecommendationEntity> entities = recommendationRepository.findTopByUserId(userId, limit);
    return ModelMapperUtils.mapList(entities, Recommendation.class);
  }
  
  @Override
  @Transactional
  public void markAsShown(Long recommendationId) {
    recommendationRepository.markAsShown(recommendationId);
  }
  
  @Override
  @Transactional
  public void markAsClicked(Long recommendationId) {
    recommendationRepository.markAsClicked(recommendationId);
  }
  
  @Override
  @Transactional
  public void deleteOldRecommendationsByUserId(Long userId, int daysToKeep) {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
    recommendationRepository.deleteOldRecommendationsByUserId(userId, cutoffDate);
  }
  
  @Override
  public List<Recommendation> saveAll(List<Recommendation> recommendations) {
    List<RecommendationEntity> entities = ModelMapperUtils.mapList(recommendations, RecommendationEntity.class);
    List<RecommendationEntity> saved = recommendationRepository.saveAll(entities);
    return ModelMapperUtils.mapList(saved, Recommendation.class);
  }
  
  @Override
  public boolean existsByUserIdAndProductId(Long userId, Long productId) {
    return recommendationRepository.existsByUserIdAndProductId(userId, productId);
  }
}
