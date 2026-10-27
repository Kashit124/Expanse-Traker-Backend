package com.cashBookBackend.cashBookBackendNew.dto;

import com.cashBookBackend.cashBookBackendNew.Entity.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDTO {
    private Integer transactionId;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
    private LocalDateTime transactionDate;
    private Integer accountId;
}
