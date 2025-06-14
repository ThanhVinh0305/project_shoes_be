package vn.shoestore.usecases.logic.cart;

import java.util.List;
import vn.shoestore.application.request.AddToCartRequest;
import vn.shoestore.application.request.UpdateCartAmountRequest;
import vn.shoestore.application.response.CartResponse;

public interface ICartUseCase {
  CartResponse getCart();

  void addToCart(AddToCartRequest request);

  void deleteProductInCart(List<Long> productPropertiesIds);

  void updateCartAmount(UpdateCartAmountRequest request);
}
