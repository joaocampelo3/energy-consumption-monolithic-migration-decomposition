package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemQuantityRepository extends CrudRepository<ItemQuantity, Integer> {
    Optional<ItemQuantity> findById(int id);

    Optional<ItemQuantity> findByItemId(int item_id);
}
