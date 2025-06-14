package vn.shoestore.usecases.logic.bill;

import vn.shoestore.application.request.BuyNowRequest;
import vn.shoestore.application.request.CreateBillRequest;
import vn.shoestore.application.request.GetAllBillRequest;
import vn.shoestore.application.response.BillResponse;
import vn.shoestore.application.response.BillResponseData;

public interface IBillUseCase {
  BillResponseData createBill(CreateBillRequest request, Boolean isOnlineTransaction);

  void adminConfirmBill(Long billId);

  BillResponseData buyNow(BuyNowRequest request, Boolean isOnlineTransaction);

  BillResponse getBillByFilter(GetAllBillRequest request);

  BillResponseData getBillById(Long id);

  void cancelBill(Long billId);
}
