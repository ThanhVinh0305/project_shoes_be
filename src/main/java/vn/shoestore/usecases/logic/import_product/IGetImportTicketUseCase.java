package vn.shoestore.usecases.logic.import_product;

import vn.shoestore.application.response.ImportTicketResponse;

public interface IGetImportTicketUseCase {
  ImportTicketResponse getById(Long id);
}
