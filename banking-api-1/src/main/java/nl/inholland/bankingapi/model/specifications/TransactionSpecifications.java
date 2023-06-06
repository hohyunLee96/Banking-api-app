package nl.inholland.bankingapi.model.specifications;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Date;

@Component
public class TransactionSpecifications {
    private TransactionSpecifications() {
    }
    public static Specification<Transaction> hasFromIban(String fromIban) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("fromIban").get("IBAN"), fromIban);
    }
    public static Specification<Transaction> hasToIban(String toIban) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("toIban").get("IBAN"), toIban);
    }
    public static Specification<Transaction> hasAmountGreaterThan(double amount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("amount"), amount);
    }
    public static Specification<Transaction> hasAmountLessThan(double amount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("amount"), amount);
    }
    public static Specification<Transaction> hasAmountEqualTo(double amount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("amount"), amount);
    }
    public static Specification<Transaction> hasDateGreaterThan(String date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), date);
    }
    public static Specification<Transaction> hasDateLessThan(String date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), date);
    }
    public static Specification<Transaction> hasDateEqualTo(Date date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("timestamp"), date);
    }
    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);
    }
    public static Specification<Transaction> hasPerformingUser(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("performingUser"), userId);
    }


    public static Specification<Transaction>getSpecifications(String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser, Date searchDate) {
        Specification<Transaction> spec = null;
        Specification<Transaction> temp=null;
        if (fromIban != null) {
           temp=hasFromIban(fromIban);
           spec=spec==null?temp:spec.and(temp);
        }
        if (toIban != null) {
            temp=hasToIban(toIban);
            spec=spec==null?temp:spec.and(temp);
        }
        if (lessThanAmount != null) {
            temp=hasAmountLessThan(lessThanAmount);
            spec=spec==null?temp:spec.and(temp);
        }
        if (greaterThanAmount != null) {
            temp=hasAmountGreaterThan(greaterThanAmount);
            spec=spec==null?temp:spec.and(temp);
        }
        if (equalToAmount!= null) {
            temp=hasAmountEqualTo(equalToAmount);
            spec=spec==null?temp:spec.and(temp);
        }
        if (fromDate != null) {
            temp=hasDateGreaterThan(fromDate);
            spec=spec==null?temp:spec.and(temp);
        }
        if (toDate!= null) {
            temp=hasDateLessThan(toDate);
            spec=spec==null?temp:spec.and(temp);
        }
        if (type!= null) {
            temp=hasType(type);
            spec=spec==null?temp:spec.and(temp);
        }
        if (performingUser!= null) {
            temp=hasPerformingUser(performingUser);
            spec=spec==null?temp:spec.and(temp);
        }
        if (searchDate!= null) {
            temp=hasDateEqualTo(searchDate);
            spec=spec==null?temp:spec.and(temp);
        }

        return spec;
    }
}
