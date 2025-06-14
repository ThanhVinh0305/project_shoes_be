package vn.shoestore.usecases.logic.promotion;

import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.application.request.PromotionRequest;
import vn.shoestore.application.response.FindProductResponse;
import vn.shoestore.application.response.PromotionResponse;

public interface IPromotionUseCase {
  void saveOrUpdatePromotion(PromotionRequest request);

  void deletePromotion(Long promotionId);

  PromotionResponse getPromotion(Long promotionId);

  FindProductResponse findAllByConditions(FindPromotionRequest request);
}
