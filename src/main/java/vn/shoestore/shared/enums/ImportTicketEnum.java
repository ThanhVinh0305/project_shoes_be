package vn.shoestore.shared.enums;

import lombok.Getter;

@Getter
public enum ImportTicketEnum {
  DRAFT(0),
  DONE(1);

  private final int status;

  ImportTicketEnum(int status) {
    this.status = status;
  }
}
