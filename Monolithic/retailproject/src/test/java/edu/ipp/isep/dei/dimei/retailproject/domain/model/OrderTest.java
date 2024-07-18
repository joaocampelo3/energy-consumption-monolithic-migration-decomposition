package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
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
class OrderTest {
    final OrderStatusEnum status = OrderStatusEnum.PENDING;
    int id;
    double price;
    Instant orderDate;
    User user;
    List<ItemQuantity> itemQuantities = new ArrayList<>();
    Payment payment;
    Merchant merchant;
    Order orderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        orderDate = currentDate;
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

        merchant = Merchant.builder()
                .id(1)
                .name("Order 1")
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
        itemQuantities = new ArrayList<>();
        ItemQuantity itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();
        itemQuantities.add(itemQuantity1);
        double totalPrice = itemQuantities.stream().mapToDouble(value -> value.getItem().getPrice() * value.getQuantityOrdered().getQuantity()).sum();

        payment = Payment.builder()
                .id(1)
                .amount(totalPrice)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        orderExpected = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .user(user)
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();
    }

    @Test
    void test_createOrder() {
        Order order = new Order(id, orderDate, status, user, itemQuantities, payment);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_createOrder2() {
        Order order = new Order(orderDate, status, user, itemQuantities, payment);

        assertNotNull(order);
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        orderExpected.setId(0);
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_createOrderBuilder() {
        Order order = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .user(user)
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_OrderSets() {
        Order order = Order.builder().build();

        order.setId(id);
        order.setOrderDate(orderDate);
        order.setStatus(status);
        order.setUser(user);
        order.setItemQuantities(itemQuantities);
        order.setPayment(payment);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_isPendingFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.APPROVED;
        Order order = new Order(id, orderDate, newStatus, user, itemQuantities, payment);

        assertFalse(order.isPending());
    }

    @Test
    void test_isApprovedFail() {
        Order order = new Order(id, orderDate, status, user, itemQuantities, payment);

        assertFalse(order.isApproved());
    }

    @Test
    void test_isShippedFail() {
        Order order = new Order(id, orderDate, status, user, itemQuantities, payment);

        assertFalse(order.isShipped());
    }

    @Test
    void test_isPendingOrApprovedFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.REJECTED;
        Order order = new Order(id, orderDate, newStatus, user, itemQuantities, payment);

        assertFalse(order.isPendingOrApproved());
    }
}
