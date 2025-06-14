package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IProductController;
import vn.shoestore.application.request.ProductRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.product.IProductUseCase;

@Component
@RequiredArgsConstructor
public class ProductControllerImpl implements IProductController {
  private final IProductUseCase iProductUseCase;

  @Override
  public ResponseEntity<BaseResponse> saveOrUpdateProduct(ProductRequest request) {
    iProductUseCase.createOrUpdateProduct(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> deleteProduct(Long id) {
    iProductUseCase.deleteProduct(id);
    return ResponseFactory.success();
  }
}
