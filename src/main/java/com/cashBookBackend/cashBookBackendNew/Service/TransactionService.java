package com.cashBookBackend.cashBookBackendNew.Service;

import com.cashBookBackend.cashBookBackendNew.Entity.Accounts;
import com.cashBookBackend.cashBookBackendNew.Entity.TransactionType;
import com.cashBookBackend.cashBookBackendNew.Entity.Transactions;
import com.cashBookBackend.cashBookBackendNew.Entity.User;
import com.cashBookBackend.cashBookBackendNew.Repository.AccountRepository;
import com.cashBookBackend.cashBookBackendNew.Repository.TransactionRepository;
import com.cashBookBackend.cashBookBackendNew.Repository.UserRepository;
import com.cashBookBackend.cashBookBackendNew.dto.TransactionRequestDTO;
import com.cashBookBackend.cashBookBackendNew.dto.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public List<TransactionResponseDTO> getTransactionsByAccount(Integer accountId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Accounts accounts = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!accounts.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this account");
        }

        List<Transactions> transactions = transactionRepository.findByAccount_AccountId(accountId);
        return transactions.stream().map(this::mapToResponseDto).toList();
    }

    public TransactionResponseDTO createTransaction(Integer accountId, String email, TransactionRequestDTO requestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Accounts account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access of this account");
        }

        // 1. Create and save the transaction
        Transactions transaction = new Transactions();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setType(requestDTO.getType());
        transaction.setCategory(requestDTO.getCategory());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);

        Transactions savedTransaction = transactionRepository.save(transaction);

        // 2. Update the physical current_balance in the Accounts table
        BigDecimal currentBalance = account.getCurrentBalance() != null ? account.getCurrentBalance() : account.getOpeningBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        if (requestDTO.getType() == TransactionType.INCOME) {
            account.setCurrentBalance(currentBalance.add(requestDTO.getAmount()));
        } else if (requestDTO.getType() == TransactionType.EXPENSE) {
            account.setCurrentBalance(currentBalance.subtract(requestDTO.getAmount()));
        }
        accountRepository.save(account);

        return mapToResponseDto(savedTransaction);
    }

    public TransactionResponseDTO updateTransaction(Integer transactionId, String email, TransactionRequestDTO requestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getAccount().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this transaction");
        }

        // For simplicity when updating, a robust way is to re-calculate from all transactions,
        // or reverse the old amount and apply the new one.
        // Let's use the dynamic recalculation approach to update the account's current balance safely:
        Accounts account = transaction.getAccount();

        transaction.setAmount(requestDTO.getAmount());
        transaction.setType(requestDTO.getType());
        transaction.setCategory(requestDTO.getCategory());
        transaction.setDescription(requestDTO.getDescription());
        transactionRepository.save(transaction);

        // Recalculate balance for this account
        recalculateAndUpdateAccountBalance(account);

        return mapToResponseDto(transaction);
    }

    public void deleteTransaction(Integer transactionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getAccount().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this transaction");
        }

        Accounts account = transaction.getAccount();
        transactionRepository.delete(transaction);

        // Recalculate balance for this account after deletion
        recalculateAndUpdateAccountBalance(account);
    }

    private void recalculateAndUpdateAccountBalance(Accounts account) {
        List<Transactions> transactions = transactionRepository.findByAccount_AccountId(account.getAccountId());
        BigDecimal opening = account.getOpeningBalance() != null ? account.getOpeningBalance() : BigDecimal.ZERO;

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        account.setCurrentBalance(opening.add(totalIncome).subtract(totalExpense));
        accountRepository.save(account);
    }

    private TransactionResponseDTO mapToResponseDto(Transactions transactions) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setTransactionId(transactions.getTransactionId());
        dto.setAmount(transactions.getAmount());
        dto.setType(transactions.getType());
        dto.setCategory(transactions.getCategory());
        dto.setDescription(transactions.getDescription());
        dto.setTransactionDate(transactions.getTransactionDate());

        if (transactions.getAccount() != null) {
            dto.setAccountId(transactions.getAccount().getAccountId());
        }
        return dto;
    }
}