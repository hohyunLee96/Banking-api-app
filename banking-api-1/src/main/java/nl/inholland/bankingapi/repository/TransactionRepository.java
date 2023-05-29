package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByPerformingUser_Id(Long id);

    List<Transaction> findAllByFromIban(Account fromIban);

    List<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

    Page<Transaction> findAllByFromIbanOrToIbanOrTimestampBetweenOrAmountLessThanEqualOrAmountGreaterThanEqualOrAmountEqualsOrTypeOrPerformingUser(Pageable pageable, String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser);

}
