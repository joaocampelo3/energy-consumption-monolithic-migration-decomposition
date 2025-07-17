package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.Publisher;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.events.ItemEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ItemRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;
    private final MerchantService merchantService;

    @Autowired
    Publisher publisher;

    public List<ItemDTO> getAllItems() {
        List<Item> items = new ArrayList<>();
        List<ItemDTO> itemDTOS = new ArrayList<>();

        this.itemRepository.findAll().forEach(items::add);

        for (Item item : items) {
            itemDTOS.add(new ItemDTO(item));
        }

        return itemDTOS;
    }

    public List<ItemDTO> getUserItems(String authorizationToken) throws NotFoundException {
        Merchant merchant = getItemMerchantByUser(authorizationToken);

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

    public ItemDTO getUserItemDTO(String authorizationToken, int id) throws NotFoundException {
        return new ItemDTO(getUserItemById(authorizationToken, id));
    }

    public Item getItemById(int id) throws NotFoundException {
        return this.itemRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
    }

    private Item getUserItemById(String authorizationToken, int id) throws NotFoundException {
        String userRole = userService.getRoleFromAuthorizationString(authorizationToken);
        String userEmail = userService.getEmailFromAuthorizationString(authorizationToken);

        if (RoleEnum.ADMIN.name().equals(userRole)) {
            return this.itemRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            Merchant merchant = merchantService.getMerchantByUser(userEmail);
            return this.itemRepository.findById(id).filter(item -> item.getMerchant().equals(merchant)).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }

    }

    public ItemDTO createItem(String authorizationToken, ItemDTO itemDTO, boolean isEvent) throws NotFoundException, BadPayloadException, InvalidQuantityException {
        Merchant userMerchant;
        if (isEvent) {
            userMerchant = itemDTO.getMerchant().dtoToEntity();
        }
        else
        {
            userMerchant = getItemMerchantByUser(authorizationToken);
        }

        if (itemDTO.getMerchant().getId() != userMerchant.getId()) {
            throw new BadPayloadException("You can not create item for this merchant.");
        }

        Item item = Item.builder()
                .name(itemDTO.getItemName())
                .sku(itemDTO.getSku())
                .description(itemDTO.getItemDescription())
                .price(itemDTO.getPrice())
                .quantityInStock(new StockQuantity(itemDTO.getQuantityInStock()))
                .category(itemDTO.getCategory().dtoToEntity())
                .merchant(itemDTO.getMerchant().dtoToEntity())
                .build();

        item = this.itemRepository.save(item);

        ItemDTO itemDTO1 = new ItemDTO(item);

        try {
            publisher.mainPublish(new ItemEvent(itemDTO1, EventTypeEnum.CREATE), ItemRoutingKeyEnum.ITEM_CREATED.getItemKey());
        } catch (Exception e) {
            return itemDTO1;
        }

        return itemDTO1;
    }

    private Merchant getItemMerchantByUser(String authorizationToken) throws NotFoundException {
        String userEmail = userService.getEmailFromAuthorizationString(authorizationToken);

        return this.merchantService.getMerchantByUser(userEmail);
    }

    public ItemDTO deleteItem(String authorizationToken, int id, boolean isEvent) throws NotFoundException {
        Item item;

        if (!isEvent) {
            item = getUserItemById(authorizationToken, id);
        } else
        {
            item = getItemById(id);
        }

        ItemDTO itemDTO = new ItemDTO(item);

        this.itemRepository.deleteById(item.getId());

        try {
            publisher.mainPublish(new ItemEvent(itemDTO, EventTypeEnum.DELETE), ItemRoutingKeyEnum.ITEM_DELETED.getItemKey());
        } catch (Exception e) {
            return itemDTO;
        }

        return itemDTO;
    }

    public ItemDTO addItemStock(String authorizationToken, int id, ItemUpdateDTO itemUpdateDTO, boolean isEvent) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        return changeItemStock(authorizationToken, id, itemUpdateDTO, "addItemStock", isEvent);

    }

    public ItemDTO removeItemStock(String authorizationToken, int id, ItemUpdateDTO itemUpdateDTO, boolean isEvent) throws BadPayloadException, NotFoundException, InvalidQuantityException {
        return changeItemStock(authorizationToken, id, itemUpdateDTO, "removeItemStock", isEvent);
    }

    private ItemDTO changeItemStock(String authorizationToken, int id, ItemUpdateDTO itemUpdateDTO, String action, boolean isEvent) throws BadPayloadException, NotFoundException, InvalidQuantityException {

        if (id != itemUpdateDTO.getId() || itemUpdateDTO.getQuantityInStock() < 0) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        } else {
            String userRole = this.userService.getRoleFromAuthorizationString(authorizationToken);

            Item item;
            if (RoleEnum.MERCHANT.name().equals(userRole) && !isEvent) {
                item = getUserItemById(authorizationToken, id);
            } else {
                item = getItemById(id);
            }

            if (action.compareTo("removeItemStock") == 0 && item.getQuantityInStock().getQuantity() >= itemUpdateDTO.getQuantityInStock()) {
                item.getQuantityInStock().decreaseStockQuantity(itemUpdateDTO.getQuantityInStock());
                item = this.itemRepository.save(item);

                ItemDTO itemDTO = new ItemDTO(item);

                try {
                    publisher.mainPublish(new ItemEvent(itemDTO, EventTypeEnum.UPDATE), ItemRoutingKeyEnum.ITEM_REMOVE_STOCK.getItemKey());
                } catch (Exception e) {
                    return itemDTO;
                }

                return itemDTO;
            } else if (action.compareTo("addItemStock") == 0 && item.getQuantityInStock().getQuantity() <= itemUpdateDTO.getQuantityInStock()) {
                item.getQuantityInStock().increaseStockQuantity(itemUpdateDTO.getQuantityInStock());
                item = this.itemRepository.save(item);

                ItemDTO itemDTO = new ItemDTO(item);

                try {
                    publisher.mainPublish(new ItemEvent(itemDTO, EventTypeEnum.UPDATE), ItemRoutingKeyEnum.ITEM_ADD_STOCK.getItemKey());
                } catch (Exception e) {
                    return itemDTO;
                }

                return itemDTO;
            } else {
                throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
            }
        }

    }
}
