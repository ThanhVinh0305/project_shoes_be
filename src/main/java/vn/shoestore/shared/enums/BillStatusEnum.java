package vn.shoestore.shared.enums;

import lombok.Getter;

@Getter
public enum BillStatusEnum {
  CREATED(0),
  PURCHASE(1),
  CANCEL(2);

  private final int status;

  BillStatusEnum(int status) {
    this.status = status;
  }
}
