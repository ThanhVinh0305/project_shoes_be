package vn.shoestore.usecases.logic.comment.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.CreateCommentRequest;
import vn.shoestore.application.request.EditCommentRequest;
import vn.shoestore.application.response.CommentResponse;
import vn.shoestore.domain.adapter.BillAdapter;
import vn.shoestore.domain.adapter.ProductCommentAdapter;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.ProductComment;
import vn.shoestore.domain.model.ProductCommentAttachment;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.comment.ICommentUseCase;

@UseCase
@RequiredArgsConstructor
public class CommentUseCaseImpl implements ICommentUseCase {
  private final ProductCommentAdapter productCommentAdapter;
  private final BillAdapter billAdapter;
  private final UserAdapter userAdapter;

  @Override
  @Transactional
  public void createComment(CreateCommentRequest request) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    Optional<ProductComment> comment =
        productCommentAdapter.getCommentByProductIdAndUserId(
            customUserDetails.getUser().getId(), request.getProductId());

    if (comment.isPresent()) {
      throw new InputNotValidException(YOU_ARE_ALREADY_COMMENT);
    }

    ProductComment savedComment =
        productCommentAdapter.save(
            ProductComment.builder()
                .userId(customUserDetails.getUser().getId())
                .star(request.getStar())
                .productId(request.getProductId())
                .comment(request.getComment())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build());

    if (Objects.nonNull(request.getAttachments()) && !request.getAttachments().isEmpty()) {
      saveAttachments(request.getAttachments(), savedComment.getId());
    }
  }

  private void saveAttachments(List<String> productAttachments, Long commentId) {
    List<ProductCommentAttachment> attachments = new ArrayList<>();
    for (String attachment : productAttachments) {
      attachments.add(
          ProductCommentAttachment.builder()
              .productCommentId(commentId)
              .attachment(attachment)
              .build());
    }
    productCommentAdapter.saveAllAttachment(attachments);
  }

  @Override
  @Transactional
  public void editComment(EditCommentRequest request) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    Optional<ProductComment> productCommentOptional =
        productCommentAdapter.findById(request.getId());
    if (productCommentOptional.isEmpty()) {
      throw new InputNotValidException(COMMENT_NOT_EXIST);
    }

    ProductComment productComment = productCommentOptional.get();
    if (!Objects.equals(customUserDetails.getUser().getId(), productComment.getUserId())) {
      throw new InputNotValidException(COMMENT_NOT_EXIST);
    }

    productComment.setStar(request.getStar());
    productComment.setComment(request.getComment());
    productComment.setUpdatedDate(LocalDateTime.now());

    productCommentAdapter.save(productComment);

    List<ProductCommentAttachment> productCommentAttachments =
        productCommentAdapter.getAllByCommentIdIn(
            Collections.singletonList(productComment.getId()));

    if (!productCommentAttachments.isEmpty()) {
      productCommentAdapter.deleteAllAttachment(
          ModelTransformUtils.getAttribute(
              productCommentAttachments, ProductCommentAttachment::getId));
    }

    if (Objects.nonNull(request.getAttachments()) && !request.getAttachments().isEmpty()) {
      saveAttachments(request.getAttachments(), productComment.getId());
    }
  }

  @Override
  public List<CommentResponse> getAllCommentByProducts(Long productId) {
    List<ProductComment> allComments = productCommentAdapter.getAllByProductId(productId);
    if (allComments.isEmpty()) return Collections.emptyList();

    List<CommentResponse> commentResponses =
        ModelMapperUtils.mapList(allComments, CommentResponse.class);

    enrichUserInfo(commentResponses);
    enrichAttachment(commentResponses);

    commentResponses.sort(
        Comparator.comparing(
            CommentResponse::getCreatedDate, Comparator.nullsLast(Comparator.reverseOrder())));

    return commentResponses;
  }

  private void enrichUserInfo(List<CommentResponse> commentResponses) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();

    List<Long> userIds =
        ModelTransformUtils.getAttribute(commentResponses, CommentResponse::getUserId);

    List<User> users = userAdapter.getUserByIdIn(userIds);
    Map<Long, User> userMap = ModelTransformUtils.toMap(users, User::getId);

    for (CommentResponse response : commentResponses) {
      if (Objects.nonNull(customUserDetails)
          && Objects.nonNull(customUserDetails.getUser().getId())
          && Objects.equals(customUserDetails.getUser().getId(), response.getUserId())) {
        response.setCanEdit(true);
      }

      User user = userMap.get(response.getUserId());

      if (Objects.isNull(user)) continue;

      response.setUsername(user.getUsername());
      response.setFullName(
          (Objects.nonNull(user.getFirstName()) ? user.getFirstName() : "")
              + (Objects.nonNull(user.getLastName()) ? user.getLastName() : ""));
    }
  }

  private void enrichAttachment(List<CommentResponse> commentResponses) {
    List<Long> ids = ModelTransformUtils.getAttribute(commentResponses, CommentResponse::getId);
    List<ProductCommentAttachment> productCommentAttachments =
        productCommentAdapter.getAllByCommentIdIn(ids);

    Map<Long, List<String>> mapCommentWithAttachments =
        productCommentAttachments.stream()
            .collect(
                Collectors.groupingBy(
                    ProductCommentAttachment::getProductCommentId,
                    Collectors.mapping(
                        ProductCommentAttachment::getAttachment, Collectors.toList())));

    for (CommentResponse response : commentResponses) {
      response.setAttachments(
          mapCommentWithAttachments.getOrDefault(response.getId(), Collections.emptyList()));
    }
  }

  @Override
  public void deleteComment(Long commentId) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    Optional<ProductComment> productCommentOptional = productCommentAdapter.findById(commentId);
    if (productCommentOptional.isEmpty()) {
      throw new InputNotValidException(CANNOT_DELETE_COMMENT);
    }

    ProductComment productComment = productCommentOptional.get();
    if (!Objects.equals(customUserDetails.getUser().getId(), productComment.getUserId())) {
      throw new InputNotValidException(CANNOT_DELETE_COMMENT);
    }
    productCommentAdapter.deleteProductComment(commentId);
  }
}
