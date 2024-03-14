package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
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

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    MerchantService merchantService;
    @Mock
    UserService userService;
    ItemDTO itemDTO1;
    Item item1;
    Item newItem1;
    Item item1Updated;
    Category category;
    Address merchantAddress;
    Merchant merchant;
    List<Item> items = new ArrayList<>();
    List<ItemDTO> itemDTOs = new ArrayList<>();
    Account account;
    User user;
    ItemDTO itemDTO1Updated;
    ItemUpdateDTO itemUpdateDTO1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        category = Category.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        merchantAddress = Address.builder()
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
                .address(merchantAddress)
                .build();

        account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();

        newItem1 = Item.builder()
                .id(0)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();

        item1Updated = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(10)
                .quantityInStock(new StockQuantity(10))
                .category(category)
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
                .build();
    }

    @Test
    void test_GetAllItem() throws NotFoundException {
        // Define the behavior of the mock
        when(itemRepository.findAll())
                .thenReturn(items);
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        List<ItemDTO> result = itemService.getAllItems();
        List<ItemDTO> expected = itemDTOs;

        // Perform assertions
        verify(itemRepository, atLeastOnce()).findAll();
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserItems() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.findAllByMerchantId(merchant.getId()))
                .thenReturn(items);

        // Call the service method that uses the Repository
        List<ItemDTO> result = itemService.getUserItems(JwtTokenDummy);
        List<ItemDTO> expected = itemDTOs;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(itemRepository, atLeastOnce()).findAllByMerchantId(merchant.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserItemDTO() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        ItemDTO result = itemService.getUserItemDTO(JwtTokenDummy, item1.getId());
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
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
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.save(newItem1)).thenReturn(item1);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.createItem(JwtTokenDummy, itemDTO1);
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(itemRepository, atLeastOnce()).save(newItem1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteItem() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));

        // Call the service method that uses the Repository
        ItemDTO result = itemService.deleteItem(JwtTokenDummy, item1.getId());
        ItemDTO expected = itemDTO1;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
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
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1Updated)).thenReturn(item1Updated);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.addItemStock(JwtTokenDummy, item1.getId(), itemUpdateDTO1);
        ItemDTO expected = itemDTO1Updated;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
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
        when(userService.getUserByToken(JwtTokenDummy))
                .thenReturn(user);
        when(merchantService.getMerchantByUser(user))
                .thenReturn(merchant);
        when(itemRepository.findById(item1.getId()).filter(item -> item.getMerchant().equals(merchant)))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1Updated)).thenReturn(item1Updated);

        // Call the service method that uses the Repository
        ItemDTO result = itemService.removeItemStock(JwtTokenDummy, item1.getId(), itemUpdateDTO1);
        ItemDTO expected = itemDTO1Updated;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchantByUser(user);
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(itemRepository, atLeastOnce()).findById(item1.getId());
        verify(itemRepository, atLeastOnce()).save(item1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }
}
