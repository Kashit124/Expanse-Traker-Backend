package com.cashBookBackend.cashBookBackendNew.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AccountResponseDTO {
    private Integer accountId;
    private String name;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;
}
