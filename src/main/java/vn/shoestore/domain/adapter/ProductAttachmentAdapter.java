package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ProductAttachment;

import java.util.List;

public interface ProductAttachmentAdapter {
  List<ProductAttachment> findAllByProductIds(List<Long> productIds);

  void saveAll(List<ProductAttachment> productAttachments);

  void deleteByIds(List<Long> ids);
}
