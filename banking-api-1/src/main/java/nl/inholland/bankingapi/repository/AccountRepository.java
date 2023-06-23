package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account,Long> {

    List<Account> getAllAccountsByUserId(long id);

    List<Account> findAll(Specification<Account> specification, Pageable pageable);

    Account findAccountByIBAN(String IBAN);

    @Query("SELECT SUM(account.balance) AS totalBalance FROM Account account WHERE account.user.id = :userId")
    Double getTotalBalanceByUserId(long userId);


    boolean existsByUserIdAndAccountType(Long userId, AccountType accountType);

}
