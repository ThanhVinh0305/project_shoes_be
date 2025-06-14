package vn.shoestore.usecases.logic.cart.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.NOT_VALID_USER_DETAILS;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.AddToCartRequest;
import vn.shoestore.application.request.UpdateCartAmountRequest;
import vn.shoestore.application.response.CartResponse;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.domain.adapter.CartAdapter;
import vn.shoestore.domain.adapter.ProductPropertiesAdapter;
import vn.shoestore.domain.model.Cart;
import vn.shoestore.domain.model.ProductCart;
import vn.shoestore.domain.model.ProductProperties;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.dto.ProductCartDTO;
import vn.shoestore.shared.exceptions.NotAuthorizedException;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.cart.ICartUseCase;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;

@UseCase
@RequiredArgsConstructor
public class CartUseCaseImpl implements ICartUseCase {

  private final CartAdapter cartAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;
  private final IGetProductUseCase iGetProductUseCase;

  @Override
  public CartResponse getCart() {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUser())) {
      throw new NotAuthorizedException(NOT_VALID_USER_DETAILS);
    }
    User user = customUserDetails.getUser();
    Cart cart = cartAdapter.getCartByUserId(user.getId());

    if (Objects.isNull(cart)) {
      return buildEmptyCart(user);
    }

    CartResponse response = CartResponse.builder().id(cart.getId()).userId(user.getId()).build();
    enrichCartInfo(response, cart);
    return response;
  }

  @Override
  @Transactional
  public void addToCart(AddToCartRequest request) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUser())) {
      throw new NotAuthorizedException(NOT_VALID_USER_DETAILS);
    }
    User user = customUserDetails.getUser();

    Cart cart = cartAdapter.getCartByUserId(user.getId());
    if (Objects.isNull(cart)) {
      cart =
          cartAdapter.save(
              Cart.builder()
                  .userId(user.getId())
                  .createdDate(LocalDateTime.now())
                  .updatedDate(LocalDateTime.now())
                  .build());
    }
    List<AddToCartRequest> data = Collections.singletonList(request);

    List<Long> productIds = ModelTransformUtils.getAttribute(data, AddToCartRequest::getProductId);
    List<Integer> sizes = ModelTransformUtils.getAttribute(data, AddToCartRequest::getSize);

    List<ProductProperties> productProperties =
        productPropertiesAdapter.findAllByProductIdInAndSizeInAndIsAble(productIds, sizes, true);

    List<ProductCart> productCarts = cartAdapter.getAllByCartId(cart.getId());

    List<Long> propertiesInCarts =
        ModelTransformUtils.getAttribute(productCarts, ProductCart::getProductPropertiesId);

    List<ProductProperties> notExistProperties =
        productProperties.stream().filter(e -> !propertiesInCarts.contains(e.getId())).toList();

    Map<Long, ProductProperties> productPropertiesMap =
        ModelTransformUtils.toMap(productProperties, ProductProperties::getId);

    saveNonExistCartProduct(notExistProperties, data, cart.getId());
    saveExistCartProduct(productCarts, data, productPropertiesMap);
  }

  @Override
  public void deleteProductInCart(List<Long> productPropertiesIds) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    User user = customUserDetails.getUser();
    Cart cart = cartAdapter.getCartByUserId(user.getId());
    if(cart == null) return;
    List<ProductCart> productCarts = cart.getProductCarts();
    List<Long> deletedIds =
        productCarts.stream()
            .filter(e -> productPropertiesIds.contains(e.getProductPropertiesId()))
            .map(ProductCart::getId)
            .toList();

    if (deletedIds.isEmpty()) return;
    cartAdapter.deleteProductCarts(deletedIds);
  }

  @Override
  @Transactional
  public void updateCartAmount(UpdateCartAmountRequest request) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUser())) {
      throw new NotAuthorizedException(NOT_VALID_USER_DETAILS);
    }
    User user = customUserDetails.getUser();
    Cart cart = cartAdapter.getCartByUserId(user.getId());
    if (cart == null) return;

    Optional<ProductCart> productCartOptional =
        cart.getProductCarts().stream()
            .filter(
                e -> Objects.equals(e.getProductPropertiesId(), request.getProductPropertiesId()))
            .findFirst();

    if (productCartOptional.isEmpty()) return;
    ProductCart productCart = productCartOptional.get();

    productCart.setAmount(request.getAmount());
    productCart.setAddedDate(LocalDateTime.now());

    cartAdapter.saveProductCart(Collections.singletonList(productCart));
  }

  private void saveNonExistCartProduct(
      List<ProductProperties> notExistProperties, List<AddToCartRequest> data, Long cartId) {
    if (notExistProperties.isEmpty()) return;
    List<ProductCart> savedProductCarts = new ArrayList<>();
    for (ProductProperties properties : notExistProperties) {
      Optional<AddToCartRequest> optionalData =
          data.stream()
              .filter(
                  e ->
                      Objects.equals(e.getProductId(), properties.getProductId())
                          && Objects.equals(e.getSize(), properties.getSize()))
              .findFirst();
      if (optionalData.isEmpty()) continue;
      savedProductCarts.add(
          ProductCart.builder()
              .cartId(cartId)
              .productPropertiesId(properties.getId())
              .addedDate(LocalDateTime.now())
              .amount(optionalData.get().getAmount())
              .build());
    }
    cartAdapter.saveProductCart(savedProductCarts);
  }

  private void saveExistCartProduct(
      List<ProductCart> productCarts,
      List<AddToCartRequest> data,
      Map<Long, ProductProperties> productPropertiesMap) {
    if (productCarts.isEmpty()) return;

    List<ProductCart> savedProductCarts = new ArrayList<>();
    for (ProductCart productCart : productCarts) {
      ProductProperties properties = productPropertiesMap.get(productCart.getProductPropertiesId());
      if (Objects.isNull(properties)) continue;

      Optional<AddToCartRequest> optionalData =
          data.stream()
              .filter(
                  e ->
                      Objects.equals(e.getProductId(), properties.getProductId())
                          && Objects.equals(e.getSize(), properties.getSize()))
              .findFirst();

      if (optionalData.isEmpty()) continue;
      AddToCartRequest addToCartData = optionalData.get();

      productCart.setAmount(productCart.getAmount() + addToCartData.getAmount());
      savedProductCarts.add(productCart);
    }

    cartAdapter.saveProductCart(savedProductCarts);
  }

  private void enrichCartInfo(CartResponse response, Cart cart) {
    List<ProductCart> productCarts = cart.getProductCarts();
    if (productCarts.isEmpty()) return;
    List<ProductCartDTO> productCartDTOS = new ArrayList<>();

    List<Long> productPropertiesIds =
        ModelTransformUtils.getAttribute(productCarts, ProductCart::getProductPropertiesId);

    List<ProductProperties> productProperties =
        productPropertiesAdapter.findAllIdIn(productPropertiesIds);

    List<Long> productIds =
        productProperties.stream().map(ProductProperties::getProductId).distinct().toList();

    List<ProductResponse> productResponses = iGetProductUseCase.findByIds(productIds);

    Map<Long, ProductResponse> productResponseMap =
        ModelTransformUtils.toMap(productResponses, ProductResponse::getId);

    Map<Long, ProductProperties> mapProductProperties =
        ModelTransformUtils.toMap(productProperties, ProductProperties::getId);

    for (ProductCart productCart : productCarts) {
      ProductCartDTO dto =
          ProductCartDTO.builder()
              .productPropertiesId(productCart.getProductPropertiesId())
              .amount(productCart.getAmount())
              .build();

      ProductProperties properties = mapProductProperties.get(productCart.getProductPropertiesId());
      dto.setSize(properties.getSize());
      dto.setProductId(properties.getProductId());

      ProductResponse productResponse = productResponseMap.get(properties.getProductId());

      if (Objects.isNull(productResponse)) {
        productCartDTOS.add(dto);
        continue;
      }
      dto.setProduct(productResponse);
      productCartDTOS.add(dto);
    }

    response.setProducts(productCartDTOS);
  }

  private CartResponse buildEmptyCart(User user) {
    return CartResponse.builder().userId(user.getId()).amount(0).build();
  }
}
