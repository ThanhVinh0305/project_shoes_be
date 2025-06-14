package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ProductBrand;

import java.util.List;

public interface ProductBrandAdapter {
  List<ProductBrand> findAllByProductIds(List<Long> productIds);

  void saveAll(List<ProductBrand> productBrands);

  void deleteByIds(List<Long> ids);
}
