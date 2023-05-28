package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User getUserById(long id);
    Optional<User> findUsersByEmail(String email);
}
