package com.abenezer.personalfinancetracker.account_transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountTransactionCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<AccountTransaction> findTransactions(Double minAmount, Long categoryId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountTransaction> cq = cb.createQuery(AccountTransaction.class);
        Root<AccountTransaction> transaction = cq.from(AccountTransaction.class);

        Predicate amountPredicate = cb.greaterThanOrEqualTo(transaction.get("amount"), minAmount);
        Predicate categoryPredicate = cb.equal(transaction.get("category").get("id"), categoryId);

        if (categoryId != null) {
            cq.where(cb.and(amountPredicate, categoryPredicate));
        } else {
            cq.where(amountPredicate);
        }

        return entityManager.createQuery(cq).getResultList();
    }
}
