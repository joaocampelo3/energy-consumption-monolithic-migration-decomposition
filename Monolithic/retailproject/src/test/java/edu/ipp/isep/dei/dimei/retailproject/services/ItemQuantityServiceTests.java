package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemQuantityRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemQuantityServiceTests {
    @InjectMocks
    ItemQuantityService itemQuantityService;
    @Mock
    ItemQuantityRepository itemQuantityRepository;
    @Mock
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantity itemQuantity1;
    Category category;
    Address merchantAddress;
    Merchant merchant;
    Item item1;

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

        itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item1)
                .build();

        itemQuantityDTO1 = new ItemQuantityDTO(itemQuantity1);

    }

    @Test
    void test_CreateItemQuantity() throws NotFoundException, InvalidQuantityException {
        // Define the behavior of the mock
        when(itemService.getItemById(itemQuantityDTO1.getId()))
                .thenReturn(item1);
        when(itemQuantityRepository.findById(itemQuantityDTO1.getId()))
                .thenReturn(Optional.ofNullable(itemQuantity1));

        // Call the service method that uses the Repository
        ItemQuantity result = itemQuantityService.createItemQuantity(itemQuantityDTO1);
        ItemQuantity expected = itemQuantity1;

        // Perform assertions
        verify(itemService, atLeastOnce()).getItemById(itemQuantityDTO1.getId());
        verify(itemQuantityRepository, atLeastOnce()).findById(itemQuantityDTO1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetItemQuantityById() throws NotFoundException {
        // Define the behavior of the mock
        when(itemQuantityRepository.findById(itemQuantityDTO1.getId()))
                .thenReturn(Optional.ofNullable(itemQuantity1));

        // Call the service method that uses the Repository
        ItemQuantity result = itemQuantityService.getItemQuantityById(itemQuantityDTO1.getItemId());
        ItemQuantity expected = itemQuantity1;

        // Perform assertions
        verify(itemQuantityRepository, atLeastOnce()).findById(itemQuantityDTO1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
