package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.domain.adapter.ProductPromotionAdapter;
import vn.shoestore.domain.model.ProductPromotion;
import vn.shoestore.domain.model.Promotion;
import vn.shoestore.infrastructure.repository.entity.ProductPromotionEntity;
import vn.shoestore.infrastructure.repository.entity.PromotionEntity;
import vn.shoestore.infrastructure.repository.repository.ProductPromotionRepository;
import vn.shoestore.infrastructure.repository.repository.PromotionRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class ProductPromotionAdapterImpl implements ProductPromotionAdapter {
  private final ProductPromotionRepository productPromotionRepository;
  private final PromotionRepository promotionRepository;

  @Override
  public Promotion savePromotion(Promotion productPromotion) {
    return ModelMapperUtils.mapper(
        promotionRepository.save(ModelMapperUtils.mapper(productPromotion, PromotionEntity.class)),
        Promotion.class);
  }

  @Override
  public void deletePromotion(Long id) {
    promotionRepository.deleteById(id);
  }

  @Override
  public void saveProductPromotions(List<ProductPromotion> productPromotions) {
    productPromotionRepository.saveAll(
        ModelMapperUtils.mapList(productPromotions, ProductPromotionEntity.class));
  }

  @Override
  public Optional<Promotion> findById(Long promotionId) {
    Optional<PromotionEntity> optional = promotionRepository.findById(promotionId);
    if (optional.isEmpty()) return Optional.empty();
    return Optional.of(ModelMapperUtils.mapper(optional, Promotion.class));
  }

  @Override
  public List<ProductPromotion> findAllByPromotionId(Long promotionId) {
    return ModelMapperUtils.mapList(
        productPromotionRepository.findAllByPromotionId(promotionId), ProductPromotion.class);
  }

  @Override
  public void deleteAllByProductIds(Long promotionId, List<Long> productIds) {
    if (productIds.isEmpty()) return;
    List<Long> productPromotionIds =
        findAllByPromotionId(promotionId).stream()
            .filter(e -> productIds.contains(e.getProductId()))
            .map(ProductPromotion::getId)
            .toList();

    productPromotionRepository.deleteAllByIdInBatch(productPromotionIds);
  }

  @Override
  public Page<Promotion> findPromotionByCondition(FindPromotionRequest request) {
    return ModelMapperUtils.mapPage(
        promotionRepository.findAllByKeyword(
            request.getKeyword(), PageRequest.of(request.getPage() - 1, request.getSize())),
        Promotion.class);
  }

  @Override
  public List<Promotion> getPromotionByIds(List<Long> ids) {
    return ModelMapperUtils.mapList(promotionRepository.findAllByIdIn(ids), Promotion.class);
  }

  @Override
  public List<ProductPromotion> getProductPromotionByProductIds(List<Long> productIds) {
    return ModelMapperUtils.mapList(
        productPromotionRepository.findAllByProductIdIn(productIds), ProductPromotion.class);
  }
}
