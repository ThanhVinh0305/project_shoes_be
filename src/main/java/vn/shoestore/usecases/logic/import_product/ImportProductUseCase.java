package vn.shoestore.usecases.logic.import_product;

import vn.shoestore.application.request.ConfirmImportTicketRequest;
import vn.shoestore.application.request.GetTicketRequest;
import vn.shoestore.application.request.ImportProductRequest;
import vn.shoestore.application.response.GetAllTicketResponse;

public interface ImportProductUseCase {
  void saveOrUpdateImportTicket(ImportProductRequest request);

  void submitImportTicket(ConfirmImportTicketRequest request);

  void deleteImportTicket(Long importTicket);

  GetAllTicketResponse getAllByConditions(GetTicketRequest request);
}
