package vn.shoestore.application.controllers.open_api.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.open_api.IOpenApiCommentController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.CommentResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.comment.ICommentUseCase;

@Component
@RequiredArgsConstructor
public class OpenApiCommentControllerImpl implements IOpenApiCommentController {
  private final ICommentUseCase iCommentUseCase;

  @Override
  public ResponseEntity<BaseResponse<List<CommentResponse>>> getComments(Long productId) {
    return ResponseFactory.success(iCommentUseCase.getAllCommentByProducts(productId));
  }
}
