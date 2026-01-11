package vn.shoestore.usecases.logic.recommend;

import vn.shoestore.application.response.RecommendBlocksResponse;

public interface IRecommendBlockUseCase {
  RecommendBlocksResponse getBlocks(Long userId);
}


