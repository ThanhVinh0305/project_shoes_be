package vn.shoestore.usecases.logic.product;

import vn.shoestore.application.request.BrandRequest;
import vn.shoestore.domain.model.Brand;

public interface IBrandUseCase {
  Brand createBrand(BrandRequest request);

  Brand updateBrand(BrandRequest request);

  void deleteBrand(Long id);

  Brand getById(Long id);
}
