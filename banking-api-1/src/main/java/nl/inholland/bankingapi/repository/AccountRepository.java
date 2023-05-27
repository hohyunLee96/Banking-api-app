package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account,Long> {
    List<Account> getIBANByUserFirstName(String firstName);

    Account findAccountByIBAN(String IBAN);

}
