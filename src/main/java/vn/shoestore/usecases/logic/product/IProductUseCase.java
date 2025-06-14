package vn.shoestore.usecases.logic.product;

import vn.shoestore.application.request.ProductRequest;

public interface IProductUseCase {
  void createOrUpdateProduct(ProductRequest request);

  void deleteProduct(Long id);
}
