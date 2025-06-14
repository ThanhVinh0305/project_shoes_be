package vn.shoestore.domain.adapter;

import org.springframework.data.domain.Page;
import vn.shoestore.application.request.GetTicketRequest;
import vn.shoestore.domain.model.ImportTicket;
import vn.shoestore.domain.model.ImportTicketProduct;
import vn.shoestore.domain.model.ProductAmount;

import java.util.List;

public interface ImportTicketAdapter {
  ImportTicket saveImportTicket(ImportTicket importTicket);

  void saveAllImportTicketProduct(List<ImportTicketProduct> importTicketProducts);

  List<ImportTicketProduct> findAllByTicketIdIn(List<Long> ticketIds);

  void deleteImportTicketProductByIds(List<Long> ids);

  ImportTicket getTicketById(Long id);

  void saveProductAmount(List<ProductAmount> productAmounts);

  List<ProductAmount> getAllProductPropertiesIds(List<Long> productPropertiesIds);

  void deleteTicket(Long ticketId);

  Page<ImportTicket> findAllByConditions(GetTicketRequest request);

  List<ProductAmount> getAllProductPropertiesIdsForUpdate(List<Long> productPropertiesIds);
}
