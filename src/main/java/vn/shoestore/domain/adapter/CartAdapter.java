package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.Cart;
import vn.shoestore.domain.model.ProductCart;

import java.util.List;

public interface CartAdapter {
  Cart save(Cart cart);

  void saveProductCart(List<ProductCart> productCarts);

  void enrichProductCart(Cart cart);

  void deleteProductCarts(List<Long> productCartIds);

  Cart getCartById(Long id);

  List<ProductCart> getAllByCartId(Long cartId);

  Cart getCartByUserId(Long userId);
}
