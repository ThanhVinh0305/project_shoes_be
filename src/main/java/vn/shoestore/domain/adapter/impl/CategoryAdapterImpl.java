package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.CategoryAdapter;
import vn.shoestore.domain.model.Category;
import vn.shoestore.infrastructure.repository.entity.CategoryEntity;
import vn.shoestore.infrastructure.repository.repository.CategoryRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;
import java.util.Optional;

import static vn.shoestore.shared.constants.ExceptionMessage.CATEGORY_NOT_FOUND;

@Adapter
@RequiredArgsConstructor
public class CategoryAdapterImpl implements CategoryAdapter {
  private final CategoryRepository categoryRepository;

  @Override
  public List<Category> findAllCategory() {
    return ModelMapperUtils.mapList(categoryRepository.findAll(), Category.class);
  }

  @Override
  public List<Category> findAllByIdIn(List<Long> ids) {
    return ModelMapperUtils.mapList(categoryRepository.findAllByIdIn(ids), Category.class);
  }

  @Override
  public Category createOrUpdateCategory(Category category) {
    return ModelMapperUtils.mapper(
        categoryRepository.save(ModelMapperUtils.mapper(category, CategoryEntity.class)),
        Category.class);
  }

  @Override
  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }

  @Override
  public Category findById(Long id) {
    Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(id);
    if (optionalCategoryEntity.isEmpty()) {
      throw new InputNotValidException(CATEGORY_NOT_FOUND);
    }
    return ModelMapperUtils.mapper(optionalCategoryEntity.get(), Category.class);
  }
}
