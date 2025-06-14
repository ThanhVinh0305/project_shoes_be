package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.ProductAttachmentAdapter;
import vn.shoestore.domain.model.ProductAttachment;
import vn.shoestore.infrastructure.repository.entity.ProductAttachmentEntity;
import vn.shoestore.infrastructure.repository.repository.ProductAttachmentRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ProductAttachmentAdapterImpl implements ProductAttachmentAdapter {
  private final ProductAttachmentRepository productAttachmentRepository;

  @Override
  public List<ProductAttachment> findAllByProductIds(List<Long> productIds) {
    return ModelMapperUtils.mapList(
        productAttachmentRepository.findAllByProductIdIn(productIds), ProductAttachment.class);
  }

  @Override
  public void saveAll(List<ProductAttachment> productAttachments) {
    productAttachmentRepository.saveAll(
        ModelMapperUtils.mapList(productAttachments, ProductAttachmentEntity.class));
  }

  @Override
  public void deleteByIds(List<Long> ids) {
    productAttachmentRepository.deleteAllByIdInBatch(ids);
  }
}
