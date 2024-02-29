package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantOrderRepository extends CrudRepository<MerchantOrder, Integer> {

    Optional<MerchantOrder> findById(int id);

    List<MerchantOrder> findByUser(User user);

    List<MerchantOrder> findAll();

    Page<MerchantOrder> findAll(Specification<MerchantOrder> specs, Pageable pageable);

}
