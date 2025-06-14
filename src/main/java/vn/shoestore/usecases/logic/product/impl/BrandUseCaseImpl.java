package vn.shoestore.usecases.logic.product.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.application.request.BrandRequest;
import vn.shoestore.domain.adapter.BrandAdapter;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.usecases.logic.product.IBrandUseCase;

@UseCase
@RequiredArgsConstructor
public class BrandUseCaseImpl implements IBrandUseCase {
  private final BrandAdapter brandAdapter;

  @Override
  public Brand createBrand(BrandRequest request) {
    return brandAdapter.createOrUpdateBrand(ModelMapperUtils.mapper(request, Brand.class));
  }

  @Override
  public Brand updateBrand(BrandRequest request) {
    return brandAdapter.createOrUpdateBrand(ModelMapperUtils.mapper(request, Brand.class));
  }

  @Override
  public void deleteBrand(Long id) {
    brandAdapter.deleteById(id);
  }

  @Override
  public Brand getById(Long id) {
    return brandAdapter.findById(id);
  }
}
