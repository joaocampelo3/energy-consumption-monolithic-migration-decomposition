package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findById(int id);
}
