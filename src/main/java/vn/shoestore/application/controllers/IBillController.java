package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.BuyNowRequest;
import vn.shoestore.application.request.CreateBillRequest;
import vn.shoestore.application.request.GetAllBillRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.BillResponse;
import vn.shoestore.application.response.BillResponseData;

@RestController
@RequestMapping("/api/v1/bill")
public interface IBillController {
  @PostMapping("create-bill")
  ResponseEntity<BaseResponse<BillResponseData>> createBill(
      @RequestBody @Valid CreateBillRequest request);

  @PutMapping("confirm-purchase/{id}")
  ResponseEntity<BaseResponse> confirmPurchase(@PathVariable Long id);

  @PostMapping("buy-now")
  ResponseEntity<BaseResponse<BillResponseData>> buyNow(@RequestBody @Valid BuyNowRequest request);

  @PostMapping("get-all")
  ResponseEntity<BaseResponse<BillResponse>> getBillByConditions(
      @RequestBody GetAllBillRequest request);

  @PostMapping("get-bill-info/{id}")
  ResponseEntity<BaseResponse<BillResponseData>> getOneBill(@PathVariable Long id);

  @PostMapping("cancel-order/{id}")
  ResponseEntity<BaseResponse> cancelOrder(@PathVariable Long id);
}
