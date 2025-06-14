package vn.shoestore.application.controllers.impl;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.ICartController;
import vn.shoestore.application.request.AddToCartRequest;
import vn.shoestore.application.request.UpdateCartAmountRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.CartResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.cart.ICartUseCase;

@Component
@RequiredArgsConstructor
public class CartControllerImpl implements ICartController {
  private final ICartUseCase iCartUseCase;

  @Override
  public ResponseEntity<BaseResponse<CartResponse>> getCart() {
    return ResponseFactory.success(iCartUseCase.getCart());
  }

  @Override
  public ResponseEntity<BaseResponse> addToCard(AddToCartRequest request) {
    iCartUseCase.addToCart(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> deleteFromCart(Long id) {
    iCartUseCase.deleteProductInCart(Collections.singletonList(id));
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> updateAmount(UpdateCartAmountRequest request) {
    iCartUseCase.updateCartAmount(request);
    return ResponseFactory.success();
  }
}
