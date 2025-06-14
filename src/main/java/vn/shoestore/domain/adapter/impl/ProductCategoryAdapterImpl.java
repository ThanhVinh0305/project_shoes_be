package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.ProductCategoryAdapter;
import vn.shoestore.domain.model.ProductCategory;
import vn.shoestore.infrastructure.repository.entity.ProductCategoryEntity;
import vn.shoestore.infrastructure.repository.repository.ProductCategoryRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ProductCategoryAdapterImpl implements ProductCategoryAdapter {

  private final ProductCategoryRepository productCategoryRepository;

  @Override
  public List<ProductCategory> findAllByProductIds(List<Long> productIds) {
    return ModelMapperUtils.mapList(
        productCategoryRepository.findAllByProductIdIn(productIds), ProductCategory.class);
  }

  @Override
  public void saveAll(List<ProductCategory> productAttachments) {
    productCategoryRepository.saveAll(
        ModelMapperUtils.mapList(productAttachments, ProductCategoryEntity.class));
  }

  @Override
  public void deleteByIds(List<Long> ids) {
    productCategoryRepository.deleteAllByIdInBatch(ids);
  }
}
