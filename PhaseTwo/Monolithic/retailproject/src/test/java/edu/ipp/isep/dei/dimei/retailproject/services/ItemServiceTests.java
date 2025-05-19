package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {
    @InjectMocks
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    MerchantService merchantService;
    ItemDTO itemDTO1;
    Item item1;
    Item newItem1;
    Item item1Updated;
    AddressDTO merchantAddressDTO;
    Merchant merchant;
    List<Item> items = new ArrayList<>();
    List<ItemDTO> itemDTOs = new ArrayList<>();
    UserDTO merchantUserDTO;
    UserDTO userDTO;
    ItemDTO itemDTO1Updated;
    ItemUpdateDTO itemUpdateDTO1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        merchantAddressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(merchantAddressDTO.getId())
                .build();

        merchantUserDTO = UserDTO.builder()
                .userId(1)
                .email("merchant_email@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        userDTO = UserDTO.builder()
                .userId(2)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .merchant(merchant)
                .build();

        newItem1 = Item.builder()
                .id(0)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .merchant(merchant)
                .build();

        item1Updated = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .merchant(merchant)
                .build();

        items.add(item1);

        itemDTO1 = new ItemDTO(item1);

        itemDTOs.add(itemDTO1);

        itemDTO1Updated = new ItemDTO(item1);

        itemUpdateDTO1 = ItemUpdateDTO.builder()
                .id(itemDTO1.getId())
                .sku(itemDTO1.getSku())
                .price(itemDTO1.getPrice())
                .quantityInStock(itemDTO1.getQuantityInStock())
                .userDTO(userDTO)
                .build();
    }

    @Test
    void test_GetAllItem() {
        // Define the behavior of the mock
        when(itemRepository.findAll())
                .thenReturn(items);

        // Call the service method that uses the Repository
        List<ItemDTO> result = itemService.getAllItems();
        List<ItemDTO> expected = itemDTOs;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserItems() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantService.getMerchantByUser(userDTO))
                .thenReturn(merchant);
        when(itemRepository.findAllByMerchantId(merchant.getId()))
                .thenReturn(items);

        // Call the service method that uses the Repository
        List<ItemDTO> result = itemService.getUserItems(userDTO);
        List<ItemDTO> expected = itemDTOs;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(userDTO);
        verify(itemRepository, atLeastOnce()).findAllByMerchantId(merchant.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetItemDTO() throws NotFoundException {
        // Define the behavior of the mock
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        ItemDTO result = itemService.getItemDTO(item1.getId());
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserItemDTO() throws NotFoundException {
        // Define the behavior of the mock
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        ItemDTO result = itemService.getUserItemDTO(userDTO, item1.getId());
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetItemById() throws NotFoundException {
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        Item result = itemService.getItemById(item1.getId());
        Item expected = item1;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateItem() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        itemDTO1.setUserDTO(merchantUserDTO);
        when(merchantService.getMerchantByUser(merchantUserDTO))
                .thenReturn(merchant);
        when(itemRepository.save(newItem1)).thenReturn(item1);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.createItem(itemDTO1);
        itemDTO1.setUserDTO(null);
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(merchantUserDTO);
        verify(itemRepository, atLeastOnce()).save(newItem1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteItem() throws NotFoundException {
        // Define the behavior of the mock
        itemDTO1.setUserDTO(null);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        ItemDTO result = itemService.deleteItem(item1.getId());
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_AddItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        item1Updated.getQuantityInStock().setQuantity(item1Updated.getQuantityInStock().getQuantity() + 1);
        itemDTO1Updated.setQuantityInStock(itemDTO1.getQuantityInStock() + 1);
        itemUpdateDTO1.setQuantityInStock(itemDTO1.getQuantityInStock() + 1);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1Updated)).thenReturn(item1Updated);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.addItemStock(item1.getId(), itemUpdateDTO1);
        ItemDTO expected = itemDTO1Updated;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        verify(itemRepository, atLeastOnce()).save(item1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RemoveItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        item1Updated.getQuantityInStock().setQuantity(item1Updated.getQuantityInStock().getQuantity() - 1);
        itemDTO1Updated.setQuantityInStock(itemDTO1.getQuantityInStock() - 1);
        itemUpdateDTO1.setQuantityInStock(itemDTO1.getQuantityInStock() - 1);

        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1Updated)).thenReturn(item1Updated);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.removeItemStock(item1.getId(), itemUpdateDTO1);
        ItemDTO expected = itemDTO1Updated;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        verify(itemRepository, atLeastOnce()).save(item1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }
}
