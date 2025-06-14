package vn.shoestore.usecases.logic.import_product.impl;

import java.util.*;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import vn.shoestore.application.response.ImportTicketResponse;
import vn.shoestore.application.response.ProductResponse;
import vn.shoestore.domain.adapter.*;
import vn.shoestore.domain.model.*;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.ProductImportDTO;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.import_product.IGetImportTicketUseCase;
import vn.shoestore.usecases.logic.product.IGetProductUseCase;

@UseCase
@RequiredArgsConstructor
public class GetImportTicketUseCaseImpl implements IGetImportTicketUseCase {

  private final ImportTicketAdapter importTicketAdapter;
  private final UserAdapter userAdapter;
  private final BrandAdapter brandAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;
  private final IGetProductUseCase iGetProductUseCase;

  @Override
  public ImportTicketResponse getById(Long id) {
    ImportTicket importTicket = importTicketAdapter.getTicketById(id);

    ImportTicketResponse response =
        ImportTicketResponse.builder()
            .id(importTicket.getId())
            .importedTime(importTicket.getImportedTime())
            .createdDate(importTicket.getCreatedDate())
            .description(importTicket.getDescription())
            .status(importTicket.getStatus())
            .build();

    enrichBrand(response, importTicket);
    importUser(response, importTicket);
    enrichProduct(response, importTicket);

    return response;
  }

  private void enrichBrand(ImportTicketResponse response, ImportTicket importTicket) {
    Long brandId = importTicket.getBrandId();
    Brand brand = brandAdapter.findById(brandId);
    response.setBrand(brand);
  }

  private void importUser(ImportTicketResponse response, ImportTicket importTicket) {
    List<Long> userIds =
        Stream.of(importTicket.getImportUserId(), importTicket.getCreatedUserId())
            .filter(Objects::nonNull)
            .toList();

    Map<Long, User> mapUser =
        ModelTransformUtils.toMap(userAdapter.getUserByIdIn(userIds), User::getId);

    response.setCreatedUser(mapUser.get(importTicket.getCreatedUserId()));

    if (Objects.nonNull(importTicket.getImportUserId())) {
      response.setImportUser(mapUser.get(importTicket.getImportUserId()));
    }
  }

  private void enrichProduct(ImportTicketResponse response, ImportTicket importTicket) {
    List<ImportTicketProduct> importTicketProducts =
        importTicketAdapter.findAllByTicketIdIn(Collections.singletonList(importTicket.getId()));

    if (importTicketProducts.isEmpty()) return;

    List<Long> productPropertiesIds =
        ModelTransformUtils.getAttribute(
            importTicketProducts, ImportTicketProduct::getProductPropertiesId);

    List<ProductProperties> productProperties =
        productPropertiesAdapter.findAllIdIn(productPropertiesIds);

    List<Long> productIds =
        ModelTransformUtils.getAttribute(productProperties, ProductProperties::getProductId);

    List<ProductResponse> products = iGetProductUseCase.findByIds(productIds);

    Map<Long, ProductProperties> productPropertiesMap =
        ModelTransformUtils.toMap(productProperties, ProductProperties::getId);

    Map<Long, ProductResponse> productMap =
        ModelTransformUtils.toMap(products, ProductResponse::getId);

    List<ProductImportDTO> productData = new ArrayList<>();
    for (ImportTicketProduct ticketProduct : importTicketProducts) {
      ProductProperties properties =
          productPropertiesMap.get(ticketProduct.getProductPropertiesId());
      ProductResponse product = null;
      if (Objects.nonNull(properties)) {
        product = productMap.get(properties.getProductId());
      }

      productData.add(
          ProductImportDTO.builder()
              .productPropertiesId(ticketProduct.getProductPropertiesId())
              .amount(ticketProduct.getAmount())
              .size(Objects.nonNull(properties) ? properties.getSize() : null)
              .productId(Objects.nonNull(product) ? product.getId() : null)
              .name(Objects.nonNull(product) ? product.getName() : null)
              .importCost(ticketProduct.getImportCost())
              .product(product)
              .build());
    }
    response.setProducts(productData);
  }
}
