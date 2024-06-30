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
class MerchantOrderTest {
    int id;
    Instant orderDate;
    final MerchantOrderStatusEnum status = MerchantOrderStatusEnum.PENDING;
    User user;
    Order order;
    Merchant merchant;
    MerchantOrder merchantOrderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
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

    @Test
    void test_MerchantOrderSets() {
        MerchantOrder merchantOrder = MerchantOrder.builder().build();

        merchantOrder.setId(id);
        merchantOrder.setOrderDate(orderDate);
        merchantOrder.setStatus(status);
        merchantOrder.setUser(user);
        merchantOrder.setOrder(order);
        merchantOrder.setMerchant(merchant);

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
    void test_statusMerchantOrderPending() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
        assertTrue(merchantOrder.isPending());
    }

    @Test
    void test_statusMerchantOrderPendingFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.APPROVED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isPending());
    }

    @Test
    void test_statusMerchantOrderApproved() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.APPROVED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertTrue(merchantOrder.isApproved());
    }

    @Test
    void test_statusMerchantOrderApprovedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isApproved());
    }

    @Test
    void test_statusMerchantOrderRejected() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.REJECTED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.REJECTED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertTrue(merchantOrder.isRejected());
    }

    @Test
    void test_statusMerchantOrderRejectedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isRejected());
    }

    @Test
    void test_statusMerchantOrderCancelled() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.CANCELLED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.CANCELLED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertTrue(merchantOrder.isCancelled());
    }

    @Test
    void test_statusMerchantOrderCancelledFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isCancelled());
    }

    @Test
    void test_statusMerchantOrderShipped() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.SHIPPED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.SHIPPED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertTrue(merchantOrder.isShipped());
    }

    @Test
    void test_statusMerchantOrderShippedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isShipped());
    }

    @Test
    void test_statusMerchantOrderDelivered() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.DELIVERED)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.DELIVERED, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertTrue(merchantOrder.isDelivered());
    }

    @Test
    void test_statusMerchantOrderDeliveredFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(user, merchantOrder.getUser());
        assertEquals(order, merchantOrder.getOrder());
        assertEquals(merchant, merchantOrder.getMerchant());
        assertFalse(merchantOrder.isDelivered());
    }
}
