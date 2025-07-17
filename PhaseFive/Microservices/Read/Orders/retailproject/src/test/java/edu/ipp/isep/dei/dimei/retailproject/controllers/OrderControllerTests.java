package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {
    final String exceptionOrderNotFound = "Order not found.";
    final String exceptionOrderBadRequest = "Wrong order payload.";
    final String exceptionOrderInvalidQuantity = "The number of quantity inserted is not valid";
    final String exceptionOrderWrongFlow = "It is not possible to change Order status";
    @InjectMocks
    OrderController orderController;
    @Mock
    OrderService orderService;
    Order order;
    OrderDTO orderDTO;
    OrderCreateDTO orderCreateDTO;
    OrderUpdateDTO orderUpdateDTO;
    OrderUpdateDTO orderDTOExpected;
    List<OrderDTO> orderDTOS = new ArrayList<>();
    UserDTO userDTO;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList = new ArrayList<>();
    OrderQuantity orderQuantity1;
    OrderQuantity orderQuantity2;
    Item item1;
    Item item2;
    AddressDTO addressDTO;
    int categoryId;
    int merchantId;
    Payment payment;
    boolean isEvent = false;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        double price1 = 12.0;
        double price2 = 5.0;
        addressDTO = AddressDTO.builder()
                .id(0)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        categoryId = 1;
        merchantId = 1;

        item1 = Item.builder()
                .id(0)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Description")
                .price(price1)
                .quantityInStock(new StockQuantity(10))
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        item2 = Item.builder()
                .id(0)
                .name("Item 2")
                .sku("ABC-12345-M-BL")
                .description("Item 2 Description")
                .price(price2)
                .quantityInStock(new StockQuantity(5))
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        orderQuantity1 = new OrderQuantity(2);
        orderQuantity2 = new OrderQuantity(1);

        itemQuantity1 = new ItemQuantity(orderQuantity1, item1, price1);
        itemQuantity2 = new ItemQuantity(orderQuantity2, item2, price2);

        itemQuantityList.add(itemQuantity1);
        itemQuantityList.add(itemQuantity2);

        userDTO = UserDTO.builder()
                .userId(1)
                .email("merchant_email@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        payment = Payment.builder()
                .id(0)
                .amount(itemQuantity1.getTotalPrice() + itemQuantity2.getTotalPrice())
                .paymentDateTime(Instant.now())
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();

        order = Order.builder()
                .id(0)
                .orderDate(Instant.now())
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList)
                .payment(payment)
                .build();

        orderDTO = new OrderDTO(order);

        orderCreateDTO = OrderCreateDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus())
                .customerId(order.getUserId())
                .email(userDTO.getEmail())
                .orderItems(order.getItemQuantities().stream().map(ItemQuantityDTO::new).toList())
                .totalPrice(order.getItemQuantities().stream().mapToDouble(ItemQuantity::getTotalPrice).sum())
                .payment(new PaymentDTO(payment))
                .merchantId(merchantId)
                .address(addressDTO)
                .build();

        orderDTOS.add(orderDTO);

        orderUpdateDTO = new OrderUpdateDTO(order, userDTO.getEmail());
        orderDTOExpected = new OrderUpdateDTO(order, userDTO.getEmail());
    }


    @Test
    void test_GetAllOrders() {
        // Define the behavior of the mock
        when(orderService.getAllOrders()).thenReturn(orderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<List<OrderDTO>> orderResponseEntity = orderController.getAllOrders();
        ResponseEntity<List<OrderDTO>> orderResponseEntityExpected = ResponseEntity.ok(orderDTOS);

        // Perform assertions
        verify(orderService, atLeastOnce()).getAllOrders();
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_GetUserOrders() {
        // Define the behavior of the mock
        when(orderService.getUserOrders(userDTO)).thenReturn(orderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.getUserOrders(userDTO);
        ResponseEntity<List<OrderDTO>> orderResponseEntityExpected = ResponseEntity.ok(orderDTOS);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrders(userDTO);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_GetOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderService.getUserOrder(userDTO, id)).thenReturn(orderDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.getUserOrderById(userDTO, id);
        ResponseEntity<OrderDTO> orderResponseEntityExpected = ResponseEntity.ok(orderDTO);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, id);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_GetOrderByIdFail1() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderService.getUserOrder(userDTO, id)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.getUserOrderById(userDTO, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }
}
