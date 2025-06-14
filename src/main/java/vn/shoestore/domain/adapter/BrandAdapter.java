package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.Brand;

import java.util.List;

public interface BrandAdapter {
  List<Brand> findAll();

  List<Brand> findAllByIds(List<Long> ids);

  Brand createOrUpdateBrand(Brand brand);

  void deleteById(Long id);

  Brand findById(Long id);
}
