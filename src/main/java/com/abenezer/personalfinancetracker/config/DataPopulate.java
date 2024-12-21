package com.abenezer.personalfinancetracker.config;

import com.abenezer.personalfinancetracker.account.Account;
import com.abenezer.personalfinancetracker.account.AccountRepository;
import com.abenezer.personalfinancetracker.account.AccountType;
import com.abenezer.personalfinancetracker.account_transaction.AccountTransaction;
import com.abenezer.personalfinancetracker.account_transaction.AccountTransactionRepository;
import com.abenezer.personalfinancetracker.budget.Budget;
import com.abenezer.personalfinancetracker.budget.BudgetRepository;
import com.abenezer.personalfinancetracker.category.TransactionCategory;
import com.abenezer.personalfinancetracker.category.TransactionCategoryRepository;
import com.abenezer.personalfinancetracker.customer.Customer;
import com.abenezer.personalfinancetracker.customer.CustomerRepository;
import com.abenezer.personalfinancetracker.customer.Role;
import com.abenezer.personalfinancetracker.customer.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataPopulate {
    @Bean
    public CommandLineRunner populateData(
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            AccountTransactionRepository transactionRepository,
            TransactionCategoryRepository categoryRepository,
            BudgetRepository budgetRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Create Roles
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");

            Role userRole = new Role();
            userRole.setName("ROLE_USER");

            roleRepository.saveAll(List.of(adminRole, userRole));

            // Create Customers
            Customer customer1 = new Customer();
            customer1.setName("John Doe");
            customer1.setUserName("john");
            customer1.setPassword(passwordEncoder.encode("111"));
            customer1.setRole(userRole);

            Customer customer2 = new Customer();
            customer2.setName("Jane Smith");
            customer2.setUserName("jane");
            customer2.setPassword(passwordEncoder.encode("123"));
            customer2.setRole(adminRole);

            customerRepository.saveAll(List.of(customer1, customer2));

            // Create Accounts
            Account account1 = new Account();
            account1.setName("John's Savings");
            account1.setType(AccountType.BANK);
            account1.setBalance(5000.0);
            account1.setCustomer(customer1);

            Account account2 = new Account();
            account2.setName("Jane's Credit");
            account2.setType(AccountType.CREDIT);
            account2.setBalance(10000.0);
            account2.setCustomer(customer2);

            accountRepository.saveAll(List.of(account1, account2));

            // Create Transaction Categories
            TransactionCategory category1 = new TransactionCategory();
            category1.setName("Groceries");

            TransactionCategory category2 = new TransactionCategory();
            category2.setName("Utilities");

            categoryRepository.saveAll(List.of(category1, category2));

            // Create Transactions
            AccountTransaction transaction1 = new AccountTransaction();
            transaction1.setAmount(100.0);
            transaction1.setDescription("Grocery shopping");
            transaction1.setCategory(category1);
            transaction1.setAccount(account1);

            AccountTransaction transaction2 = new AccountTransaction();
            transaction2.setAmount(200.0);
            transaction2.setDescription("Electricity bill");
            transaction2.setCategory(category2);
            transaction2.setAccount(account2);

            transactionRepository.saveAll(List.of(transaction1, transaction2));


            // Create Budgets
            Budget budget1 = new Budget();
            budget1.setAmount(500.0);
            budget1.setStartDate(LocalDate.now().minusDays(30));
            budget1.setEndDate(LocalDate.now());
            budget1.setCustomer(customer1);

            Budget budget2 = new Budget();
            budget2.setAmount(300.0);
            budget2.setStartDate(LocalDate.now().minusDays(10));
            budget2.setEndDate(LocalDate.now());
            budget2.setCustomer(customer2);

            budgetRepository.saveAll(List.of(budget1, budget2));
        };
    }
}
