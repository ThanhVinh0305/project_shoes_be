package vn.shoestore.usecases.logic.import_product.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.*;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.application.request.ConfirmImportTicketRequest;
import vn.shoestore.application.request.GetTicketRequest;
import vn.shoestore.application.request.ImportProductRequest;
import vn.shoestore.application.response.GetAllTicketResponse;
import vn.shoestore.domain.adapter.ImportTicketAdapter;
import vn.shoestore.domain.adapter.ProductPropertiesAdapter;
import vn.shoestore.domain.adapter.UserAdapter;
import vn.shoestore.domain.model.*;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.dto.ProductPropertiesAmountDTO;
import vn.shoestore.shared.dto.TicketDataDTO;
import vn.shoestore.shared.enums.ImportTicketEnum;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.shared.utils.ModelMapperUtils;
import vn.shoestore.shared.utils.ModelTransformUtils;
import vn.shoestore.usecases.logic.import_product.ImportProductUseCase;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class ImportProductUseCaseImpl implements ImportProductUseCase {
  private final ImportTicketAdapter importTicketAdapter;
  private final ProductPropertiesAdapter productPropertiesAdapter;
  private final UserAdapter userAdapter;

  @Override
  @Transactional
  public void saveOrUpdateImportTicket(ImportProductRequest request) {
    ImportTicket importTicket = ModelMapperUtils.mapper(request, ImportTicket.class);
    CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();

    importTicket.setCreatedUserId(userDetails.getUser().getId());
    importTicket.setCreatedDate(LocalDateTime.now());
    importTicket.setStatus(ImportTicketEnum.DRAFT.getStatus());

    ImportTicket savedTicket = importTicketAdapter.saveImportTicket(importTicket);

    if (Objects.nonNull(request.getId())) {
      validateStatus(request.getId());
      deleteImportTicketProducts(savedTicket.getId());
    }

    List<ProductPropertiesAmountDTO> amountDTOS = request.getProductAmounts();
    List<Long> productIds =
        ModelTransformUtils.getAttribute(amountDTOS, ProductPropertiesAmountDTO::getProductId)
            .stream()
            .distinct()
            .toList();

    List<Integer> sizes =
        ModelTransformUtils.getAttribute(amountDTOS, ProductPropertiesAmountDTO::getSize).stream()
            .distinct()
            .toList();

    List<ProductProperties> productProperties =
        productPropertiesAdapter.findAllByProductIdInAndSizeInAndIsAble(productIds, sizes, true);
    List<ImportTicketProduct> products = new ArrayList<>();

    for (ProductPropertiesAmountDTO dto : amountDTOS) {
      Optional<ProductProperties> optionalProductProperties =
          productProperties.stream()
              .filter(
                  e ->
                      Objects.equals(e.getProductId(), dto.getProductId())
                          && Objects.equals(e.getSize(), dto.getSize()))
              .findFirst();
      if (optionalProductProperties.isEmpty()) continue;
      products.add(
          ImportTicketProduct.builder()
              .productPropertiesId(optionalProductProperties.get().getId())
              .importCost(dto.getImportCost())
              .amount(dto.getAmount())
              .ticketId(savedTicket.getId())
              .build());
    }
    importTicketAdapter.saveAllImportTicketProduct(products);

    if (request.getIsConfirm()) {
      confirm(savedTicket.getId());
    }
  }

  @Override
  @Transactional
  public void submitImportTicket(ConfirmImportTicketRequest request) {
    confirm(request.getTicketId());
  }

  private void confirm(Long id) {
    ImportTicket importTicket = importTicketAdapter.getTicketById(id);
    if (Objects.equals(importTicket.getStatus(), ImportTicketEnum.DONE.getStatus())) {
      throw new InputNotValidException(TICKET_STATUS_NOT_FOUND);
    }

    CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();

    importTicket.setImportUserId(userDetails.getUser().getId());
    importTicket.setStatus(ImportTicketEnum.DONE.getStatus());
    importTicket.setImportedTime(LocalDateTime.now());

    importTicketAdapter.saveImportTicket(importTicket);

    List<ImportTicketProduct> importTicketProducts =
        importTicketAdapter.findAllByTicketIdIn(Collections.singletonList(importTicket.getId()));

    saveProductAmount(importTicketProducts);
  }

  @Override
  public void deleteImportTicket(Long importTicketId) {
    ImportTicket importTicket = importTicketAdapter.getTicketById(importTicketId);
    if (Objects.equals(importTicket.getStatus(), ImportTicketEnum.DONE.getStatus())) {
      throw new InputNotValidException(TICKET_STATUS_NOT_FOUND);
    }

    importTicketAdapter.deleteTicket(importTicketId);
  }

  @Override
  public GetAllTicketResponse getAllByConditions(GetTicketRequest request) {
    Page<ImportTicket> importTicketPage = importTicketAdapter.findAllByConditions(request);
    List<TicketDataDTO> data =
        ModelMapperUtils.mapList(importTicketPage.getContent(), TicketDataDTO.class);
    if (data.isEmpty()) {
      return GetAllTicketResponse.builder().build();
    }

    List<Long> userIds =
        new ArrayList<>(
            ModelTransformUtils.getAttribute(data, TicketDataDTO::getImportUserId).stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList());

    userIds.addAll(ModelTransformUtils.getAttribute(data, TicketDataDTO::getCreatedUserId));

    List<User> users = userAdapter.getUserByIdIn(userIds);
    Map<Long, User> userMap = ModelTransformUtils.toMap(users, User::getId);

    for (TicketDataDTO dto : data) {
      User importUser = userMap.get(dto.getImportUserId());
      User createdUser = userMap.get(dto.getCreatedUserId());

      if (Objects.nonNull(createdUser)) {
        dto.setCreatedBy(createdUser.getUsername());
      }

      if (Objects.isNull(importUser)) continue;
      dto.setConfirmBy(importUser.getUsername());
    }

    return GetAllTicketResponse.builder()
        .total(importTicketPage.getTotalElements())
        .data(data)
        .build();
  }

  private void deleteImportTicketProducts(Long ticketId) {
    List<ImportTicketProduct> existProducts =
        importTicketAdapter.findAllByTicketIdIn(Collections.singletonList(ticketId));

    if (existProducts.isEmpty()) return;

    List<Long> ids = ModelTransformUtils.getAttribute(existProducts, ImportTicketProduct::getId);
    importTicketAdapter.deleteImportTicketProductByIds(ids);
  }

  private void validateStatus(Long ticketId) {
    ImportTicket importTicket = importTicketAdapter.getTicketById(ticketId);
    if (Objects.equals(importTicket.getStatus(), ImportTicketEnum.DONE.getStatus())) {
      throw new InputNotValidException(TICKET_STATUS_NOT_FOUND);
    }
  }

  private void saveProductAmount(List<ImportTicketProduct> importTicketProducts) {
    if (importTicketProducts.isEmpty()) return;
    List<ProductAmount> productAmounts =
        importTicketAdapter.getAllProductPropertiesIds(
            ModelTransformUtils.getAttribute(
                importTicketProducts, ImportTicketProduct::getProductPropertiesId));

    Map<Long, ProductAmount> productAmountMap =
        ModelTransformUtils.toMap(productAmounts, ProductAmount::getProductPropertiesId);

    List<ProductAmount> savedProductAmounts = new ArrayList<>();
    for (ImportTicketProduct importProduct : importTicketProducts) {
      ProductAmount productAmount = productAmountMap.get(importProduct.getProductPropertiesId());
      if (Objects.isNull(productAmount)) {
        savedProductAmounts.add(
            ProductAmount.builder()
                .amount(importProduct.getAmount())
                .productPropertiesId(importProduct.getProductPropertiesId())
                .updatedDate(LocalDateTime.now())
                .build());
      } else {
        productAmount.setAmount(productAmount.getAmount() + importProduct.getAmount());
        productAmount.setUpdatedDate(LocalDateTime.now());
        savedProductAmounts.add(productAmount);
      }
    }
    importTicketAdapter.saveProductAmount(savedProductAmounts);
  }
}
