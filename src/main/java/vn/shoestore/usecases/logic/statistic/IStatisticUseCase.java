package vn.shoestore.usecases.logic.statistic;

import vn.shoestore.application.response.StatisticResponse;
import vn.shoestore.application.response.StatisticStatusResponse;

import java.util.List;

public interface IStatisticUseCase {
  Integer getAllProductInStore();

  List<StatisticResponse> statisticImport();

  List<StatisticResponse> getBillStatistic();

  List<StatisticStatusResponse> getBillStatusStatistic();
}
