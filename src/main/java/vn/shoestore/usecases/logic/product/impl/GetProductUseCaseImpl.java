package vn.shoestore.usecases.logic.product.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.PRODUCT_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import vn.shoestore.application.request.SearchProductRequest;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.application.response.SearchProductResponse;
import vn.shoestore.domain.adapter.*;
import vn.shoestore.domain.model.*;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.SizeAmountDTO;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;

@UseCase
@RequiredArgsConstructor
public class GetProductUseCaseImpl implements IGetProductUseCase {
  private final ProductAdapter productAdapter;
  private final ProductBrandAdapter productBrandAdapter;
  private final BrandAdapter brandAdapter;
  private final ProductCategoryAdapter productCategoryAdapter;
  private final CategoryAdapter categoryAdapter;
  private final ProductAttachmentAdapter productAttachmentAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;
  private final ProductPromotionAdapter productPromotionAdapter;
  private final ImportTicketAdapter importTicketAdapter;

  @Override
  public SearchProductResponse searchProduct(SearchProductRequest request) {
    Page<Long> productPage = productAdapter.getProductByCondition(request);
    if (productPage.isEmpty()) return SearchProductResponse.builder().build();

    List<Long> productIds = productPage.getContent();
    List<Product> products = productAdapter.findAllByIds(productIds);

    List<ProductResponse> productResponses =
        ModelMapperUtils.mapList(products, ProductResponse.class);
    enrichInfo(productResponses);

    return SearchProductResponse.builder()
        .data(productResponses)
        .total(productPage.getTotalElements())
        .build();
  }

  @Override
  public ProductResponse findOne(Long productId) {
    Product product = productAdapter.getProductById(productId);
    if (Objects.isNull(product)) {
      throw new InputNotValidException(PRODUCT_NOT_FOUND);
    }
    ProductResponse productResponse = ModelMapperUtils.mapper(product, ProductResponse.class);
    enrichInfo(Collections.singletonList(productResponse));
    return productResponse;
  }

  @Override
  public List<ProductResponse> findByIds(List<Long> ids) {
    List<Product> products = productAdapter.findAllByIds(ids);
    List<ProductResponse> productResponses =
        ModelMapperUtils.mapList(products, ProductResponse.class);
    enrichInfo(productResponses);
    return productResponses;
  }

  private void enrichInfo(List<ProductResponse> productResponses) {
    enrichBrand(productResponses);
    enrichCategories(productResponses);
    enrichUrls(productResponses);
    enrichSize(productResponses);
    enrichPromotion(productResponses);
  }

  private void enrichPromotion(List<ProductResponse> productResponses) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(productResponses, ProductResponse::getId);

    List<ProductPromotion> productPromotions =
        productPromotionAdapter.getProductPromotionByProductIds(productIds);

    List<Long> promotionIds =
        productPromotions.stream().map(ProductPromotion::getPromotionId).distinct().toList();

    List<Promotion> allPromotions = productPromotionAdapter.getPromotionByIds(promotionIds);
    Map<Long, List<Long>> mapProductIdWithPromotions =
        productPromotions.stream()
            .collect(
                Collectors.groupingBy(
                    ProductPromotion::getProductId,
                    Collectors.mapping(ProductPromotion::getPromotionId, Collectors.toList())));
    LocalDateTime now = LocalDateTime.now();
    for (ProductResponse response : productResponses) {
      List<Long> responsePromoId =
          mapProductIdWithPromotions.getOrDefault(response.getId(), Collections.emptyList());
      List<Promotion> promotions =
          allPromotions.stream().filter(e -> responsePromoId.contains(e.getId())).toList();
      if (promotions.isEmpty()) continue;

      for (Promotion promotion : promotions) {
        if (now.isAfter(promotion.getEndDate()) || now.isBefore(promotion.getStartDate())) continue;
        response.setPromotionId(promotion.getId());
        response.setIsPromotion(true);
        response.setPercentDiscount(promotion.getPercentDiscount());
        response.setPromotionPrice(
            (double)
                (response.getPrice()
                    - (response.getPrice() * promotion.getPercentDiscount() / 100)));
        break;
      }
    }
  }

  private void enrichSize(List<ProductResponse> productResponses) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(productResponses, ProductResponse::getId);

    List<ProductProperties> productProperties =
        productPropertiesAdapter.getAllByProductIdInAndIsAble(productIds, true);

    Map<Long, List<ProductProperties>> mapProperties =
        productProperties.stream().collect(Collectors.groupingBy(ProductProperties::getProductId));

    List<ProductAmount> productAmounts =
        importTicketAdapter.getAllProductPropertiesIds(
            ModelTransformUtils.getAttribute(productProperties, ProductProperties::getId));

    Map<Long, ProductAmount> productAmountMap =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    for (ProductResponse response : productResponses) {
      List<ProductProperties> properties =
          mapProperties.getOrDefault(response.getId(), Collections.emptyList());
      if (properties.isEmpty()) continue;
      List<SizeAmountDTO> sizes = new ArrayList<>();
      for (ProductProperties prop : properties) {
        ProductAmount productAmount = productAmountMap.get(prop.getId());
        sizes.add(
            SizeAmountDTO.builder()
                .id(prop.getId())
                .size(prop.getSize())
                .amount(Objects.nonNull(productAmount) ? productAmount.getAmount() : 0)
                .build());
      }

      response.setSizes(sizes);
    }
  }

  private void enrichCategories(List<ProductResponse> productResponses) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(productResponses, ProductResponse::getId);

    List<ProductCategory> productCategories =
        productCategoryAdapter.findAllByProductIds(productIds);

    List<Long> categoryIds =
        ModelTransformUtils.getAttribute(productCategories, ProductCategory::getCategoryId);

    List<Category> categories = categoryAdapter.findAllByIdIn(categoryIds);

    Map<Long, List<Long>> mapByProducts =
        productCategories.stream()
            .collect(
                Collectors.groupingBy(
                    ProductCategory::getProductId,
                    Collectors.mapping(ProductCategory::getCategoryId, Collectors.toList())));

    for (ProductResponse response : productResponses) {
      List<Long> categoryIdOfProducts =
          mapByProducts.getOrDefault(response.getId(), Collections.emptyList());

      List<Category> categoryOfProducts =
          categories.stream().filter(e -> categoryIdOfProducts.contains(e.getId())).toList();

      if (categoryOfProducts.isEmpty()) continue;
      response.setCategories(categoryOfProducts);
    }
  }

  private void enrichUrls(List<ProductResponse> productResponses) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(productResponses, ProductResponse::getId);
    List<ProductAttachment> productAttachments =
        productAttachmentAdapter.findAllByProductIds(productIds);

    Map<Long, List<ProductAttachment>> mapProductAttachments =
        productAttachments.stream().collect(Collectors.groupingBy(ProductAttachment::getProductId));

    for (ProductResponse response : productResponses) {
      List<ProductAttachment> attachments =
          mapProductAttachments.getOrDefault(response.getId(), Collections.emptyList());
      response.setImages(attachments);
    }
  }

  private void enrichBrand(List<ProductResponse> productResponses) {
    List<Long> productIds =
        ModelTransformUtils.getAttribute(productResponses, ProductResponse::getId);

    List<ProductBrand> productBrands = productBrandAdapter.findAllByProductIds(productIds);

    List<Long> brandIds = ModelTransformUtils.getAttribute(productBrands, ProductBrand::getBrandId);
    List<Brand> allBrands = brandAdapter.findAllByIds(brandIds);

    Map<Long, Long> mapProductIdWithBrandId =
        productBrands.stream()
            .collect(
                Collectors.toMap(
                    ProductBrand::getProductId, ProductBrand::getBrandId, (u1, u2) -> u2));

    Map<Long, Brand> mapBrandWithId = ModelTransformUtils.toMap(allBrands, Brand::getId);

    for (ProductResponse response : productResponses) {
      Long brandId = mapProductIdWithBrandId.getOrDefault(response.getId(), 0L);
      Brand brand = mapBrandWithId.get(brandId);
      if (Objects.isNull(brand)) continue;
      response.setBrand(brand);
    }
  }
}
