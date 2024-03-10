package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public Address createAddress(AddressDTO addressDTO, User user) throws NotFoundException {
        Address address = addressDTO.dtoToEntity();

        if (!existsAddressByStreetAndZipCodeAndCityAndCountry(address, user.getId())) {
            address.setUser(user);
            this.addressRepository.save(address);
        }

        return getAddressByUser(address, user.getId());
    }

    private boolean existsAddressByStreetAndZipCodeAndCityAndCountry(Address address, int userId) {
        return this.addressRepository.existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(address.getStreet(), address.getZipCode(), address.getCity(), address.getCountry(), userId);
    }

    public Address getAddressByUser(Address address, int userId) throws NotFoundException {
        return this.addressRepository.findAddressByStreetAndZipCodeAndCityAndCountry(address.getStreet(), address.getZipCode(), address.getCity(), address.getCountry())
                .filter(address1 -> address1.getUser().getId() == userId && address1.getId() == address.getId())
                .orElseThrow(() -> new NotFoundException("Address not found."));
    }
}
