package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    Optional<Item> findById(int id);

    @Query("SELECT i FROM Item i where i.merchant.id = :merchantId")
    List<Item> findAllByMerchantId(@Param("merchantId") int merchantId);

    Optional<Item> findBySku(String sku);
}
