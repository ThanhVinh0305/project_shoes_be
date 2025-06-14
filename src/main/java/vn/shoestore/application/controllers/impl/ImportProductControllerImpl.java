package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IImportProductController;
import vn.shoestore.application.request.ConfirmImportTicketRequest;
import vn.shoestore.application.request.GetTicketRequest;
import vn.shoestore.application.request.ImportProductRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.GetAllTicketResponse;
import vn.shoestore.application.response.ImportTicketResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.import_product.IGetImportTicketUseCase;
import vn.shoestore.usecases.logic.import_product.ImportProductUseCase;

@Component
@RequiredArgsConstructor
public class ImportProductControllerImpl implements IImportProductController {
  private final ImportProductUseCase importProductUseCase;
  private final IGetImportTicketUseCase iGetImportTicketUseCase;

  @Override
  public ResponseEntity<BaseResponse> saveOrUpdateTicket(ImportProductRequest request) {
    importProductUseCase.saveOrUpdateImportTicket(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> confirmImportTicket(ConfirmImportTicketRequest request) {
    importProductUseCase.submitImportTicket(request);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse> deleteTicket(Long id) {
    importProductUseCase.deleteImportTicket(id);
    return ResponseFactory.success();
  }

  @Override
  public ResponseEntity<BaseResponse<GetAllTicketResponse>> findByConditions(
      GetTicketRequest request) {
    return ResponseFactory.success(importProductUseCase.getAllByConditions(request));
  }

  @Override
  public ResponseEntity<BaseResponse<ImportTicketResponse>> getById(Long id) {
    return ResponseFactory.success(iGetImportTicketUseCase.getById(id));
  }
}
