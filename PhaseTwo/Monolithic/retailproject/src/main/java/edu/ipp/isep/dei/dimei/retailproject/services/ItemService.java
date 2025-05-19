package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
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

    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Item not found.";
    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong item payload.";
    private final ItemRepository itemRepository;
    private final MerchantService merchantService;

    public List<ItemDTO> getAllItems() {
        List<Item> items = new ArrayList<>();
        List<ItemDTO> itemDTOS = new ArrayList<>();

        this.itemRepository.findAll().forEach(items::add);

        for (Item item : items) {
            itemDTOS.add(new ItemDTO(item));
        }

        return itemDTOS;
    }

    public List<ItemDTO> getUserItems(UserDTO userDTO) throws NotFoundException {
        Merchant merchant = getItemMerchantByUser(userDTO);

        List<Item> items = this.itemRepository.findAllByMerchantId(merchant.getId());
        List<ItemDTO> itemDTOS = new ArrayList<>();

        for (Item item : items) {
            itemDTOS.add(new ItemDTO(item));
        }

        return itemDTOS;
    }

    public ItemDTO getItemDTO(int id) throws NotFoundException {
        return new ItemDTO(getItemById(id));
    }

    public ItemDTO getUserItemDTO(UserDTO userDTO, int id) throws NotFoundException {
        return new ItemDTO(getUserItemById(userDTO, id));
    }

    public Item getItemById(int id) throws NotFoundException {
        return this.itemRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
    }

    private Item getUserItemById(UserDTO userDTO, int id) throws NotFoundException {
        if (RoleEnum.MERCHANT.compareTo(userDTO.getRole()) == 0) {
            Merchant merchant = getItemMerchantByUser(userDTO);

            return this.itemRepository.findById(id).filter(item -> item.getMerchant().equals(merchant)).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return getItemById(id);
        }
    }

    public ItemDTO createItem(ItemDTO itemDTO) throws NotFoundException, BadPayloadException, InvalidQuantityException {
        Merchant userMerchant = getItemMerchantByUser(itemDTO.getUserDTO());

        if (itemDTO.getMerchant().getId() != userMerchant.getId()) {
            throw new BadPayloadException("You can not create item for this merchant.");
        }

        Item item = Item.builder()
                .name(itemDTO.getItemName())
                .sku(itemDTO.getSku())
                .description(itemDTO.getItemDescription())
                .price(itemDTO.getPrice())
                .quantityInStock(new StockQuantity(itemDTO.getQuantityInStock()))
                .merchant(itemDTO.getMerchant().dtoToEntity())
                .build();

        item = this.itemRepository.save(item);

        return new ItemDTO(item);
    }

    private Merchant getItemMerchantByUser(UserDTO userDTO) throws NotFoundException {
        return this.merchantService.getMerchantByUser(userDTO);
    }

    public ItemDTO deleteItem(int id) throws NotFoundException {
        Item item = getItemById(id);

        this.itemRepository.deleteById(item.getId());

        return new ItemDTO(item);
    }

    public ItemDTO addItemStock(int id, ItemUpdateDTO itemUpdateDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        return changeItemStock(itemUpdateDTO.getUserDTO(), id, itemUpdateDTO, "addItemStock");

    }

    public ItemDTO removeItemStock(int id, ItemUpdateDTO itemUpdateDTO) throws BadPayloadException, NotFoundException, InvalidQuantityException {
        return changeItemStock(itemUpdateDTO.getUserDTO(), id, itemUpdateDTO, "removeItemStock");
    }

    private ItemDTO changeItemStock(UserDTO userDTO, int id, ItemUpdateDTO itemUpdateDTO, String action) throws BadPayloadException, NotFoundException, InvalidQuantityException {

        if (id != itemUpdateDTO.getId() || itemUpdateDTO.getQuantityInStock() < 0) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        } else {
            Item item;
            if (userDTO.getRole() == RoleEnum.MERCHANT) {
                item = getUserItemById(userDTO, id);
            } else {
                item = getItemById(id);
            }

            if (action.compareTo("removeItemStock") == 0 && item.getQuantityInStock().getQuantity() >= itemUpdateDTO.getQuantityInStock()) {
                item.getQuantityInStock().decreaseStockQuantity(itemUpdateDTO.getQuantityInStock());
                item = this.itemRepository.save(item);
                return new ItemDTO(item);
            } else if (action.compareTo("addItemStock") == 0 && item.getQuantityInStock().getQuantity() <= itemUpdateDTO.getQuantityInStock()) {
                item.getQuantityInStock().increaseStockQuantity(itemUpdateDTO.getQuantityInStock());
                item = this.itemRepository.save(item);
                return new ItemDTO(item);
            } else {
                throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
            }
        }

    }
}
