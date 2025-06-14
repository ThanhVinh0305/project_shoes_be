package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.ProductBrandAdapter;
import vn.shoestore.domain.model.ProductBrand;
import vn.shoestore.infrastructure.repository.entity.ProductBrandEntity;
import vn.shoestore.infrastructure.repository.repository.ProductBrandRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ProductBrandAdapterImpl implements ProductBrandAdapter {

  private final ProductBrandRepository productBrandRepository;

  @Override
  public List<ProductBrand> findAllByProductIds(List<Long> productIds) {
    return ModelMapperUtils.mapList(
        productBrandRepository.findAllByProductIdIn(productIds), ProductBrand.class);
  }

  @Override
  public void saveAll(List<ProductBrand> productBrands) {
    productBrandRepository.saveAll(
        ModelMapperUtils.mapList(productBrands, ProductBrandEntity.class));
  }

  @Override
  public void deleteByIds(List<Long> ids) {
    productBrandRepository.deleteAllByIdInBatch(ids);
  }
}
