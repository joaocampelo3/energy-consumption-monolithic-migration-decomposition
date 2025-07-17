package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantOrderRepository extends CrudRepository<MerchantOrder, Integer> {
    Optional<MerchantOrder> findById(int id);

    List<MerchantOrder> findByMerchantId(int merchantId);

    Optional<MerchantOrder> findByOrderId(int orderId);

    @NonNull
    List<MerchantOrder> findAll();

    void deleteByOrderId(int orderId);
    void deleteByMerchantId(int merchantId);
}
