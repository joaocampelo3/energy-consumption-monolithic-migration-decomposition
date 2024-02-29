package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingOrderRepository extends CrudRepository<ShippingOrder, Integer> {
    Optional<ShippingOrder> findById(int id);

    List<ShippingOrder> findByUser(User user);

    List<ShippingOrder> findAll();

    Page<ShippingOrder> findAll(Specification<ShippingOrder> specs, Pageable pageable);

}
