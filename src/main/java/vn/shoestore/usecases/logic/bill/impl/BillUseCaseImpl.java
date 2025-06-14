package vn.shoestore.usecases.logic.bill.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.*;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.BuyNowRequest;
import vn.shoestore.application.request.CreateBillRequest;
import vn.shoestore.application.request.GetAllBillRequest;
import vn.shoestore.application.response.BillResponse;
import vn.shoestore.application.response.BillResponseData;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.domain.adapter.*;
import vn.shoestore.domain.model.*;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.BuyNowProductDTO;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.dto.ProductBilDTO;
import vn.shoestore.shared.enums.BillStatusEnum;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.exceptions.NotAuthorizedException;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.bill.IBillUseCase;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;

@UseCase
@RequiredArgsConstructor
public class BillUseCaseImpl implements IBillUseCase {
  private final BillAdapter billAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;
  private final ImportTicketAdapter importTicketAdapter;
  private final CartAdapter cartAdapter;
  private final ProductAdapter productAdapter;
  private final IGetProductUseCase iGetProductUseCase;

  @Override
  @Transactional
  public BillResponseData createBill(CreateBillRequest request, Boolean isOnlineTransaction) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUser())) {
      throw new NotAuthorizedException(NOT_VALID_USER_DETAILS);
    }
    User user = customUserDetails.getUser();
    Cart cart = cartAdapter.getCartByUserId(user.getId());

    if (Objects.isNull(cart) || cart.getProductCarts().isEmpty()) {
      throw new InputNotValidException(CART_STATUS_NOT_VALID);
    }

    List<Long> productInCarts =
        ModelTransformUtils.getAttribute(
            cart.getProductCarts(), ProductCart::getProductPropertiesId);

    List<Long> productPropertiesNotInCarts =
        request.getProductPropertiesIds().stream()
            .filter(e -> !productInCarts.contains(e))
            .toList();

    if (!productPropertiesNotInCarts.isEmpty()) {
      throw new InputNotValidException(PRODUCT_NOT_IN_CART);
    }

    List<ProductCart> productCarts =
        cart.getProductCarts().stream()
            .filter(e -> request.getProductPropertiesIds().contains(e.getProductPropertiesId()))
            .toList();

    List<ProductAmount> productAmounts =
        importTicketAdapter.getAllProductPropertiesIdsForUpdate(request.getProductPropertiesIds());

    List<ProductProperties> allProductProperties =
        productPropertiesAdapter.findAllIdIn(request.getProductPropertiesIds());

    validateAmount(productCarts, productAmounts, allProductProperties);
    // tạo hoá đơn
    extractProductInStorage(productCarts, productAmounts);
    return createBill(request, productCarts, allProductProperties, isOnlineTransaction);
  }

  @Override
  public void adminConfirmBill(Long billId) {
    Bill bill = billAdapter.findBillById(billId);
    bill.setStatus(BillStatusEnum.PURCHASE.getStatus());
    billAdapter.saveBill(bill);
  }

  @Override
  @Transactional
  public BillResponseData buyNow(BuyNowRequest request, Boolean isOnlineTransaction) {
    List<BuyNowProductDTO> products = request.getProducts();

    List<Long> productIds =
        ModelTransformUtils.getAttribute(products, BuyNowProductDTO::getProductId);

    List<Integer> sizes = ModelTransformUtils.getAttribute(products, BuyNowProductDTO::getSize);

    List<ProductProperties> properties =
        productPropertiesAdapter.findAllByProductIdInAndSizeInAndIsAble(productIds, sizes, true);

    List<ProductAmount> productAmounts =
        importTicketAdapter.getAllProductPropertiesIdsForUpdate(
            ModelTransformUtils.getAttribute(properties, ProductProperties::getId));

    validateAmountBuyNow(products, productAmounts, properties);

    extractProductInStorageForBuyNow(request.getProducts(), productAmounts, properties);
    return createBillForBuyNow(request, properties, isOnlineTransaction);
  }

  @Override
  public BillResponse getBillByFilter(GetAllBillRequest request) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    User user = customUserDetails.getUser();
    Long userId = null;
    if (!user.isAdmin()) {
      userId = user.getId();
    }

    Page<Bill> billPage = billAdapter.findAllByByConditions(request, userId);
    if (billPage.isEmpty()) return BillResponse.builder().build();

    List<Bill> bills = billPage.getContent();
    List<BillResponseData> data = ModelMapperUtils.mapList(bills, BillResponseData.class);
    enrichProductBill(data);

    return BillResponse.builder().data(data).total(billPage.getTotalElements()).build();
  }

  @Override
  public BillResponseData getBillById(Long id) {
    Bill bill = billAdapter.findBillById(id);
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    User user = customUserDetails.getUser();
    if (!user.isAdmin() && !Objects.equals(user.getId(), bill.getUserId())) {
      throw new InputNotValidException(YOU_CANNOT_VIEW_THIS_BILL);
    }
    BillResponseData data = ModelMapperUtils.mapper(bill, BillResponseData.class);
    enrichProductBill(Collections.singletonList(data));
    return data;
  }

  @Override
  @Transactional
  public void cancelBill(Long billId) {
    Bill bill = billAdapter.findBillById(billId);
    List<ProductBill> productBills =
        billAdapter.findAllByBillIdIn(Collections.singletonList(billId));

    List<ProductAmount> productAmounts =
        importTicketAdapter.getAllProductPropertiesIdsForUpdate(
            ModelTransformUtils.getAttribute(productBills, ProductBill::getProductPropertiesId));

    Map<Long, ProductBill> mapProductBills =
        ModelTransformUtils.toMap(productBills, ProductBill::getProductPropertiesId);
    for (ProductAmount productAmount : productAmounts) {
      ProductBill productBill = mapProductBills.get(productAmount.getProductPropertiesId());
      if (Objects.isNull(productBill)) continue;
      productAmount.setAmount(productAmount.getAmount() + productBill.getAmount());
    }

    bill.setStatus(BillStatusEnum.CANCEL.getStatus());
    billAdapter.saveBill(bill);
    importTicketAdapter.saveProductAmount(productAmounts);
  }

  private void enrichProductBill(List<BillResponseData> data) {
    List<ProductBill> productBills =
        billAdapter.findAllByBillIdIn(
            ModelTransformUtils.getAttribute(data, BillResponseData::getId));

    List<Long> productPropertiesIds =
        ModelTransformUtils.getAttribute(productBills, ProductBill::getProductPropertiesId).stream()
            .distinct()
            .toList();

    List<ProductProperties> properties = productPropertiesAdapter.findAllIdIn(productPropertiesIds);

    List<Long> productIds =
        ModelTransformUtils.getAttribute(properties, ProductProperties::getProductId);

    List<ProductResponse> productResponses = iGetProductUseCase.findByIds(productIds);

    Map<Long, List<ProductBill>> mapProductBills =
        ModelTransformUtils.toMapList(productBills, ProductBill::getBillId);

    Map<Long, ProductProperties> mapProductProperties =
        ModelTransformUtils.toMap(properties, ProductProperties::getId);

    Map<Long, ProductResponse> mapProductResponse =
        ModelTransformUtils.toMap(productResponses, ProductResponse::getId);

    for (BillResponseData responseData : data) {
      List<ProductBill> productInBills =
          mapProductBills.getOrDefault(responseData.getId(), Collections.emptyList());
      if (productInBills.isEmpty()) continue;
      List<ProductBilDTO> products = new ArrayList<>();
      for (ProductBill productBill : productInBills) {
        ProductProperties productProperties =
            mapProductProperties.get(productBill.getProductPropertiesId());
        if (Objects.isNull(productProperties)) continue;
        ProductResponse productResponse = mapProductResponse.get(productProperties.getProductId());
        if (Objects.isNull(productResponse)) continue;
        products.add(
            ProductBilDTO.builder()
                .productId(productResponse.getId())
                .productPropertiesId(productProperties.getId())
                .price(productBill.getTotalPrice())
                .size(productProperties.getSize())
                .product(productResponse)
                .amount(productBill.getAmount())
                .build());
      }
      responseData.setProducts(products);
    }
  }

  private void validateAmount(
      List<ProductCart> productCarts,
      List<ProductAmount> productAmounts,
      List<ProductProperties> allProductProperties) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(allProductProperties, ProductProperties::getProductId);

    Map<Long, ProductProperties> mapProductProperties =
        ModelTransformUtils.toMap(allProductProperties, ProductProperties::getId);

    List<Product> products = productAdapter.findAllByIds(productIds);

    Map<Long, Product> mapProducts = ModelTransformUtils.toMap(products, Product::getId);
    Map<Long, ProductAmount> mapProductAmount =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    for (ProductCart productCart : productCarts) {
      ProductAmount productAmount = mapProductAmount.get(productCart.getProductPropertiesId());
      if (Objects.nonNull(productAmount) && productAmount.getAmount() >= productCart.getAmount())
        continue;
      ProductProperties properties = mapProductProperties.get(productCart.getProductPropertiesId());
      Product product =
          mapProducts.get(Objects.nonNull(properties) ? properties.getProductId() : 0L);
      throw new InputNotValidException(
          String.format(
              "Số lượng sản phẩm %s size %s không đủ . Không thể thanh tạo đơn",
              Objects.isNull(product) ? "" : product.getName(),
              Objects.nonNull(properties) ? String.valueOf(properties.getSize()) : ""));
    }
  }

  private void validateAmountBuyNow(
      List<BuyNowProductDTO> productDTOSs,
      List<ProductAmount> productAmounts,
      List<ProductProperties> properties) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(properties, ProductProperties::getProductId);

    List<Product> products = productAdapter.findAllByIds(productIds);
    Map<Long, ProductAmount> mapProductAmount =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    Map<Long, Product> mapProducts = ModelTransformUtils.toMap(products, Product::getId);

    for (BuyNowProductDTO dto : productDTOSs) {
      Optional<ProductProperties> optionalProductProperties =
          properties.stream()
              .filter(
                  e ->
                      Objects.equals(e.getSize(), dto.getSize())
                          && Objects.equals(e.getProductId(), dto.getProductId()))
              .findFirst();

      if (optionalProductProperties.isEmpty()) {
        throw new InputNotValidException(PRODUCT_NOT_FOUND);
      }

      ProductProperties productProperties = optionalProductProperties.get();
      ProductAmount productAmount = mapProductAmount.get(productProperties.getId());
      if (Objects.nonNull(productAmount) && productAmount.getAmount() >= dto.getAmount()) continue;
      Product product = mapProducts.get(productProperties.getProductId());
      throw new InputNotValidException(
          String.format(
              "Số lượng sản phẩm %s size %s không đủ . Không thể thanh tạo đơn",
              Objects.isNull(product) ? "" : product.getName(), productProperties.getSize()));
    }
  }

  private void extractProductInStorage(
      List<ProductCart> productCarts, List<ProductAmount> productAmounts) {
    Map<Long, ProductAmount> mapProductAmount =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    for (ProductCart productCart : productCarts) {
      ProductAmount productAmount = mapProductAmount.get(productCart.getProductPropertiesId());
      if (productAmount == null) continue;

      productAmount.setAmount(Math.max(productAmount.getAmount() - productCart.getAmount(), 0));
    }

    importTicketAdapter.saveProductAmount(productAmounts);
  }

  private BillResponseData createBill(
      CreateBillRequest request,
      List<ProductCart> productCarts,
      List<ProductProperties> allProductProperties,
      Boolean isOnlineTransaction) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    List<Long> productIds =
        ModelTransformUtils.getAttribute(allProductProperties, ProductProperties::getProductId);
    List<ProductResponse> productResponses = iGetProductUseCase.findByIds(productIds);

    Map<Long, ProductProperties> mapProductProperties =
        ModelTransformUtils.toMap(allProductProperties, ProductProperties::getId);

    Map<Long, ProductResponse> mapProductResponse =
        ModelTransformUtils.toMap(productResponses, ProductResponse::getId);

    User user = customUserDetails.getUser();

    Bill bill =
        Bill.builder()
            .createdBy(user.getId())
            .userId(user.getId())
            .createdDate(LocalDateTime.now())
            .status(BillStatusEnum.CREATED.getStatus())
            .isOnlineTransaction(isOnlineTransaction)
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .build();

    Bill savedBill = billAdapter.saveBill(bill);

    List<ProductBill> productBills = new ArrayList<>();

    for (ProductCart productCart : productCarts) {
      ProductProperties properties = mapProductProperties.get(productCart.getProductPropertiesId());
      if (properties == null) continue;
      ProductResponse productResponse = mapProductResponse.get(properties.getProductId());
      if (productResponse == null) continue;
      productBills.add(
          ProductBill.builder()
              .billId(savedBill.getId())
              .productPropertiesId(properties.getId())
              .amount(productCart.getAmount())
              .price(productResponse.getPrice())
              .promotionPrice(productResponse.getPromotionPrice())
              .promotionId(productResponse.getPromotionId())
              .build());
    }

    billAdapter.saveProductBill(productBills);
    Double total =
        productBills.stream()
            .filter(e -> Objects.nonNull(e.getTotalPrice()))
            .mapToDouble(ProductBill::getTotalPrice)
            .sum();

    savedBill.setTotal(total);
    billAdapter.saveBill(savedBill);
    cartAdapter.deleteProductCarts(
        ModelTransformUtils.getAttribute(productCarts, ProductCart::getId));
    BillResponseData response = ModelMapperUtils.mapper(savedBill, BillResponseData.class);
    enrichProductBill(Collections.singletonList(response));
    return response;
  }

  private BillResponseData createBillForBuyNow(
      BuyNowRequest request,
      List<ProductProperties> allProductProperties,
      Boolean isOnlineTransaction) {
    CustomUserDetails customUserDetails = AuthUtils.getAuthUserDetails();
    List<Long> productIds =
        ModelTransformUtils.getAttribute(allProductProperties, ProductProperties::getProductId);
    List<ProductResponse> productResponses = iGetProductUseCase.findByIds(productIds);

    User user = customUserDetails.getUser();
    Map<Long, ProductResponse> mapProductResponse =
        ModelTransformUtils.toMap(productResponses, ProductResponse::getId);

    Bill bill =
        Bill.builder()
            .createdBy(user.getId())
            .userId(user.getId())
            .createdDate(LocalDateTime.now())
            .status(BillStatusEnum.CREATED.getStatus())
            .isOnlineTransaction(isOnlineTransaction)
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .build();
    List<ProductBill> productBills = new ArrayList<>();

    Bill savedBill = billAdapter.saveBill(bill);

    for (BuyNowProductDTO dto : request.getProducts()) {
      Optional<ProductProperties> optionalProductProperties =
          allProductProperties.stream()
              .filter(
                  e ->
                      Objects.equals(e.getSize(), dto.getSize())
                          && Objects.equals(e.getProductId(), dto.getProductId()))
              .findFirst();
      if (optionalProductProperties.isEmpty()) continue;
      ProductProperties properties = optionalProductProperties.get();
      ProductResponse productResponse = mapProductResponse.get(properties.getProductId());
      if (productResponse == null) continue;
      productBills.add(
          ProductBill.builder()
              .billId(savedBill.getId())
              .productPropertiesId(properties.getId())
              .amount(dto.getAmount())
              .price(productResponse.getPrice())
              .promotionPrice(productResponse.getPromotionPrice())
              .promotionId(productResponse.getPromotionId())
              .build());
    }

    Double total =
        productBills.stream()
            .filter(e -> Objects.nonNull(e.getTotalPrice()))
            .mapToDouble(ProductBill::getTotalPrice)
            .sum();

    billAdapter.saveProductBill(productBills);

    savedBill.setTotal(total);
    savedBill = billAdapter.saveBill(savedBill);

    BillResponseData response = ModelMapperUtils.mapper(savedBill, BillResponseData.class);
    enrichProductBill(Collections.singletonList(response));
    return response;
  }

  private void extractProductInStorageForBuyNow(
      List<BuyNowProductDTO> productDTOSs,
      List<ProductAmount> productAmounts,
      List<ProductProperties> allProductProperties) {
    Map<Long, ProductAmount> mapProductAmount =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    for (BuyNowProductDTO dto : productDTOSs) {
      Optional<ProductProperties> optionalProductProperties =
          allProductProperties.stream()
              .filter(
                  e ->
                      Objects.equals(e.getSize(), dto.getSize())
                          && Objects.equals(e.getProductId(), dto.getProductId()))
              .findFirst();
      if (optionalProductProperties.isEmpty()) continue;
      ProductAmount productAmount = mapProductAmount.get(optionalProductProperties.get().getId());
      if (productAmount == null) continue;

      productAmount.setAmount(Math.max(productAmount.getAmount() - dto.getAmount(), 0));
    }

    importTicketAdapter.saveProductAmount(productAmounts);
  }
}
