package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ShippingOrderTest {
    final ShippingOrderStatusEnum status = ShippingOrderStatusEnum.PENDING;
    int id;
    double price;
    Instant shippingOrderDate;
    Address shippingAddress;
    Order order;
    MerchantOrder merchantOrder;
    User user;
    ShippingOrder shippingOrderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        price = 12.0;

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

        Merchant merchant = Merchant.builder()
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
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();
        List<ItemQuantity> itemQuantityList = new ArrayList<>();
        ItemQuantity itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
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

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        shippingAddress = Address.builder()
                .id(2)
                .street("Another Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        shippingOrderExpected = ShippingOrder.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .status(status)
                .shippingAddress(shippingAddress)
                .order(order)
                .merchantOrder(merchantOrder)
                .user(user)
                .build();
    }

    @Test
    void test_createShippingOrder() {
        ShippingOrder shippingOrder = new ShippingOrder(id, shippingOrderDate, status, shippingAddress, order, merchantOrder, user);

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddress, shippingOrder.getShippingAddress());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
        assertEquals(user, shippingOrder.getUser());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrder2() {
        ShippingOrder shippingOrder = new ShippingOrder(user, order, merchantOrder, shippingAddress);

        assertNotNull(shippingOrder);
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddress, shippingOrder.getShippingAddress());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
        assertEquals(user, shippingOrder.getUser());
        shippingOrderExpected.setId(0);
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrderBuilder() {
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .status(status)
                .shippingAddress(shippingAddress)
                .order(order)
                .merchantOrder(merchantOrder)
                .user(user)
                .build();

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddress, shippingOrder.getShippingAddress());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
        assertEquals(user, shippingOrder.getUser());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_ShippingOrderSets() {
        ShippingOrder shippingOrder = ShippingOrder.builder().build();

        shippingOrder.setId(id);
        shippingOrder.setShippingOrderDate(shippingOrderDate);
        shippingOrder.setStatus(status);
        shippingOrder.setShippingAddress(shippingAddress);
        shippingOrder.setOrder(order);
        shippingOrder.setMerchantOrder(merchantOrder);
        shippingOrder.setUser(user);

        assertEquals(shippingOrder.getShippingOrderDate(), shippingOrder.getShippingOrderDate());
        assertEquals(shippingOrder.getStatus(), shippingOrder.getStatus());
        assertEquals(shippingOrder.getShippingAddress(), shippingOrder.getShippingAddress());
        assertEquals(shippingOrder.getOrder(), shippingOrder.getOrder());
        assertEquals(shippingOrder.getMerchantOrder(), shippingOrder.getMerchantOrder());
        assertEquals(shippingOrder.getUser(), shippingOrder.getUser());

        assertEquals(shippingOrderExpected, shippingOrder);
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
    }

    @Test
    void test_ShippingOrderSets2() {
        ShippingOrder shippingOrder = ShippingOrder.builder().build();

        shippingOrder.setId(2);
        shippingOrder.setShippingOrderDate(Instant.MAX);
        shippingOrder.setStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder.setShippingAddress(shippingAddress);
        shippingOrder.setOrder(order);
        shippingOrder.setMerchantOrder(merchantOrder);
        shippingOrder.setUser(user);

        assertNotEquals(shippingOrder.getId(), id);
        assertNotEquals(shippingOrder.getShippingOrderDate(), shippingOrderDate);
        assertNotEquals(shippingOrder.getStatus(), status);

        assertNotEquals(shippingOrderExpected, shippingOrder);
        assertNotEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
    }

    @Test
    void test_isApprovedShippingOrderFail() {
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isApproved());
    }

    @Test
    void test_isShippedShippingOrderFail() {
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isShipped());
    }

    @Test
    void test_isPendingOrApprovedShippingOrder() {
        shippingOrderExpected.setStatus(ShippingOrderStatusEnum.APPROVED);
        assertNotNull(shippingOrderExpected);
        assertTrue(shippingOrderExpected.isPendingOrApproved());
    }

    @Test
    void test_isPendingOrApprovedShippingOrderFail() {
        shippingOrderExpected.setStatus(ShippingOrderStatusEnum.SHIPPED);
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isPendingOrApproved());
    }
}
