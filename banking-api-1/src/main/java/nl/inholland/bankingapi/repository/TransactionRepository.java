package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByFromIban(Account fromIban);

    List<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

    List<Transaction> findAllByFromIbanAndTimestamp(Account fromIban, LocalDateTime timestamp);
}
