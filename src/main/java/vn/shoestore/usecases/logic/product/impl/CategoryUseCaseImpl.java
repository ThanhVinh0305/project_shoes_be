package vn.shoestore.usecases.logic.product.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.application.request.CategoryRequest;
import vn.shoestore.domain.adapter.CategoryAdapter;
import vn.shoestore.domain.model.Category;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.usecases.logic.product.ICategoryUseCase;

@UseCase
@RequiredArgsConstructor
public class CategoryUseCaseImpl implements ICategoryUseCase {
  private final CategoryAdapter categoryAdapter;

  @Override
  public Category createCategory(CategoryRequest request) {
    return categoryAdapter.createOrUpdateCategory(ModelMapperUtils.mapper(request, Category.class));
  }

  @Override
  public Category updateCategory(CategoryRequest request) {
    return categoryAdapter.createOrUpdateCategory(ModelMapperUtils.mapper(request, Category.class));
  }

  @Override
  public void deleteCategory(Long id) {
    categoryAdapter.deleteById(id);
  }

  @Override
  public Category getById(Long id) {
    return categoryAdapter.findById(id);
  }
}
