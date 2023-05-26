package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
