package vn.shoestore.domain.adapter;

import java.util.List;
import java.util.Optional;
import vn.shoestore.domain.model.ProductComment;
import vn.shoestore.domain.model.ProductCommentAttachment;

public interface ProductCommentAdapter {
  ProductComment save(ProductComment comment);

  void saveAllAttachment(List<ProductCommentAttachment> productCommentAttachments);

  List<ProductComment> getAllByProductId(Long productId);

  List<ProductCommentAttachment> getAllByCommentIdIn(List<Long> commentIds);

  Optional<ProductComment> getCommentByProductIdAndUserId(Long productId, Long userId);

  Optional<ProductComment> findById(Long id);

  void deleteProductComment(Long id);

  void deleteAllAttachment(List<Long> attachmentIds);
}
