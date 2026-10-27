package com.cashBookBackend.cashBookBackendNew.Controller;

import com.cashBookBackend.cashBookBackendNew.Service.AccountService;
import com.cashBookBackend.cashBookBackendNew.dto.AccountRequestDTO;
import com.cashBookBackend.cashBookBackendNew.dto.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getUserAccount(@RequestHeader("X-User-Email") String email){
        List<AccountResponseDTO> accounts = accountService.getAccountsByUserEmail(email);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestHeader("X-User-Email") String email, @RequestBody AccountRequestDTO requestDTO){
        AccountResponseDTO responseDTO = accountService.createAccountForUser(email, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}