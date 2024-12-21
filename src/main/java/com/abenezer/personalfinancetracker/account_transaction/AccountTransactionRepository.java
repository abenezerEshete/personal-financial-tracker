package com.abenezer.personalfinancetracker.account_transaction;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends CrudRepository<AccountTransaction, Long> {
    List<AccountTransaction> findAll();

    List<AccountTransaction> findByAccountId(@Param("accountId") Long accountId);


    @Query("SELECT t FROM AccountTransaction t WHERE t.date BETWEEN :startDate AND :endDate")
    List<AccountTransaction> findTransactionsWithinDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM AccountTransaction t " +
            "JOIN t.account a " +
            "JOIN t.category c " +
            "WHERE a.id = :accountId AND c.id = :categoryId AND t.date BETWEEN :startDate AND :endDate")
    List<AccountTransaction> findTransactionsByAccountAndCategory(
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM AccountTransaction t WHERE t.account.id = :accountId")
    double sumTransactionsByAccount(Long accountId);

}
