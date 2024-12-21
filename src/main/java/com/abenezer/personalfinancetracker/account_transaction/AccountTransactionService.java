package com.abenezer.personalfinancetracker.account_transaction;

import com.abenezer.personalfinancetracker.account_transaction.AccountTransaction;
import com.abenezer.personalfinancetracker.account_transaction.dto.AccountTransactionRequestDTO;
import com.abenezer.personalfinancetracker.account_transaction.dto.AccountTransactionResponseDTO;
import com.abenezer.personalfinancetracker.account.Account;
import com.abenezer.personalfinancetracker.budget.BudgetService;
import com.abenezer.personalfinancetracker.category.TransactionCategory;
import com.abenezer.personalfinancetracker.account.AccountRepository;
import com.abenezer.personalfinancetracker.category.TransactionCategoryRepository;
import com.abenezer.personalfinancetracker.exception.ResourceNotFoundException;
import com.abenezer.personalfinancetracker.jms.BudgetNotificationProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountTransactionService {

    private final AccountTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionCategoryRepository categoryRepository;
    private final AccountTransactionCriteriaRepository accountTransactionCriteriaRepository;
    private final BudgetService budgetService;
    private final BudgetNotificationProducer budgetNotificationProducer;

    public AccountTransactionService(AccountTransactionRepository transactionRepository,
                                     AccountRepository accountRepository,
                                     TransactionCategoryRepository categoryRepository, AccountTransactionCriteriaRepository accountTransactionCriteriaRepository, BudgetService budgetService, BudgetNotificationProducer budgetNotificationProducer) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.accountTransactionCriteriaRepository = accountTransactionCriteriaRepository;
        this.budgetService = budgetService;
        this.budgetNotificationProducer = budgetNotificationProducer;
    }

    @Transactional
    public AccountTransactionResponseDTO createTransaction(AccountTransactionRequestDTO requestDTO) {
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        TransactionCategory category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        AccountTransaction transaction = new AccountTransaction();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setType(Enum.valueOf(TransactionType.class, requestDTO.getType().toUpperCase()));
        transaction.setDate(requestDTO.getDate());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setAccount(account);
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);

        checkBudgetExceedLimit(account, transaction);

        return mapToResponseDTO(transaction);
    }

    @Transactional
    public AccountTransactionResponseDTO updateTransaction(Long id, AccountTransactionRequestDTO requestDTO) {
        AccountTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        TransactionCategory category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        transaction.setAmount(requestDTO.getAmount());
        transaction.setType(Enum.valueOf(TransactionType.class, requestDTO.getType().toUpperCase()));
        transaction.setDate(requestDTO.getDate());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setAccount(account);
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);

        checkBudgetExceedLimit(account, transaction);

        return mapToResponseDTO(transaction);
    }

    private void checkBudgetExceedLimit(Account account, AccountTransaction transaction) {
        double sumTransactionsByAccount = transactionRepository.sumTransactionsByAccount(account.getId());

        double budgetByCustomer = budgetService.getBudgetByCustomer(transaction.getAccount().getCustomer().getId());

        if (sumTransactionsByAccount > budgetByCustomer) {
            budgetNotificationProducer.sendBudgetExceededNotification(sumTransactionsByAccount, budgetByCustomer);
        }
    }

    public List<AccountTransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public AccountTransactionResponseDTO getTransactionById(Long id) {
        AccountTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToResponseDTO(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        AccountTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transactionRepository.delete(transaction);
    }

    private AccountTransactionResponseDTO mapToResponseDTO(AccountTransaction transaction) {
        AccountTransactionResponseDTO responseDTO = new AccountTransactionResponseDTO();
        responseDTO.setId(transaction.getId());
        responseDTO.setAmount(transaction.getAmount());
        if (transaction.getType() != null) {
            responseDTO.setType(transaction.getType().toString());
        }
        responseDTO.setDate(transaction.getDate());
        responseDTO.setDescription(transaction.getDescription());
        responseDTO.setAccountName(transaction.getAccount().getName());
        responseDTO.setAccountId(transaction.getAccount().getId());
        responseDTO.setCategoryName(transaction.getCategory().getName());
        responseDTO.setCategoryId(transaction.getCategory().getId());
        return responseDTO;
    }

    public List<AccountTransactionResponseDTO> getTransactionWithDateRange(LocalDate startDate, LocalDate endDate) {
        List<AccountTransaction> transactions = transactionRepository.findTransactionsWithinDateRange(startDate, endDate);

        return transactions.stream().map(t -> mapToResponseDTO(t)).collect(Collectors.toList());
    }

    public List<AccountTransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        List<AccountTransaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream().map(t -> mapToResponseDTO(t)).collect(Collectors.toList());
    }

    public List<AccountTransactionResponseDTO> getTransactionsByCriteria(Double minAmount, Long categoryId) {
        List<AccountTransaction> transactions = accountTransactionCriteriaRepository.findTransactions(minAmount, categoryId);
        return transactions.stream().map(t -> mapToResponseDTO(t)).collect(Collectors.toList());
    }

    public List<AccountTransaction> getTransactionsByAccountAndCategory(Long accountId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findTransactionsByAccountAndCategory(accountId, categoryId, startDate, endDate);
    }

    public double sumTransactionsByAccount(Long accountId) {
        return transactionRepository.sumTransactionsByAccount(accountId);
    }


}
