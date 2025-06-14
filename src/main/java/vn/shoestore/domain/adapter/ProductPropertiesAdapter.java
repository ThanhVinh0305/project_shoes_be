package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ProductProperties;

import java.util.List;

public interface ProductPropertiesAdapter {
  void saveAll(List<ProductProperties> productProperties);

  List<ProductProperties> getAllByProductIdInAndIsAble(List<Long> productIds, Boolean isAble);

  List<ProductProperties> findAllByProductIdInAndSizeInAndIsAble(
      List<Long> productIds, List<Integer> sizes, Boolean isAble);

  List<ProductProperties> findAllIdIn(List<Long> ids);

  List<ProductProperties> findAllIdInForUpdate(List<Long> ids);

}
