package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemQuantityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class ItemQuantityService {
    private final ItemQuantityRepository itemQuantityRepository;

    public ItemQuantity createItemQuantity(ItemQuantityDTO itemQuantityDTO) throws InvalidQuantityException, BadPayloadException {
        ItemQuantity itemQuantity = itemQuantityDTO.dtoToEntity();

        if (itemQuantityDTO.getItemId() > 0) {
            itemQuantity = this.itemQuantityRepository.save(itemQuantity);
        } else {
            throw new BadPayloadException("Wrong Item Quantity payload.");
        }
        return itemQuantity;
    }
}
