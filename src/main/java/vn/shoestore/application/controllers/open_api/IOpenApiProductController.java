package vn.shoestore.application.controllers.open_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.SearchProductRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.application.response.SearchProductResponse;
import vn.shoestore.domain.model.Brand;
import vn.shoestore.domain.model.Category;

import java.util.List;

@RequestMapping("/open-api/products")
@RestController
public interface IOpenApiProductController {
  @GetMapping("brands")
  ResponseEntity<BaseResponse<List<Brand>>> getAllBrands();

  @GetMapping("categories")
  ResponseEntity<BaseResponse<List<Category>>> getAllCategories();

  @PostMapping("search-products")
  ResponseEntity<BaseResponse<SearchProductResponse>> searchProducts(
      @RequestBody SearchProductRequest request);

  @GetMapping("{id}")
  ResponseEntity<BaseResponse<ProductResponse>> findOne(@PathVariable Long id);
}
