package com.cashBookBackend.cashBookBackendNew.Controller;

import com.cashBookBackend.cashBookBackendNew.Service.TransactionService;
import com.cashBookBackend.cashBookBackendNew.dto.TransactionRequestDTO;
import com.cashBookBackend.cashBookBackendNew.dto.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionByAccount(
            @PathVariable Integer accountId,
            @RequestHeader("X-User-Email") String email){
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccount(accountId, email);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/account/{accountId}")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @PathVariable Integer accountId,
            @RequestHeader("X-User-Email") String email,
            @RequestBody TransactionRequestDTO requestDTO){
        TransactionResponseDTO saveTransaction = transactionService.createTransaction(accountId, email, requestDTO);
        return new ResponseEntity<>(saveTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/account/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Integer transactionId,
            @RequestHeader("X-User-Email") String email,
            @RequestBody TransactionRequestDTO requestDTO){
        TransactionResponseDTO updateTransaction = transactionService.updateTransaction(transactionId, email, requestDTO);
        return new ResponseEntity<>(updateTransaction, HttpStatus.OK);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable Integer transactionId,
            @RequestHeader("X-User-Email") String email){
        transactionService.deleteTransaction(transactionId, email);
        return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
    }
}