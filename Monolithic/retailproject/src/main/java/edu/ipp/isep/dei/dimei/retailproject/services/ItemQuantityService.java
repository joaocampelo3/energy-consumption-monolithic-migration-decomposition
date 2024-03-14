package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
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

    public ItemQuantity createItemQuantity(ItemQuantityDTO itemQuantityDTO) throws NotFoundException, InvalidQuantityException {
        ItemQuantity itemQuantity = itemQuantityDTO.dtoToEntity();

        Item item = this.itemService.getItemById(itemQuantityDTO.getItemId());

        itemQuantity.setItem(item);
        this.itemQuantityRepository.save(itemQuantity);

        return getItemQuantityById(itemQuantity.getId());
    }

    public ItemQuantity getItemQuantityById(int itemQuantityId) throws NotFoundException {
        return this.itemQuantityRepository.findById(itemQuantityId)
                .orElseThrow(() -> new NotFoundException("Item Quantity not found."));
    }
}
