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
    private final UserService userService;
    private final AddressRepository addressRepository;

    public AddressDTO createAddress(String authorizationToken, AddressDTO addressDTO) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);
        Address address = addressDTO.dtoToEntity();

        if (!existsAddressByStreetAndZipCodeAndCityAndCountry(address, user.getId())) {
            address.setUser(user);
            address = this.addressRepository.save(address);
        } else {
            address = getAddressByUser(address, user.getId());
        }

        return new AddressDTO(address);
    }

    private boolean existsAddressByStreetAndZipCodeAndCityAndCountry(Address address, int userId) {
        return this.addressRepository.existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(address.getStreet(), address.getZipCode(), address.getCity(), address.getCountry(), userId);
    }

    private Address getAddressByUser(Address address, int userId) throws NotFoundException {
        return this.addressRepository.findAddressByStreetAndZipCodeAndCityAndCountry(address.getStreet(), address.getZipCode(), address.getCity(), address.getCountry())
                .filter(address1 -> address1.getUser().getId() == userId
                        && address1.getStreet().compareTo(address.getStreet()) == 0
                        && address1.getZipCode().compareTo(address.getZipCode()) == 0
                        && address1.getCity().compareTo(address.getCity()) == 0
                        && address1.getCountry().compareTo(address.getCountry()) == 0)
                .orElseThrow(() -> new NotFoundException("Address not found."));
    }
}
