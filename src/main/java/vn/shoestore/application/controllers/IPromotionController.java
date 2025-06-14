package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.application.request.PromotionRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.FindProductResponse;
import vn.shoestore.application.response.PromotionResponse;

@RestController
@RequestMapping("/api/v1/promotions/")
public interface IPromotionController {

  @PostMapping("save-promotion")
  ResponseEntity<BaseResponse> savePromotion(@RequestBody @Valid PromotionRequest request);

  @DeleteMapping("delete-promotion/{id}")
  ResponseEntity<BaseResponse> deletePromotion(@PathVariable Long id);

  @GetMapping("get-by-id/{id}")
  ResponseEntity<BaseResponse<PromotionResponse>> getById(@PathVariable Long id);

  @PostMapping("get-promotion-by-filter")
  ResponseEntity<BaseResponse<FindProductResponse>> findByCondition(
      @RequestBody FindPromotionRequest request);
}
