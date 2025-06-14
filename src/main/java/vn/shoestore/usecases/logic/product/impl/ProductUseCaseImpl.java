package vn.shoestore.usecases.logic.product.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.ProductRequest;
import vn.shoestore.domain.adapter.*;
import vn.shoestore.domain.model.*;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.ListUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.product.IProductUseCase;

import java.util.*;
import java.util.stream.Collectors;

import static vn.shoestore.shared.constants.ExceptionMessage.PRODUCT_NOT_FOUND;

@UseCase
@RequiredArgsConstructor
public class ProductUseCaseImpl implements IProductUseCase {
  private final ProductAdapter productAdapter;
  private final ProductAttachmentAdapter productAttachmentAdapter;
  private final ProductCategoryAdapter productCategoryAdapter;
  private final ProductBrandAdapter productBrandAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;

  @Override
  @Transactional
  public void createOrUpdateProduct(ProductRequest request) {
    if (Objects.nonNull(request.getId())) {
      update(request);
    } else create(request);
  }

  @Override
  public void deleteProduct(Long id) {
    productAdapter.delete(id);
  }

  private void create(ProductRequest request) {
    Product product =
        Product.builder()
            .code(request.getCode())
            .description(request.getDescription())
            .price(request.getPrice())
            .name(request.getName())
            .build();

    Product savedProduct = productAdapter.save(product);

    processSave(request, savedProduct);
    saveNewProperties(request.getSizes(), savedProduct.getId());
  }

  private void processSave(ProductRequest request, Product savedProduct) {
    if (Objects.nonNull(request.getBrandId())) {
      productBrandAdapter.saveAll(
          Collections.singletonList(
              ProductBrand.builder()
                  .brandId(request.getBrandId())
                  .productId(savedProduct.getId())
                  .build()));
    }

    if (Objects.nonNull(request.getCategories()) && !request.getCategories().isEmpty()) {
      List<Long> categories =
          ModelTransformUtils.getAttribute(request.getCategories(), Category::getId);
      List<ProductCategory> productCategories = new ArrayList<>();
      for (Long categoryId : categories) {
        productCategories.add(
            ProductCategory.builder()
                .categoryId(categoryId)
                .productId(savedProduct.getId())
                .build());
      }
      productCategoryAdapter.saveAll(productCategories);
    }

    if (Objects.nonNull(request.getImages()) && !request.getImages().isEmpty()) {
      List<String> attachments =
          ModelTransformUtils.getAttribute(
              request.getImages(), ProductAttachment::getAttachment);
      List<ProductAttachment> productAttachments = new ArrayList<>();
      for (String attachment : attachments) {
        productAttachments.add(
            ProductAttachment.builder()
                .productId(savedProduct.getId())
                .attachment(attachment)
                .build());
      }
      productAttachmentAdapter.saveAll(productAttachments);
    }
  }

  private void processDelete(Long productId) {
    List<ProductCategory> productCategories =
        productCategoryAdapter.findAllByProductIds(Collections.singletonList(productId));

    List<ProductAttachment> productAttachments =
        productAttachmentAdapter.findAllByProductIds(Collections.singletonList(productId));

    List<ProductBrand> productBrands =
        productBrandAdapter.findAllByProductIds(Collections.singletonList(productId));

    productCategoryAdapter.deleteByIds(
        ModelTransformUtils.getAttribute(productCategories, ProductCategory::getId));

    productAttachmentAdapter.deleteByIds(
        ModelTransformUtils.getAttribute(productAttachments, ProductAttachment::getId));

    productBrandAdapter.deleteByIds(
        ModelTransformUtils.getAttribute(productBrands, ProductBrand::getId));
  }

  private void update(ProductRequest request) {
    Product product = productAdapter.getProductById(request.getId());
    if (Objects.isNull(product)) {
      throw new InputNotValidException(PRODUCT_NOT_FOUND);
    }

    product.setCode(Objects.nonNull(request.getCode()) ? request.getCode() : product.getCode());
    product.setName(Objects.nonNull(request.getName()) ? request.getName() : product.getName());
    product.setPrice(Objects.nonNull(request.getPrice()) ? request.getPrice() : product.getPrice());
    product.setDescription(
        Objects.nonNull(request.getDescription())
            ? request.getDescription()
            : product.getDescription());

    productAdapter.save(product);
    processDelete(product.getId());
    processSave(request, product);
    savePropertiesForUpdate(request);
  }

  private void saveNewProperties(List<Integer> sizes, Long productId) {
    if (sizes.isEmpty()) return;
    List<ProductProperties> productProperties = new ArrayList<>();
    sizes = sizes.stream().distinct().collect(Collectors.toList());
    for (Integer size : sizes) {
      productProperties.add(
          ProductProperties.builder().productId(productId).size(size).isAble(true).build());
    }
    productPropertiesAdapter.saveAll(productProperties);
  }

  private void savePropertiesForUpdate(ProductRequest request) {
    Long id = request.getId();
    List<Integer> sizes = request.getSizes().stream().distinct().collect(Collectors.toList());

    List<ProductProperties> productProperties =
        productPropertiesAdapter.getAllByProductIdInAndIsAble(Collections.singletonList(id), true);

    List<Integer> existSizes =
        ModelTransformUtils.getAttribute(productProperties, ProductProperties::getSize);
    List<Integer> deletedSizes = ListUtils.diff(existSizes, sizes);
    List<Integer> newSizes = ListUtils.diff(sizes, existSizes);
    disableProperties(productProperties, deletedSizes);
    enableProperties(id, newSizes);
  }

  private void disableProperties(
      List<ProductProperties> productProperties, List<Integer> deletedSizes) {
    if (deletedSizes.isEmpty()) return;
    List<ProductProperties> deleteProductProperties =
        productProperties.stream().filter(e -> deletedSizes.contains(e.getSize())).toList();
    deleteProductProperties.forEach(e -> e.setIsAble(false));
    productPropertiesAdapter.saveAll(deleteProductProperties);
  }

  private void enableProperties(Long id, List<Integer> newSizes) {
    if (newSizes.isEmpty()) return;
    List<ProductProperties> productProperties =
        productPropertiesAdapter.getAllByProductIdInAndIsAble(Collections.singletonList(id), false);

    List<ProductProperties> updatedProperties =
        productProperties.stream().filter(e -> newSizes.contains(e.getSize())).toList();

    updatedProperties.forEach(e -> e.setIsAble(true));
    productPropertiesAdapter.saveAll(updatedProperties);

    List<Integer> updatedSizes =
        ModelTransformUtils.getAttribute(updatedProperties, ProductProperties::getSize);

    List<Integer> addSizes = newSizes.stream().filter(e -> !updatedSizes.contains(e)).toList();
    saveNewProperties(addSizes, id);
  }
}
