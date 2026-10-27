package com.cashBookBackend.cashBookBackendNew.dto;

import com.cashBookBackend.cashBookBackendNew.Entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
}
