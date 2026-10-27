package com.cashBookBackend.cashBookBackendNew.Service;

import com.cashBookBackend.cashBookBackendNew.Entity.Accounts;
import com.cashBookBackend.cashBookBackendNew.Entity.TransactionType;
import com.cashBookBackend.cashBookBackendNew.Entity.Transactions;
import com.cashBookBackend.cashBookBackendNew.Entity.User;
import com.cashBookBackend.cashBookBackendNew.Repository.AccountRepository;
import com.cashBookBackend.cashBookBackendNew.Repository.TransactionRepository;
import com.cashBookBackend.cashBookBackendNew.Repository.UserRepository;
import com.cashBookBackend.cashBookBackendNew.dto.AccountRequestDTO;
import com.cashBookBackend.cashBookBackendNew.dto.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<AccountResponseDTO> getAccountsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Accounts> accounts = accountRepository.findByUser_Id(user.getId());

        return accounts.stream().map(account -> {
            AccountResponseDTO dto = new AccountResponseDTO();
            dto.setAccountId(account.getAccountId());
            dto.setName(account.getName());
            dto.setOpeningBalance(account.getOpeningBalance()); // Fixed: Set opening balance here
            dto.setCreatedAt(account.getCreatedAt());

            // Calculate current balance dynamically based on transactions
            BigDecimal openingBalance = account.getOpeningBalance() != null ? account.getOpeningBalance() : BigDecimal.ZERO;

            List<Transactions> transactions = transactionRepository.findByAccount_AccountId(account.getAccountId());

            BigDecimal totalIncome = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalExpense = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentBalance = openingBalance.add(totalIncome).subtract(totalExpense);
            dto.setCurrentBalance(currentBalance);

            return dto;
        }).toList();
    }

    public AccountResponseDTO createAccountForUser(String email, AccountRequestDTO requestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Accounts account = new Accounts();
        account.setName(requestDTO.getName());
        account.setOpeningBalance(requestDTO.getOpeningBalance());
        account.setUser(user);

        Accounts savedAccount = accountRepository.save(account);

        AccountResponseDTO responseDTO = new AccountResponseDTO();
        responseDTO.setAccountId(savedAccount.getAccountId());
        responseDTO.setName(savedAccount.getName());
        responseDTO.setOpeningBalance(savedAccount.getOpeningBalance());
        responseDTO.setCreatedAt(savedAccount.getCreatedAt());

        // A newly created account has no transactions yet, so current balance = opening balance
        responseDTO.setCurrentBalance(savedAccount.getOpeningBalance() != null ? savedAccount.getOpeningBalance() : BigDecimal.ZERO);

        return responseDTO;
    }
}