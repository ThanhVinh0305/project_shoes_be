package vn.shoestore.domain.adapter.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.ProductCommentAdapter;
import vn.shoestore.domain.model.ProductComment;
import vn.shoestore.domain.model.ProductCommentAttachment;
import vn.shoestore.infrastructure.repository.entity.ProductCommentAttachmentEntity;
import vn.shoestore.infrastructure.repository.entity.ProductCommentEntity;
import vn.shoestore.infrastructure.repository.repository.ProductCommentAttachmentRepository;
import vn.shoestore.infrastructure.repository.repository.ProductCommentRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

@Adapter
@RequiredArgsConstructor
public class ProductCommentAdapterImpl implements ProductCommentAdapter {

  private final ProductCommentRepository productCommentRepository;
  private final ProductCommentAttachmentRepository productCommentAttachmentRepository;

  @Override
  public ProductComment save(ProductComment comment) {
    return ModelMapperUtils.mapper(
        productCommentRepository.save(ModelMapperUtils.mapper(comment, ProductCommentEntity.class)),
        ProductComment.class);
  }

  @Override
  public void saveAllAttachment(List<ProductCommentAttachment> productCommentAttachments) {
    productCommentAttachmentRepository.saveAll(
        ModelMapperUtils.mapList(productCommentAttachments, ProductCommentAttachmentEntity.class));
  }

  @Override
  public List<ProductComment> getAllByProductId(Long productId) {
    return ModelMapperUtils.mapList(
        productCommentRepository.findAllByProductId(productId), ProductComment.class);
  }

  @Override
  public List<ProductCommentAttachment> getAllByCommentIdIn(List<Long> commentIds) {
    return ModelMapperUtils.mapList(
        productCommentAttachmentRepository.findAllByProductCommentIdIn(commentIds),
        ProductCommentAttachment.class);
  }

  @Override
  public Optional<ProductComment> getCommentByProductIdAndUserId(Long productId, Long userId) {
    ProductCommentEntity productCommentEntity =
        productCommentRepository.findAllByProductIdAndUserId(userId, productId);
    if (Objects.isNull(productCommentEntity)) return Optional.empty();
    return Optional.of(ModelMapperUtils.mapper(productCommentEntity, ProductComment.class));
  }

  @Override
  public Optional<ProductComment> findById(Long id) {
    Optional<ProductCommentEntity> optionalProductComment = productCommentRepository.findById(id);
    return optionalProductComment.map(
        productCommentEntity ->
            ModelMapperUtils.mapper(productCommentEntity, ProductComment.class));
  }

  @Override
  public void deleteProductComment(Long id) {
    productCommentRepository.deleteById(id);
  }

  @Override
  public void deleteAllAttachment(List<Long> attachmentIds) {
    productCommentAttachmentRepository.deleteAllByIdInBatch(attachmentIds);
  }
}
