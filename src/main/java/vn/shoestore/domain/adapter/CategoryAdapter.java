package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.Category;

import java.util.List;

public interface CategoryAdapter {
  List<Category> findAllCategory();

  List<Category> findAllByIdIn(List<Long> ids);

  Category createOrUpdateCategory(Category category);

  void deleteById(Long id);

  Category findById(Long id);
}
