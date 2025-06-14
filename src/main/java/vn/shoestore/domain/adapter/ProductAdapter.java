package vn.shoestore.domain.adapter;

import org.springframework.data.domain.Page;
import vn.shoestore.application.request.SearchProductRequest;
import vn.shoestore.domain.model.Product;

import java.util.List;

public interface ProductAdapter {
  Page<Long> getProductByCondition(SearchProductRequest request);

  Product save(Product product);

  Product getProductById(Long id);

  void delete(Long id);

  List<Product> findAllByIds(List<Long> ids);
}
