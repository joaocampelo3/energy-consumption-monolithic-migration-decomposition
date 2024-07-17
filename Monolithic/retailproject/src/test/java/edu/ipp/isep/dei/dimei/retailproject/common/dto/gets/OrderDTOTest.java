package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;


import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
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
class OrderDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    int customerId;
    String email;
    List<ItemQuantityDTO> orderItemsDTO = new ArrayList<>();
    double totalPrice;
    PaymentDTO paymentDTO;
    Order order;
    OrderDTO orderDTOExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        customerId = 1;
        email = "johndoe1234@gmail.com";
        double price1 = 12.0;
        double price2 = 5.0;

        ItemQuantityDTO itemQuantityDTO1 = ItemQuantityDTO.builder()
                .id(1)
                .itemId(1)
                .itemName("Item 1")
                .itemSku("ABC-12345-S-BL")
                .itemDescription("Item 1 description")
                .qty(3)
                .price(36.0)
                .build();

        orderItemsDTO.add(itemQuantityDTO1);

        ItemQuantityDTO itemQuantityDTO2 = ItemQuantityDTO.builder()
                .id(2)
                .itemId(2)
                .itemName("Item 2")
                .itemSku("ABC-12345-XS-BL")
                .itemDescription("Item 2 description")
                .qty(5)
                .price(25.0)
                .build();

        orderItemsDTO.add(itemQuantityDTO2);

        totalPrice = 61;
        paymentDTO = PaymentDTO.builder()
                .id(1)
                .amount(price1+price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        Account account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        List<ItemQuantity> orderItems = new ArrayList<>();

        Item item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(price1)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Item 2")
                .sku("ABC-12345-XS-BL")
                .description("Item 2 description")
                .price(price2)
                .build();

        ItemQuantity itemQuantity1 = new ItemQuantity(1, new OrderQuantity(3), item1, price1);
        ItemQuantity itemQuantity2 = new ItemQuantity(2, new OrderQuantity(5), item2, price2);

        orderItems.add(itemQuantity1);
        orderItems.add(itemQuantity2);

        Payment payment = Payment.builder()
                .id(1)
                .amount(price1+price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(orderItems)
                .payment(payment)
                .build();

        orderDTOExpected = new OrderDTO(id, orderDate, orderStatus, customerId, email, orderItemsDTO, totalPrice, paymentDTO);
    }

    @Test
    void test_createOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(id, orderDate, orderStatus, customerId, email, orderItemsDTO, totalPrice, paymentDTO);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(email, orderDTO.getEmail());
        assertEquals(orderItemsDTO, orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());

        assertTrue(orderDTO.getTotalPrice() > 0);
        assertEquals(orderDTO.getTotalPrice(), orderItemsDTO.stream().mapToDouble(ItemQuantityDTO::getPrice).sum());
        assertTrue(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum() > 0);
        assertEquals(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum(), orderItemsDTO.stream().mapToInt(ItemQuantityDTO::getQty).sum());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTOByOrder() {
        OrderDTO orderDTO = new OrderDTO(order);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(email, orderDTO.getEmail());
        assertEquals(orderItemsDTO, orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());

        assertTrue(orderDTO.getTotalPrice() > 0);
        assertEquals(orderDTO.getTotalPrice(), orderItemsDTO.stream().mapToDouble(ItemQuantityDTO::getPrice).sum());
        assertTrue(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum() > 0);
        assertEquals(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum(), orderItemsDTO.stream().mapToInt(ItemQuantityDTO::getQty).sum());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTOByOrder2() {
        order.setItemQuantities(null);
        OrderDTO orderDTO = new OrderDTO(order);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(email, orderDTO.getEmail());
        assertNotNull(orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());
        assertEquals(0, orderDTO.getTotalPrice());
    }

    @Test
    void test_createOrderDTOBuilder() {
        OrderDTO orderDTO = OrderDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .customerId(customerId)
                .email(email)
                .orderItems(orderItemsDTO)
                .totalPrice(totalPrice)
                .paymentDTO(paymentDTO)
                .build();

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(email, orderDTO.getEmail());
        assertEquals(orderItemsDTO, orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());

        assertTrue(orderDTO.getTotalPrice() > 0);
        assertEquals(orderDTO.getTotalPrice(), orderItemsDTO.stream().mapToDouble(ItemQuantityDTO::getPrice).sum());
        assertTrue(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum() > 0);
        assertEquals(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum(), orderItemsDTO.stream().mapToInt(ItemQuantityDTO::getQty).sum());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTONoArgsConstructor() {
        OrderDTO orderDTO = OrderDTO.builder().build();
        assertNotNull(orderDTO);
    }

    @Test
    void test_isRejectedOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isRejected());
    }

    @Test
    void test_isCancelledOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isCancelled());
    }

    @Test
    void test_isShippedOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isShipped());
    }

    @Test
    void test_isDeliveredOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.DELIVERED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isDelivered());
    }

    @Test
    void test_isDeliveredOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isDelivered());
    }

    @Test
    void test_SetsOrderDTO() {
        OrderDTO result = OrderDTO.builder().build();

        result.setId(id);
        result.setOrderDate(orderDate);
        result.setOrderStatus(orderStatus);
        result.setCustomerId(customerId);
        result.setEmail(email);
        result.setOrderItems(orderItemsDTO);
        result.setTotalPrice(totalPrice);
        result.setPaymentDTO(paymentDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(email, result.getEmail());
        assertEquals(orderItemsDTO, result.getOrderItems());
        assertEquals(totalPrice, result.getTotalPrice());
        assertEquals(paymentDTO, result.getPaymentDTO());
        assertEquals(orderDTOExpected.hashCode(), result.hashCode());
        assertEquals(orderDTOExpected, result);
        assertEquals(orderDTOExpected.toString(), result.toString());
    }
}
