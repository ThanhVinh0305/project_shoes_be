package vn.shoestore.usecases.logic.statistic.impl;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import vn.shoestore.application.response.StatisticResponse;
import vn.shoestore.application.response.StatisticStatusResponse;
import vn.shoestore.infrastructure.repository.repository.BillRepository;
import vn.shoestore.infrastructure.repository.repository.ImportTicketRepository;
import vn.shoestore.infrastructure.repository.repository.ProductAmountRepository;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.shared.enums.BillStatusEnum;
import vn.shoestore.shared.query_object.BillStatusObject;
import vn.shoestore.shared.query_object.ImportProduct;
import vn.shoestore.usecases.logic.statistic.IStatisticUseCase;

@UseCase
@RequiredArgsConstructor
public class StatisticUseCaseImpl implements IStatisticUseCase {
  private final ProductAmountRepository productAmountRepository;
  private final ImportTicketRepository importTicketRepository;
  private final BillRepository billRepository;

  @Override
  public Integer getAllProductInStore() {
    return productAmountRepository.getSumAmount();
  }

  @Override
  public List<StatisticResponse> statisticImport() {
    List<ImportProduct> importProducts = importTicketRepository.getStatisticImport();
    return buildStatisticResponse(importProducts);
  }

  private static List<StatisticResponse> buildStatisticResponse(
      List<ImportProduct> importProducts) {
    Map<Integer, Integer> mapMonth = getMapMonthWithYear();
    List<StatisticResponse> results = new ArrayList<>();
    for (Integer month : mapMonth.keySet()) {
      if (Objects.isNull(mapMonth.get(month))) continue;
      int year = mapMonth.get(month);
      StatisticResponse statisticResponse =
          StatisticResponse.builder().time(year + "-" + month).total(0L).build();
      results.add(statisticResponse);
      Optional<ImportProduct> optionalImportProduct =
          importProducts.stream()
              .filter(e -> Objects.equals(e.getMonth(), month) && Objects.equals(e.getYear(), year))
              .findFirst();
      if (optionalImportProduct.isEmpty()) continue;
      statisticResponse.setTotal(optionalImportProduct.get().getTotal());
    }
    return results;
  }

  @Override
  public List<StatisticResponse> getBillStatistic() {
    List<ImportProduct> importProducts = billRepository.getBillStatistic();
    return buildStatisticResponse(importProducts);
  }

  @Override
  public List<StatisticStatusResponse> getBillStatusStatistic() {
    Map<Integer, Integer> mapMonth = getMapMonthWithYear();
    List<StatisticStatusResponse> results = new ArrayList<>();
    List<BillStatusObject> statusObjects = billRepository.findBillStatusStatistic();
    for (Integer month : mapMonth.keySet()) {
      if (Objects.isNull(mapMonth.get(month))) continue;
      int year = mapMonth.get(month);
      StatisticStatusResponse response =
          StatisticStatusResponse.builder().time(year + "-" + month).success(0L).cancel(0L).build();
      results.add(response);

      Optional<BillStatusObject> cancelObjects =
          statusObjects.stream()
              .filter(
                  e ->
                      Objects.equals(e.getMonth(), month)
                          && Objects.equals(e.getYear(), year)
                          && Objects.equals(e.getStatus(), BillStatusEnum.CANCEL.getStatus()))
              .findFirst();

      cancelObjects.ifPresent(billStatusObject -> response.setCancel(billStatusObject.getTotal()));

      Optional<BillStatusObject> successObjects =
          statusObjects.stream()
              .filter(
                  e ->
                      Objects.equals(e.getMonth(), month)
                          && Objects.equals(e.getYear(), year)
                          && Objects.equals(e.getStatus(), BillStatusEnum.PURCHASE.getStatus()))
              .findFirst();

      successObjects.ifPresent(
          billStatusObject -> response.setSuccess(billStatusObject.getTotal()));
    }

    return results;
  }

  private static Map<Integer, Integer> getMapMonthWithYear() {
    Map<Integer, Integer> mapMonthAndYear = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    mapMonthAndYear.put(now.getMonthValue(), now.getYear());

    for (int i = 0; i < 7; i++) {
      now = now.minusMonths(1);
      mapMonthAndYear.put(now.getMonthValue(), now.getYear());
    }
    return mapMonthAndYear;
  }
}
