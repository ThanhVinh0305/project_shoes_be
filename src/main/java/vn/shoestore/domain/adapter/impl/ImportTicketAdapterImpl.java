package vn.shoestore.domain.adapter.impl;

import static vn.shoestore.shared.constants.ExceptionMessage.TICKET_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.shoestore.application.request.GetTicketRequest;
import vn.shoestore.domain.adapter.ImportTicketAdapter;
import vn.shoestore.domain.model.ImportTicket;
import vn.shoestore.domain.model.ImportTicketProduct;
import vn.shoestore.domain.model.ProductAmount;
import vn.shoestore.infrastructure.repository.entity.ImportTicketEntity;
import vn.shoestore.infrastructure.repository.entity.ImportTicketProductEntity;
import vn.shoestore.infrastructure.repository.entity.ProductAmountEntity;
import vn.shoestore.infrastructure.repository.repository.ImportTicketProductRepository;
import vn.shoestore.infrastructure.repository.repository.ImportTicketRepository;
import vn.shoestore.infrastructure.repository.repository.ProductAmountRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.exceptions.InputNotValidException;
import vn.shoestore.shared.utils.ModelMapperUtils;

@Adapter
@RequiredArgsConstructor
public class ImportTicketAdapterImpl implements ImportTicketAdapter {
  private final ImportTicketProductRepository importTicketProductRepository;
  private final ImportTicketRepository importTicketRepository;
  private final ProductAmountRepository productAmountRepository;

  @Override
  public ImportTicket saveImportTicket(ImportTicket importTicket) {
    return ModelMapperUtils.mapper(
        importTicketRepository.save(
            ModelMapperUtils.mapper(importTicket, ImportTicketEntity.class)),
        ImportTicket.class);
  }

  @Override
  public void saveAllImportTicketProduct(List<ImportTicketProduct> importTicketProducts) {
    importTicketProductRepository.saveAll(
        ModelMapperUtils.mapList(importTicketProducts, ImportTicketProductEntity.class));
  }

  @Override
  public List<ImportTicketProduct> findAllByTicketIdIn(List<Long> ticketIds) {
    return ModelMapperUtils.mapList(
        importTicketProductRepository.findAllByTicketIdIn(ticketIds), ImportTicketProduct.class);
  }

  @Override
  public void deleteImportTicketProductByIds(List<Long> ids) {
    importTicketProductRepository.deleteAllByIdInBatch(ids);
  }

  @Override
  public ImportTicket getTicketById(Long id) {
    Optional<ImportTicketEntity> optionalImportTicket = importTicketRepository.findById(id);
    if (optionalImportTicket.isEmpty()) {
      throw new InputNotValidException(TICKET_NOT_FOUND);
    }
    return ModelMapperUtils.mapper(optionalImportTicket.get(), ImportTicket.class);
  }

  @Override
  public void saveProductAmount(List<ProductAmount> productAmounts) {
    productAmountRepository.saveAll(
        ModelMapperUtils.mapList(productAmounts, ProductAmountEntity.class));
  }

  @Override
  public List<ProductAmount> getAllProductPropertiesIds(List<Long> productPropertiesIds) {
    return ModelMapperUtils.mapList(
        productAmountRepository.findAllByProductPropertiesIdIn(productPropertiesIds),
        ProductAmount.class);
  }

  @Override
  public void deleteTicket(Long ticketId) {
    importTicketRepository.deleteById(ticketId);
  }

  @Override
  public Page<ImportTicket> findAllByConditions(GetTicketRequest request) {
    return ModelMapperUtils.mapPage(
        importTicketRepository.getAllByConditions(
            PageRequest.of(request.getPage() - 1, request.getSize())),
        ImportTicket.class);
  }

  @Override
  public List<ProductAmount> getAllProductPropertiesIdsForUpdate(List<Long> productPropertiesIds) {
    return ModelMapperUtils.mapList(
        productAmountRepository.findAllByProductPropertiesIdInForUpdate(productPropertiesIds),
        ProductAmount.class);
  }
}
