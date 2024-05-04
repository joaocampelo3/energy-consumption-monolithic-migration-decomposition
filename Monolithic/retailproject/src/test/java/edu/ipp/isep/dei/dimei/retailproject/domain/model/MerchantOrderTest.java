package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MerchantOrderTest {
    int id;
    LocalDateTime orderDate;
    MerchantOrderStatusEnum status = MerchantOrderStatusEnum.PENDING;
    User user;
    Order order;
    Merchant merchant;
    MerchantOrder merchantOrderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        LocalDateTime currentDate = LocalDateTime.now();
        orderDate = currentDate;

        Account userAccount = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();
        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(userAccount)
                .build();

        Address address = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(1)
                .name("MerchantOrder 1")
                .email("merchant_email@gmail.com")
                .address(address)
                .build();
        Category category = Category.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Description")
                .price(12.0)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();
        List<ItemQuantity> itemQuantityList = new ArrayList<>();
        ItemQuantity itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .build();
        itemQuantityList.add(itemQuantity1);
        double totalPrice = itemQuantityList.stream().mapToDouble(value -> value.getItem().getPrice() * value.getQuantityOrdered().getQuantity()).sum();

        Payment payment = Payment.builder()
                .id(1)
                .amount(totalPrice)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList)
                .payment(payment)
                .build();

        merchantOrderExpected = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();
    }

    @Test
    void test_createMerchantOrder() {
        MerchantOrder merchantOrder = new MerchantOrder(id, orderDate, status, user, order, merchant);

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_createMerchantOrder2() {
        MerchantOrder merchantOrder = new MerchantOrder(user, order, merchant);

        assertNotNull(merchantOrder);
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        merchantOrderExpected.setId(0);
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_createMerchantOrderBuilder() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }
}
