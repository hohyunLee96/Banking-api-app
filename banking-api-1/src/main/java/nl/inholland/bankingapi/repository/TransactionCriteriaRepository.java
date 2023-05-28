package nl.inholland.bankingapi.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionSearchCriteria;
import nl.inholland.bankingapi.model.pages.TransactionPage;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionCriteriaRepository {
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public TransactionCriteriaRepository(TransactionRepository transactionRepository, EntityManager entityManager) {
        this.transactionRepository = transactionRepository;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }
    public Page<Transaction> findAllWithFilters(TransactionPage transactionPage, TransactionSearchCriteria searchCriteria ) {
        CriteriaQuery<Transaction> query = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> transactionRoot = query.from(Transaction.class);
        Predicate predicate = getPredicate(searchCriteria, transactionRoot);
        query.where(predicate);
        setOrder(transactionPage, query, transactionRoot);
        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(transactionPage.getPageNumber() * transactionPage.getPageSize());
        typedQuery.setMaxResults(transactionPage.getPageSize());
        Pageable pageable = getPageable(transactionPage);
        long transactionCount = getTransactionCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, transactionCount);
    }

    private long getTransactionCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }


    private void setOrder(TransactionPage transactionPage, CriteriaQuery<Transaction> query, Root<Transaction> transactionRoot) {
        query.orderBy(criteriaBuilder.desc(transactionRoot.get(transactionPage.getSortBy())));
    }

    Predicate getPredicate(TransactionSearchCriteria searchCriteria, Root<Transaction> transactionRoot) {
        return criteriaBuilder.and(
                criteriaBuilder.like(transactionRoot.get("fromIban"), "%" + searchCriteria.getFromIban() + "%"),
                criteriaBuilder.like(transactionRoot.get("toIban"), "%" + searchCriteria.getToIban() + "%"),
                criteriaBuilder.greaterThanOrEqualTo(transactionRoot.get("timestamp"), searchCriteria.getFromDate()),
                criteriaBuilder.lessThanOrEqualTo(transactionRoot.get("timestamp"), searchCriteria.getToDate()),
                criteriaBuilder.like(transactionRoot.get("amount"), searchCriteria.getAmount() + "%")
        );
    }


    private Pageable getPageable(TransactionPage transactionPage) {
        Sort sort = Sort.by(transactionPage.getSortDirection(), transactionPage.getSortBy());
        return PageRequest.of(transactionPage.getPageNumber(), transactionPage.getPageSize(), sort);
    }


}
