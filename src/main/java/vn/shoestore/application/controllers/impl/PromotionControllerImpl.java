package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IPromotionController;
import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.application.request.PromotionRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.FindProductResponse;
import vn.shoestore.application.response.PromotionResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.promotion.IPromotionUseCase;

@Component
@RequiredArgsConstructor
public class PromotionControllerImpl implements IPromotionController {
  private final IPromotionUseCase iPromotionUseCase;

  @Override
  public ResponseEntity<BaseResponse> savePromotion(PromotionRequest request) {
    iPromotionUseCase.saveOrUpdatePromotion(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> deletePromotion(Long id) {
    iPromotionUseCase.deletePromotion(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse<PromotionResponse>> getById(Long id) {
    return ResponseFactory.success(iPromotionUseCase.getPromotion(id));
  }

  @Override
  public ResponseEntity<BaseResponse<FindProductResponse>> findByCondition(
      FindPromotionRequest request) {
    return ResponseFactory.success(iPromotionUseCase.findAllByConditions(request));
  }
}
