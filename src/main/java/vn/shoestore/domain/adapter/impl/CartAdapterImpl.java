package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.CartAdapter;
import vn.shoestore.domain.model.Cart;
import vn.shoestore.domain.model.ProductCart;
import vn.shoestore.infrastructure.repository.entity.CartEntity;
import vn.shoestore.infrastructure.repository.entity.ProductCartEntity;
import vn.shoestore.infrastructure.repository.repository.CartRepository;
import vn.shoestore.infrastructure.repository.repository.ProductCartRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class CartAdapterImpl implements CartAdapter {
  private final CartRepository cartRepository;
  private final ProductCartRepository productCartRepository;

  @Override
  public Cart save(Cart cart) {
    return ModelMapperUtils.mapper(
        cartRepository.save(ModelMapperUtils.mapper(cart, CartEntity.class)), Cart.class);
  }

  @Override
  public void saveProductCart(List<ProductCart> productCarts) {
    productCartRepository.saveAll(ModelMapperUtils.mapList(productCarts, ProductCartEntity.class));
  }

  @Override
  public void enrichProductCart(Cart cart) {
    List<ProductCart> productCarts = getAllByCartId(cart.getId());
    cart.setProductCarts(productCarts);
  }

  @Override
  public void deleteProductCarts(List<Long> productCartIds) {
    productCartRepository.deleteAllByIdInBatch(productCartIds);
  }

  @Override
  public Cart getCartById(Long id) {
    Optional<CartEntity> cartOptional = cartRepository.findById(id);
    if (cartOptional.isEmpty()) return null;
    Cart cart = ModelMapperUtils.mapper(cartOptional.get(), Cart.class);
    enrichProductCart(cart);
    return cart;
  }

  @Override
  public List<ProductCart> getAllByCartId(Long cartId) {
    return ModelMapperUtils.mapList(
        productCartRepository.findAllByCartId(cartId), ProductCart.class);
  }

  @Override
  public Cart getCartByUserId(Long userId) {
    Optional<CartEntity> cartOptional = cartRepository.findByUserId(userId);
    if (cartOptional.isEmpty()) return null;
    Cart cart = ModelMapperUtils.mapper(cartOptional.get(), Cart.class);
    enrichProductCart(cart);
    return cart;
  }
}
