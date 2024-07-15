package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {
    Optional<Address> findById(int id);

    Optional<Address> findAddressByStreetAndZipCodeAndCityAndCountry(String street, String zipCode, String city, String country);

    boolean existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(String street, String zipCode, String city, String country, int userId);
}
