package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.ICategoryController;
import vn.shoestore.application.request.CategoryRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.domain.model.Category;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.product.ICategoryUseCase;

@Component
@RequiredArgsConstructor
public class CategoryControllerImpl implements ICategoryController {

  private final ICategoryUseCase iCategoryUseCase;

  @Override
  public ResponseEntity<BaseResponse<Category>> createCategory(CategoryRequest request) {
    return ResponseFactory.success(iCategoryUseCase.createCategory(request));
  }

  @Override
  public ResponseEntity<BaseResponse<Category>> updateCategory(CategoryRequest request) {
    return ResponseFactory.success(iCategoryUseCase.updateCategory(request));
  }

  @Override
  public ResponseEntity<BaseResponse> deleteCategory(Long id) {
    iCategoryUseCase.deleteCategory(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse<Category>> getById(Long id) {
    return ResponseFactory.success(iCategoryUseCase.getById(id));
  }
}
