package vn.shoestore.application.controllers.open_api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.controllers.open_api.IOpenApiRecommendController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.RecommendBlocksResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.recommend.IRecommendBlockUseCase;

@RestController
@RequiredArgsConstructor
public class OpenApiRecommendControllerImpl implements IOpenApiRecommendController {

  private final IRecommendBlockUseCase recommendBlockUseCase;

  @Override
  public ResponseEntity<BaseResponse<RecommendBlocksResponse>> getBlocks(Long userId) {
    return ResponseFactory.success(recommendBlockUseCase.getBlocks(userId));
  }
}

