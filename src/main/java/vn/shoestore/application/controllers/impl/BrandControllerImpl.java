package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IBrandController;
import vn.shoestore.application.request.BrandRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.product.IBrandUseCase;

@Component
@RequiredArgsConstructor
public class BrandControllerImpl implements IBrandController {

  private final IBrandUseCase iBrandUseCase;

  @Override
  public ResponseEntity<BaseResponse<Brand>> createBrand(BrandRequest request) {
    return ResponseFactory.success(iBrandUseCase.createBrand(request));
  }

  @Override
  public ResponseEntity<BaseResponse<Brand>> updateBrand(BrandRequest request) {
    return ResponseFactory.success(iBrandUseCase.updateBrand(request));
  }

  @Override
  public ResponseEntity<BaseResponse> deleteBrand(Long id) {
    iBrandUseCase.deleteBrand(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse<Brand>> getById(Long id) {
    return ResponseFactory.success(iBrandUseCase.getById(id));
  }
}
