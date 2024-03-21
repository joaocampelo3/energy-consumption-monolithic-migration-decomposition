package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemQuantityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class ItemQuantityService {
    private final ItemQuantityRepository itemQuantityRepository;
    private final ItemService itemService;

    public ItemQuantity createItemQuantity(ItemQuantityDTO itemQuantityDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        ItemQuantity itemQuantity = itemQuantityDTO.dtoToEntity();

        Item item = this.itemService.getItemById(itemQuantityDTO.getItemId());

        if (hasStock(itemQuantity, item)) {
            itemQuantity.setItem(item);
            itemQuantity = this.itemQuantityRepository.save(itemQuantity);
        }
        return itemQuantity;
    }

    private boolean hasStock(ItemQuantity itemQuantity, Item item) throws BadPayloadException {
        if (itemQuantity.getQuantityOrdered().getQuantity() > item.getQuantityInStock().getQuantity()) {
            throw new BadPayloadException("Wrong Item Quantity payload.");
        }
        return true;
    }
}
