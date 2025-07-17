package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingOrderRepository extends CrudRepository<ShippingOrder, Integer> {
    Optional<ShippingOrder> findById(int id);

    List<ShippingOrder> findByUserId(int userId);

    Optional<ShippingOrder> findByOrderId(int orderId);

    @NonNull
    List<ShippingOrder> findAll();

    void deleteByOrderId(int orderId);
}
