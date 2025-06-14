package vn.shoestore.application.controllers.open_api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.open_api.IOpenApiProductController;
import vn.shoestore.application.request.SearchProductRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.application.response.SearchProductResponse;
import vn.shoestore.domain.adapter.BrandAdapter;
import vn.shoestore.domain.adapter.CategoryAdapter;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.domain.model.Category;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenApiProductControllerImpl implements IOpenApiProductController {

  private final CategoryAdapter categoryAdapter;
  private final BrandAdapter brandAdapter;
  private final IGetProductUseCase iGetProductUseCase;

  @Override
  public ResponseEntity<BaseResponse<List<Brand>>> getAllBrands() {
    return ResponseFactory.success(brandAdapter.findAll());
  }

  @Override
  public ResponseEntity<BaseResponse<List<Category>>> getAllCategories() {
    return ResponseFactory.success(categoryAdapter.findAllCategory());
  }

  @Override
  public ResponseEntity<BaseResponse<SearchProductResponse>> searchProducts(
      SearchProductRequest request) {
    return ResponseFactory.success(iGetProductUseCase.searchProduct(request));
  }

  @Override
  public ResponseEntity<BaseResponse<ProductResponse>> findOne(Long id) {
    return ResponseFactory.success(iGetProductUseCase.findOne(id));
  }
}
