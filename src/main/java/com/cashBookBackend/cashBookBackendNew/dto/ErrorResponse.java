package com.cashBookBackend.cashBookBackendNew.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime dateTime;
    private int status;
    private String error;
    private String message;
}
