package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    Optional<Item> findById(int id);

    Page<Item> findAll(Specification<Item> specs, Pageable pageable);

    List<Item> findAllByCategoryId(int item_category);

    List<Item> findAllByCategoryId(int item_category, Pageable pageable);

    List<Item> findAllByCategoryId(int item_category, Pageable pageable, Specification<Item> specs);

    List<Item> findAllByNameContaining(String name, Pageable pageable);
}
