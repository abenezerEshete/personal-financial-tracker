package com.abenezer.personalfinancetracker.account_transaction;

import com.abenezer.personalfinancetracker.account_transaction.dto.AccountTransactionRequestDTO;
import com.abenezer.personalfinancetracker.account_transaction.dto.AccountTransactionResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vi/transactions")
public class AccountTransactionController {

    private final AccountTransactionService transactionService;

    public AccountTransactionController(AccountTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountTransactionResponseDTO> createTransaction(
            @RequestBody AccountTransactionRequestDTO requestDTO) {

        inputValidation(requestDTO);

        AccountTransactionResponseDTO responseDTO = transactionService.createTransaction(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<AccountTransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<AccountTransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<AccountTransactionResponseDTO>> getTransactionsWithinDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AccountTransactionResponseDTO> transactions = transactionService.getTransactionWithDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountTransactionResponseDTO> updateTransaction(
            @PathVariable Long id, @RequestBody AccountTransactionRequestDTO requestDTO) {
        AccountTransactionResponseDTO responseDTO = transactionService.updateTransaction(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/by-account/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountTransactionResponseDTO>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<AccountTransactionResponseDTO> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/criteria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountTransactionResponseDTO>> getTransactionsByCriteria(
            @RequestParam("minAmount") Double minAmount,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        List<AccountTransactionResponseDTO> transactions = transactionService.getTransactionsByCriteria(minAmount, categoryId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/by-account-and-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountTransaction>> getTransactionsByAccountAndCategory(
            @RequestParam("accountId") Long accountId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AccountTransaction> transactions = transactionService.getTransactionsByAccountAndCategory(accountId, categoryId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    private static void inputValidation(AccountTransactionRequestDTO requestDTO) {
        if (requestDTO.getAccountId() == null) throw new IllegalArgumentException("Account ID is required");
        if (requestDTO.getAmount() <=0 ) throw new IllegalArgumentException("Amount is required");
        if (requestDTO.getCategoryId() <= 0) throw new IllegalArgumentException("Category ID is required");
        if (requestDTO.getType() == null) throw new IllegalArgumentException("Type is required");
    }

}
