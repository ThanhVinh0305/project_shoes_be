package vn.shoestore.usecases.logic.product;

import vn.shoestore.application.request.SearchProductRequest;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.application.response.SearchProductResponse;

import java.util.List;

public interface IGetProductUseCase {
  SearchProductResponse searchProduct(SearchProductRequest request);

  ProductResponse findOne(Long productId);

  List<ProductResponse> findByIds(List<Long> ids);
}
