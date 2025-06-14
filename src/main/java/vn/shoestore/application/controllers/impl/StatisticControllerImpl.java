package vn.shoestore.application.controllers.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IStatisticController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.StatisticResponse;
import vn.shoestore.application.response.StatisticStatusResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.statistic.IStatisticUseCase;

@Component
@RequiredArgsConstructor
public class StatisticControllerImpl implements IStatisticController {
  private final IStatisticUseCase iStatisticUseCase;

  @Override
  public ResponseEntity<BaseResponse<Integer>> findTotalProductInStore() {
    return ResponseFactory.success(iStatisticUseCase.getAllProductInStore());
  }

  @Override
  public ResponseEntity<BaseResponse<List<StatisticResponse>>> getImportStatistic() {
    return ResponseFactory.success(iStatisticUseCase.statisticImport());
  }

  @Override
  public ResponseEntity<BaseResponse<List<StatisticResponse>>> getBillStatistic() {
    return ResponseFactory.success(iStatisticUseCase.getBillStatistic());
  }

  @Override
  public ResponseEntity<BaseResponse<List<StatisticStatusResponse>>> getBillStatusStatistic() {
    return ResponseFactory.success(iStatisticUseCase.getBillStatusStatistic());
  }
}
