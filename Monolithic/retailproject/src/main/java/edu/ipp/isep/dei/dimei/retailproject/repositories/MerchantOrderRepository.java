package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantOrderRepository extends CrudRepository<MerchantOrder, Integer> {

    Optional<MerchantOrder> findById(int id);

    List<MerchantOrder> findByUser(User user);

    Optional<MerchantOrder> findByOrder(Order order);

    List<MerchantOrder> findAll();

}
