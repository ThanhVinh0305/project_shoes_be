package vn.shoestore.application.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.StatisticResponse;
import vn.shoestore.application.response.StatisticStatusResponse;

@RestController
@RequestMapping("/api/v1/statistic/")
public interface IStatisticController {
  @GetMapping("total-product-in-store")
  ResponseEntity<BaseResponse<Integer>> findTotalProductInStore();

  @GetMapping("import-product-statistic")
  ResponseEntity<BaseResponse<List<StatisticResponse>>> getImportStatistic();

  @GetMapping("revenue-statistic")
  ResponseEntity<BaseResponse<List<StatisticResponse>>> getBillStatistic();

  @GetMapping("order-status-statistic")
  ResponseEntity<BaseResponse<List<StatisticStatusResponse>>> getBillStatusStatistic();
}
