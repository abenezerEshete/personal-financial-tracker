package com.abenezer.personalfinancetracker.account_transaction;

import com.abenezer.personalfinancetracker.account.Account;
import com.abenezer.personalfinancetracker.category.TransactionCategory;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@NamedQuery(
        name = "AccountTransaction.findByAccountId",
        query = "SELECT t FROM AccountTransaction t WHERE t.account.id = :accountId"
)
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME or EXPENSE

    private LocalDate date;

    private String description;

    @ManyToOne
    private Account account;

    @ManyToOne
    private TransactionCategory category;

    @Version
    private Long version;  // Optimistic locking version field


    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date != null) {
            this.date = date;
        }
        else this.date = LocalDate.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    // Getters and Setters
}
