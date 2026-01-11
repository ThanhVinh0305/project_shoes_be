package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.ProductViewAdapter;
import vn.shoestore.domain.model.ProductView;
import vn.shoestore.infrastructure.repository.entity.ProductViewEntity;
import vn.shoestore.infrastructure.repository.repository.ProductViewRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class ProductViewAdapterImpl implements ProductViewAdapter {
  
  private final ProductViewRepository productViewRepository;
  
  @Override
  public ProductView save(ProductView productView) {
    ProductViewEntity entity = ModelMapperUtils.mapper(productView, ProductViewEntity.class);
    ProductViewEntity saved = productViewRepository.save(entity);
    return ModelMapperUtils.mapper(saved, ProductView.class);
  }
  
  @Override
  public Optional<ProductView> findByUserIdAndProductId(Long userId, Long productId) {
    Optional<ProductViewEntity> entityOpt = productViewRepository.findByUserIdAndProductId(userId, productId);
    return entityOpt.map(entity -> ModelMapperUtils.mapper(entity, ProductView.class));
  }
  
  @Override
  public List<ProductView> findByUserId(Long userId) {
    List<ProductViewEntity> entities = productViewRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, ProductView.class);
  }
  
  @Override
  public List<ProductView> findTopViewedByUserId(Long userId, int limit) {
    List<ProductViewEntity> entities = productViewRepository.findTopViewedByUserId(userId, limit);
    return ModelMapperUtils.mapList(entities, ProductView.class);
  }
  
  @Override
  public List<ProductView> findRecentlyViewedByUserId(Long userId, int limit) {
    List<ProductViewEntity> entities = productViewRepository.findRecentlyViewedByUserId(userId, limit);
    return ModelMapperUtils.mapList(entities, ProductView.class);
  }
  
  @Override
  public List<ProductView> findByUserIdAndLastViewedDateAfter(Long userId, LocalDateTime after) {
    List<ProductViewEntity> entities = productViewRepository.findByUserIdAndLastViewedDateAfter(userId, after);
    return ModelMapperUtils.mapList(entities, ProductView.class);
  }
  
  @Override
  public Long getTotalViewCountByProductId(Long productId) {
    return productViewRepository.getTotalViewCountByProductId(productId);
  }
  
  @Override
  public List<Long> findTopViewedProducts(int limit) {
    return productViewRepository.findTopViewedProducts(limit);
  }
}
