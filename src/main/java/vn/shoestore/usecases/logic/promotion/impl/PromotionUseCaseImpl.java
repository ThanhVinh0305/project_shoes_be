package vn.shoestore.usecases.logic.promotion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.application.request.PromotionRequest;
import vn.shoestore.application.response.FindProductResponse;
import vn.shoestore.application.response.PromotionResponse;
import vn.shoestore.domain.adapter.ProductAdapter;
import vn.shoestore.domain.adapter.ProductPromotionAdapter;
import vn.shoestore.domain.model.Product;
import vn.shoestore.domain.model.ProductPromotion;
import vn.shoestore.domain.model.Promotion;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.promotion.IPromotionUseCase;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static vn.shoestore.shared.constants.ExceptionMessage.*;

@UseCase
@RequiredArgsConstructor
public class PromotionUseCaseImpl implements IPromotionUseCase {
  private final ProductPromotionAdapter productPromotionAdapter;
  private final ProductAdapter productAdapter;

  @Override
  @Transactional
  public void saveOrUpdatePromotion(PromotionRequest request) {
    Promotion promotion = ModelMapperUtils.mapper(request, Promotion.class);
    LocalDateTime now = LocalDateTime.now();
    promotion.setCreatedDate(now);
    Promotion savedPromotion = productPromotionAdapter.savePromotion(promotion);

    if (Objects.nonNull(request.getId())) {
      deleteProductPromotion(savedPromotion.getId());
    }

    List<Long> productIds = request.getProductIds();
    if (productIds.isEmpty()) return;

    preValidateBeforeSave(productIds , request);
    List<ProductPromotion> productPromotions = new ArrayList<>();
    for (Long productId : productIds) {
      productPromotions.add(
          ProductPromotion.builder()
              .productId(productId)
              .promotionId(savedPromotion.getId())
              .createdDate(now)
              .build());
    }
    productPromotionAdapter.saveProductPromotions(productPromotions);
  }

  private void preValidateBeforeSave(List<Long> productIds, PromotionRequest request) {
    List<ProductPromotion> allProductPromotions =
        productPromotionAdapter.getProductPromotionByProductIds(productIds);

    List<Long> promotionIds =
        ModelTransformUtils.getAttribute(allProductPromotions, ProductPromotion::getPromotionId)
            .stream()
            .distinct()
            .toList();

    List<Promotion> allPromotions = productPromotionAdapter.getPromotionByIds(promotionIds);

    Map<Long, List<Long>> mapProductIdWithProductIds =
        allProductPromotions.stream()
            .collect(
                Collectors.groupingBy(
                    ProductPromotion::getProductId,
                    Collectors.mapping(ProductPromotion::getPromotionId, Collectors.toList())));
    for (Long productId : mapProductIdWithProductIds.keySet()) {
      List<Long> promoIds =
          mapProductIdWithProductIds.getOrDefault(productId, Collections.emptyList());

      List<Promotion> promotions =
          allPromotions.stream().filter(e -> promoIds.contains(e.getId())).toList();

      for (Promotion promotion : promotions) {
        if (request.getEndDate().isBefore(promotion.getStartDate())
            || request.getStartDate().isAfter(promotion.getEndDate())) continue;
        throw new InputNotValidException(String.format(PRODUCT_IS_HAS_PROMOTION, productId));
      }
    }
  }

  @Override
  public void deletePromotion(Long promotion) {
    productPromotionAdapter.deletePromotion(promotion);
    deleteProductPromotion(promotion);
  }

  private void deleteProductPromotion(Long promotion) {
    List<ProductPromotion> productPromotions =
        productPromotionAdapter.findAllByPromotionId(promotion);

    List<Long> productIds =
        ModelTransformUtils.getAttribute(productPromotions, ProductPromotion::getProductId);

    productPromotionAdapter.deleteAllByProductIds(promotion, productIds);
  }

  @Override
  public PromotionResponse getPromotion(Long promotionId) {
    Optional<Promotion> optionalPromotion = productPromotionAdapter.findById(promotionId);
    if (optionalPromotion.isEmpty()) {
      throw new InputNotValidException(PROMOTION_NOT_FOUND);
    }

    Promotion promotion = optionalPromotion.get();

    List<ProductPromotion> productPromotions =
        productPromotionAdapter.findAllByPromotionId(promotionId);

    List<Long> productIds =
        ModelTransformUtils.getAttribute(productPromotions, ProductPromotion::getProductId);

    List<Product> products = productAdapter.findAllByIds(productIds);

    PromotionResponse response = ModelMapperUtils.mapper(promotion, PromotionResponse.class);
    response.setProducts(products);
    return response;
  }

  @Override
  public FindProductResponse findAllByConditions(FindPromotionRequest request) {
    Page<Promotion> page = productPromotionAdapter.findPromotionByCondition(request);
    FindProductResponse response = new FindProductResponse();

    response.setTotal(page.getTotalElements());
    response.setData(page.getContent());
    return response;
  }
}
