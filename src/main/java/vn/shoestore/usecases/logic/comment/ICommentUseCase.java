package vn.shoestore.usecases.logic.comment;

import java.util.List;
import vn.shoestore.application.request.CreateCommentRequest;
import vn.shoestore.application.request.EditCommentRequest;
import vn.shoestore.application.response.CommentResponse;

public interface ICommentUseCase {
  void createComment(CreateCommentRequest request);

  void editComment(EditCommentRequest request);

  List<CommentResponse> getAllCommentByProducts(Long productId);

  void deleteComment(Long commentId);
}
