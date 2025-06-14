package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.ICommentController;
import vn.shoestore.application.request.CreateCommentRequest;
import vn.shoestore.application.request.EditCommentRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.comment.ICommentUseCase;

@Component
@RequiredArgsConstructor
public class CommentControllerImpl implements ICommentController {
  private final ICommentUseCase iCommentUseCase;

  @Override
  public ResponseEntity<BaseResponse> createComment(CreateCommentRequest request) {
    iCommentUseCase.createComment(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> updateComment(EditCommentRequest request) {
    iCommentUseCase.editComment(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> deleteComment(Long id) {
    iCommentUseCase.deleteComment(id);
    return ResponseFactory.success();
  }
}
