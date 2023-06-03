package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.accounts a WHERE a.accountId = :id")
    Optional<User> findUserByAccountId(@Param("id") long id);

    User findUserById(long id);

    void deleteUserById(long id);

}
