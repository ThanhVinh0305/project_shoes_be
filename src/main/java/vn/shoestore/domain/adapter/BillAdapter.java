package vn.shoestore.domain.adapter;

import java.util.List;
import org.springframework.data.domain.Page;
import vn.shoestore.application.request.GetAllBillRequest;
import vn.shoestore.domain.model.Bill;
import vn.shoestore.domain.model.ProductBill;

public interface BillAdapter {
  Bill saveBill(Bill bill);

  void saveProductBill(List<ProductBill> productBills);

  Bill findBillById(Long billId);

  Page<Bill> findAllByByConditions(GetAllBillRequest request, Long userId);

  List<ProductBill> findAllByBillIdIn(List<Long> billIds);

  List<Bill> findBillOfUserAndProductId(Long userId , Long productId);
}
