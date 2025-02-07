package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemQuantityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    double price;
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity1Updated;
    Category category;
    AddressDTO merchantAddressDTO;
    Merchant merchant;
    Item item1;
    OrderQuantity orderQuantity;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        price = 10.0;

        category = Category.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

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

        item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();

        orderQuantity = new OrderQuantity(1);

        itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(orderQuantity)
                .item(item1)
                .price(price)
                .build();

        itemQuantity1Updated = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(orderQuantity)
                .item(item1)
                .price(price)
                .build();

        itemQuantityDTO1 = new ItemQuantityDTO(itemQuantity1);

    }

    @Test
    void test_CreateItemQuantity() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        when(itemService.getItemById(itemQuantityDTO1.getId())).thenReturn(item1);
        doReturn(itemQuantity1Updated).when(itemQuantityRepository).save(any(ItemQuantity.class));

        // Call the service method that uses the Repository
        ItemQuantity result = itemQuantityService.createItemQuantity(itemQuantityDTO1);
        ItemQuantity expected = itemQuantity1Updated;

        // Perform assertions
        verify(itemService, atLeastOnce()).getItemById(itemQuantityDTO1.getId());
        verify(itemQuantityRepository, atLeastOnce()).save(any(ItemQuantity.class));
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
