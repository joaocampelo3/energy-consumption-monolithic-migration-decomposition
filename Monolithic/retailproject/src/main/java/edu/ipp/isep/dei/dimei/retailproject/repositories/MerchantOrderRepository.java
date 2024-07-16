package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantOrderRepository extends CrudRepository<MerchantOrder, Integer> {
    Optional<MerchantOrder> findById(int id);

    List<MerchantOrder> findByMerchantEmail(String email);

    Optional<MerchantOrder> findByOrder(Order order);

    @NonNull
    List<MerchantOrder> findAll();

    void deleteByOrderId(int orderId);
}
