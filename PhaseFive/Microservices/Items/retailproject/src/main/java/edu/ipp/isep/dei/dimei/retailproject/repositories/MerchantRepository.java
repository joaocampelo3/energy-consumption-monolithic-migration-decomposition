package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends CrudRepository<Merchant, Integer> {
    Optional<Merchant> findById(int id);

    Optional<Merchant> findByEmail(String email);
}
