package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    User findUserByAccountsAccountId(long id);

    User findUserById(long id);

    List<User> findAllByHasAccount(boolean hasAccount);
}
