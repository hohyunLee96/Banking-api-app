package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserById(long id);
}
