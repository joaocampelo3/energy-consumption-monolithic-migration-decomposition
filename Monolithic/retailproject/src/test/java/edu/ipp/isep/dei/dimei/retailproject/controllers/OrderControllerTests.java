package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
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

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
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
    Account account;
    User user;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList = new ArrayList<>();
    OrderQuantity orderQuantity1;
    OrderQuantity orderQuantity2;
    Item item1;
    Item item2;
    Category category;
    Address address;
    Merchant merchant;
    Payment payment;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        double price1 = 12.0;
        double price2 = 5.0;
        category = Category.builder()
                .id(0)
                .name("Category 1")
                .description("Category 1 Description")
                .build();
        address = Address.builder()
                .id(0)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(0)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(address)
                .build();

        item1 = Item.builder()
                .id(0)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Description")
                .price(price1)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();

        item2 = Item.builder()
                .id(0)
                .name("Item 2")
                .sku("ABC-12345-M-BL")
                .description("Item 2 Description")
                .price(price2)
                .quantityInStock(new StockQuantity(5))
                .category(category)
                .merchant(merchant)
                .build();

        orderQuantity1 = new OrderQuantity(2);
        orderQuantity2 = new OrderQuantity(1);

        itemQuantity1 = new ItemQuantity(orderQuantity1, item1, price1);
        itemQuantity2 = new ItemQuantity(orderQuantity2, item2, price2);

        itemQuantityList.add(itemQuantity1);
        itemQuantityList.add(itemQuantity2);

        account = Account.builder()
                .id(0)
                .email("merchant_email@gmail.com")
                .password("merchant_password")
                .role(RoleEnum.MERCHANT)
                .build();

        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
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
                .user(user)
                .itemQuantities(itemQuantityList)
                .payment(payment)
                .build();

        orderDTO = new OrderDTO(order);

        orderCreateDTO = OrderCreateDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus())
                .customerId(order.getUser().getId())
                .email(order.getUser().getAccount().getEmail())
                .orderItems(order.getItemQuantities().stream().map(ItemQuantityDTO::new).toList())
                .totalPrice(order.getItemQuantities().stream().mapToDouble(ItemQuantity::getTotalPrice).sum())
                .payment(new PaymentDTO(payment))
                .merchantId(order.getItemQuantities().get(0).getItem().getMerchant().getId())
                .address(new AddressDTO(address))
                .build();

        orderDTOS.add(orderDTO);

        orderUpdateDTO = new OrderUpdateDTO(order);
        orderDTOExpected = new OrderUpdateDTO(order);
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
    void test_GetUserOrders() throws NotFoundException {
        // Define the behavior of the mock
        when(orderService.getUserOrders(JwtTokenDummy)).thenReturn(orderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.getUserOrders(JwtTokenDummy);
        ResponseEntity<List<OrderDTO>> orderResponseEntityExpected = ResponseEntity.ok(orderDTOS);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrders(JwtTokenDummy);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_GetUserOrdersFail1() throws NotFoundException {
        // Define the behavior of the mock
        when(orderService.getUserOrders(JwtTokenDummy)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.getUserOrders(JwtTokenDummy);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrders(JwtTokenDummy);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_GetOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderService.getUserOrder(JwtTokenDummy, id)).thenReturn(orderDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.getUserOrderById(JwtTokenDummy, id);
        ResponseEntity<OrderDTO> orderResponseEntityExpected = ResponseEntity.ok(orderDTO);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, id);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_GetOrderByIdFail1() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderService.getUserOrder(JwtTokenDummy, id)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.getUserOrderById(JwtTokenDummy, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateOrder() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        when(orderService.createOrder(JwtTokenDummy, orderCreateDTO)).thenReturn(orderDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.createOrder(JwtTokenDummy, orderCreateDTO);
        ResponseEntity<OrderDTO> orderResponseEntityExpected = new ResponseEntity<>(orderDTO, HttpStatus.CREATED);

        // Perform assertions
        verify(orderService, atMostOnce()).createOrder(JwtTokenDummy, orderCreateDTO);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_CreateOrderFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(orderService.createOrder(JwtTokenDummy, orderCreateDTO)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.createOrder(JwtTokenDummy, orderCreateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).createOrder(JwtTokenDummy, orderCreateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateOrderFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(orderService.createOrder(JwtTokenDummy, orderCreateDTO)).thenThrow(new BadPayloadException(exceptionOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.createOrder(JwtTokenDummy, orderCreateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).createOrder(JwtTokenDummy, orderCreateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateOrderFail3() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(orderService.createOrder(JwtTokenDummy, orderCreateDTO)).thenThrow(new BadPayloadException(exceptionOrderInvalidQuantity));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.createOrder(JwtTokenDummy, orderCreateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderInvalidQuantity, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).createOrder(JwtTokenDummy, orderCreateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeleteOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        int userId = 1;
        when(orderService.deleteOrder(userId, id)).thenReturn(orderDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.deleteOrder(userId, id);
        ResponseEntity<OrderDTO> orderResponseEntityExpected = ResponseEntity.ok(orderDTO);

        // Perform assertions
        verify(orderService, atMostOnce()).deleteOrder(userId, id);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_test_DeleteOrderFail1() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        int userId = 1;
        when(orderService.deleteOrder(userId, id)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.deleteOrder(userId, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).deleteOrder(userId, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderService.fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO)).thenReturn(orderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.fullCancelOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<OrderUpdateDTO> orderResponseEntityExpected = new ResponseEntity<>(orderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(orderService, atMostOnce()).fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_FullCancelOrderByIdFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderService.fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.fullCancelOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelOrderByIdFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderService.fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new BadPayloadException(exceptionOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.fullCancelOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelOrderByIdFail3() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderService.fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new InvalidQuantityException(exceptionOrderInvalidQuantity));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.fullCancelOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderInvalidQuantity, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelOrderByIdFail4() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderService.fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new WrongFlowException(exceptionOrderWrongFlow));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.fullCancelOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderWrongFlow, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).fullCancelOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);

        when(orderService.rejectOrder(JwtTokenDummy, id, orderUpdateDTO)).thenReturn(orderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.rejectOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<OrderUpdateDTO> orderResponseEntityExpected = new ResponseEntity<>(orderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(orderService, atLeastOnce()).rejectOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_RejectOrderByIdFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);

        when(orderService.rejectOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.rejectOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).rejectOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectOrderByIdFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);

        when(orderService.rejectOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new BadPayloadException(exceptionOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.rejectOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).rejectOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectOrderByIdFail3() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);

        when(orderService.rejectOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new InvalidQuantityException(exceptionOrderInvalidQuantity));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.rejectOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderInvalidQuantity, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).rejectOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectOrderByIdFail4() throws InvalidQuantityException, BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);

        when(orderService.rejectOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new WrongFlowException(exceptionOrderWrongFlow));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.rejectOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderWrongFlow, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).rejectOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }


    @Test
    void test_ApproveOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);

        when(orderService.approveOrder(JwtTokenDummy, id, orderUpdateDTO)).thenReturn(orderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> orderResponseEntity = orderController.approveOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<OrderUpdateDTO> orderResponseEntityExpected = new ResponseEntity<>(orderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(orderService, atLeastOnce()).approveOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(orderResponseEntity);
        assertEquals(orderResponseEntityExpected, orderResponseEntity);
    }

    @Test
    void test_ApproveOrderByIdFail1() throws BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);

        when(orderService.approveOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new NotFoundException(exceptionOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.approveOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(orderService, atLeastOnce()).approveOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ApproveOrderByIdFail2() throws BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);

        when(orderService.approveOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new BadPayloadException(exceptionOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.approveOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).approveOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ApproveOrderByIdFail3() throws BadPayloadException, NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);

        when(orderService.approveOrder(JwtTokenDummy, id, orderUpdateDTO)).thenThrow(new WrongFlowException(exceptionOrderWrongFlow));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = orderController.approveOrderById(JwtTokenDummy, id, orderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionOrderWrongFlow, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(orderService, atLeastOnce()).approveOrder(JwtTokenDummy, id, orderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }
}
