package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemQuantityRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final MerchantService merchantService;
    private final ItemQuantityRepository itemQuantityRepository;

    public List<ItemQuantityDTO> getAllItems() throws NotFoundException {
        List<Item> items = new ArrayList<>();
        List<ItemQuantityDTO> itemQuantities = new ArrayList<>();

        this.itemRepository.findAll().forEach(items::add);

        for (Item item : items) {
            itemQuantities.add(new ItemQuantityDTO(this.itemQuantityRepository.findByItemId(item.getId()).orElseThrow(() -> new NotFoundException("Item Quantity not found."))));
        }

        return itemQuantities;
    }

    public List<ItemQuantityDTO> getUserItems(String authorizationToken) throws NotFoundException {
        Merchant merchant = getItemMerchantByUser(authorizationToken);

        List<Item> items = new ArrayList<>(this.itemRepository.findAllByMerchantId(merchant.getId()));

        List<ItemQuantityDTO> itemQuantities = new ArrayList<>();

        for (Item item : items) {
            itemQuantities.add(new ItemQuantityDTO(getItemQuantity(authorizationToken, item.getId())));
        }

        return itemQuantities;
    }

    public ItemQuantityDTO getUserItem(String authorizationToken, int id) throws NotFoundException {
        return new ItemQuantityDTO(getItemQuantity(authorizationToken, id));
    }

    private Item getUserItemById(String authorizationToken, int id) throws NotFoundException {
        Merchant merchant = getItemMerchantByUser(authorizationToken);

        return this.itemRepository.findById(id).filter(item -> item.getMerchant().equals(merchant)).orElseThrow(() -> new NotFoundException("Item not found."));
    }

    private ItemQuantity getItemQuantity(String authorizationToken, int id) throws NotFoundException {
        Item item = getUserItemById(authorizationToken, id);
        return itemQuantityRepository.findByItemId(item.getId()).orElseThrow(() -> new NotFoundException("Item Quantity not found."));
    }

    public ItemDTO createItem(String authorizationToken, ItemDTO itemDTO) throws NotFoundException, BadPayloadException, InvalidQuantityException {
        Merchant userMerchant = getItemMerchantByUser(authorizationToken);

        if (itemDTO.getMerchant().getId() != userMerchant.getId()) {
            throw new BadPayloadException("You can not create item for this merchant.");
        }

        Item item = Item.builder()
                .name(itemDTO.getItemName())
                .description(itemDTO.getItemDescription())
                .price(itemDTO.getPrice())
                .quantityInStock(new StockQuantity(itemDTO.getQuantityInStock()))
                .category(itemDTO.getCategory().dtoToEntity())
                .merchant(itemDTO.getMerchant().dtoToEntity())
                .build();

        return new ItemDTO(this.itemRepository.save(item));
    }

    private Merchant getItemMerchantByUser(String authorizationToken) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.merchantService.getMerchantByUser(user);
    }

    public ItemDTO deleteItem(String authorizationToken, int id) throws NotFoundException {
        Item item = getUserItemById(authorizationToken, id);

        this.itemRepository.deleteById(item.getId());

        return new ItemDTO(item);
    }

    public ItemDTO addItemStock(String authorizationToken, int id, ItemDTO itemDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        if (id != itemDTO.getId() || itemDTO.getQuantityInStock() > 0) {
            throw new BadPayloadException("Wrong item payload.");
        }

        Item item = getUserItemById(authorizationToken, id);

        StockQuantity stockQuantity = new StockQuantity(item.getQuantityInStock().getQuantity() + itemDTO.getQuantityInStock());

        item.setQuantityInStock(stockQuantity);

        return new ItemDTO(this.itemRepository.save(item));

    }

    public ItemDTO removeItemStock(String authorizationToken, int id, ItemDTO itemDTO) throws BadPayloadException, NotFoundException, InvalidQuantityException {
        if (id != itemDTO.getId() || itemDTO.getQuantityInStock() > 0) {
            throw new BadPayloadException("Wrong item payload.");
        }

        Item item = getUserItemById(authorizationToken, id);

        StockQuantity stockQuantity = new StockQuantity(item.getQuantityInStock().getQuantity() - itemDTO.getQuantityInStock());

        item.setQuantityInStock(stockQuantity);

        return new ItemDTO(this.itemRepository.save(item));
    }
}
