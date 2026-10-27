package com.cashBookBackend.cashBookBackendNew.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequestDTO {
    private String name;
    private BigDecimal openingBalance;
}
