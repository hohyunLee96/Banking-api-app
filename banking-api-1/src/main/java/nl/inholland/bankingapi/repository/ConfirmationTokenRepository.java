package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long>{
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
