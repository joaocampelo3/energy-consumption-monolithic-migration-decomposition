package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static edu.ipp.isep.dei.dimei.retailproject.services.OrderService.BADPAYLOADEXCEPTIONMESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserService userService;
    @Mock
    MerchantOrderService merchantOrderService;
    @Mock
    ItemService itemService;
    @Mock
    ShippingOrderService shippingOrderService;
    @Mock
    AddressService addressService;
    @Mock
    PaymentService paymentService;
    @Mock
    ItemQuantityService itemQuantityService;
    double price;
    OrderDTO orderDTO1;
    OrderDTO orderDTO2;
    List<OrderDTO> orderDTOS = new ArrayList<>();
    OrderUpdateDTO orderUpdateDTO;
    AddressDTO shippingAddressDTO;
    Address shippingAddress;
    Address merchantAddress;
    Order order1;
    Order order2;
    Order newOrder1;
    Merchant merchant;
    Merchant merchant2;
    Account account;
    Account merchantAccount;
    Account adminAccount;
    User user;
    User merchantUser;
    User adminUser;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    Item item;
    Item itemUpdated;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList1 = new ArrayList<>();
    List<ItemQuantity> itemQuantityList2 = new ArrayList<>();
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantityDTO itemQuantityDTO2;
    ItemUpdateDTO itemUpdateDTO1;
    ItemDTO itemDTO1;
    ItemDTO itemDTO2;
    ItemDTO itemDTO1Updated;
    List<Order> orders = new ArrayList<>();
    Instant currentDateTime = Instant.now();
    OrderCreateDTO orderCreateDTO;
    Payment payment;
    PaymentDTO paymentDTO;
    Order order1Updated;
    MerchantOrderDTO merchantOrderDTO1;
    ShippingOrder shippingOrder1;
    ShippingOrderDTO shippingOrderDTO1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        price = 12.0;
        account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        merchantAccount = Account.builder()
                .id(2)
                .email("merchant_email@gmail.com")
                .password("merchant_password")
                .role(RoleEnum.MERCHANT)
                .build();

        adminAccount = Account.builder()
                .id(3)
                .email("admin_email@gmail.com")
                .password("admin_password")
                .role(RoleEnum.ADMIN)
                .build();

        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        merchantUser = User.builder()
                .id(2)
                .firstname("John")
                .lastname("Doe")
                .account(merchantAccount)
                .build();

        adminUser = User.builder()
                .id(3)
                .firstname("John")
                .lastname("Doe")
                .account(adminAccount)
                .build();

        shippingAddress = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantAddress = Address.builder()
                .id(2)
                .street("Different Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant1_email@gmail.com")
                .address(merchantAddress)
                .build();

        item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(1)
                .quantityInStock(new StockQuantity(10))
                .category(new Category(1, "Category 1", "Category"))
                .merchant(merchant)
                .build();

        itemUpdated = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .category(new Category(1, "Category 1", "Category"))
                .merchant(merchant)
                .build();

        itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();

        itemQuantityDTO1 = new ItemQuantityDTO(itemQuantity1);

        itemQuantity2 = ItemQuantity.builder()
                .id(2)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();

        itemQuantityDTO2 = new ItemQuantityDTO(itemQuantity2);

        itemUpdateDTO1 = new ItemUpdateDTO(item.getId(), item.getSku(), item.getPrice(), item.getQuantityInStock().getQuantity());

        itemDTO1 = new ItemDTO(item);
        itemDTO2 = new ItemDTO(item);

        itemDTO1Updated = new ItemDTO(item);
        itemDTO1Updated.setQuantityInStock(itemDTO1Updated.getQuantityInStock() - 1);

        itemQuantityList1.add(itemQuantity1);

        newOrder1 = Order.builder()
                .id(0)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        order1 = Order.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        itemQuantityList2.add(itemQuantity2);

        order2 = Order.builder()
                .id(2)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList2)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        merchantOrder1 = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(merchantUser)
                .order(order1)
                .merchant(merchant)
                .build();

        merchantOrder2 = MerchantOrder.builder()
                .id(2)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(merchantUser)
                .order(order2)
                .merchant(merchant)
                .build();

        orders.add(order1);
        orders.add(order2);

        orderDTO1 = new OrderDTO(order1);
        orderDTO2 = new OrderDTO(order2);
        orderDTOS.add(orderDTO1);
        orderDTOS.add(orderDTO2);

        shippingAddressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        payment = Payment.builder()
                .id(1)
                .amount(1)
                .paymentDateTime(currentDateTime)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();

        paymentDTO = new PaymentDTO(payment);

        orderCreateDTO = OrderCreateDTO.builder()
                .orderDate(currentDateTime)
                .customerId(user.getId())
                .email(user.getAccount().getEmail())
                .orderItems(order1.getItemQuantities().stream().map(ItemQuantityDTO::new).toList())
                .totalPrice(1)
                .payment(paymentDTO)
                .merchantId(merchant.getId())
                .address(shippingAddressDTO)
                .build();

        order1Updated = Order.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        orderUpdateDTO = new OrderUpdateDTO(order1);
        merchantOrderDTO1 = new MerchantOrderDTO(merchantOrder1);
        shippingOrder1 = new ShippingOrder(user, order1, merchantOrder1, shippingAddress);
        shippingOrderDTO1 = new ShippingOrderDTO(shippingOrder1);
    }

    @Test
    void test_GetAllOrders() {
        // Define the behavior of the mock
        when(orderRepository.findAll()).thenReturn(orders);

        // Call the service method that uses the Repository
        List<OrderDTO> result = orderService.getAllOrders();
        List<OrderDTO> expected = orderDTOS;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrders() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // Call the service method that uses the Repository
        List<OrderDTO> result = orderService.getUserOrders(jwtTokenDummy);
        List<OrderDTO> expected = orderDTOS;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findByUser(user);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateOrder() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        itemUpdateDTO1.setQuantityInStock(itemUpdateDTO1.getQuantityInStock() - 1);
        itemUpdated.getQuantityInStock().setQuantity((itemUpdated.getQuantityInStock().getQuantity() - 1));
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(itemService.getItemDTO(itemQuantityDTO1.getId())).thenReturn(itemDTO1);
        when(addressService.createAddress(orderCreateDTO.getAddress(), user)).thenReturn(shippingAddress);
        when(paymentService.createPayment(orderCreateDTO.getPayment())).thenReturn(payment);
        when(itemQuantityService.createItemQuantity(itemQuantityDTO1)).thenReturn(itemQuantity1);
        when(itemService.removeItemStock(jwtTokenDummy, itemQuantityDTO1.getItemId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty()))).thenReturn(itemDTO1Updated);
        when(orderRepository.save(newOrder1)).thenReturn(order1);
        when(merchantOrderService.createMerchantOrder(user, order1, orderCreateDTO.getMerchantId())).thenReturn(merchantOrder1);

        // Call the service method that uses the Repository
        OrderDTO result = orderService.createOrder(jwtTokenDummy, orderCreateDTO);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(itemService, atLeastOnce()).getItemDTO(itemQuantityDTO1.getId());
        verify(addressService, atLeastOnce()).createAddress(orderCreateDTO.getAddress(), user);
        verify(paymentService, atLeastOnce()).createPayment(orderCreateDTO.getPayment());
        verify(itemQuantityService, atLeastOnce()).createItemQuantity(itemQuantityDTO1);
        verify(itemService, atLeastOnce()).removeItemStock(jwtTokenDummy, itemQuantity1.getItem().getId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty()));
        verify(orderRepository, atLeastOnce()).save(newOrder1);
        verify(merchantOrderService, atLeastOnce()).createMerchantOrder(user, order1, orderCreateDTO.getMerchantId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(jwtTokenDummy, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(adminUser);
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(jwtTokenDummy, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrderByMerchant() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(jwtTokenDummy, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteOrder() throws NotFoundException {
        // Define the behavior of the mock
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.deleteOrder(user.getId(), order1.getId());
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        MerchantOrderUpdateDTO merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(merchantOrder1);
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        ShippingOrderUpdateDTO shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(shippingOrder1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(merchantOrderService.fullCancelMerchantOrderByOrder(jwtTokenDummy, order1Updated)).thenReturn(merchantOrderUpdateDTO);
        doReturn(shippingOrderUpdateDTO).when(shippingOrderService).fullCancelShippingOrderByOrder(jwtTokenDummy, order1Updated);
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrderByOrder(jwtTokenDummy, order1Updated);
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByOrder(jwtTokenDummy, order1Updated);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailCancelledOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderIDAndCancelledOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrderByOrderId(jwtTokenDummy, orderUpdateDTO.getId());
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrderByOrderId(jwtTokenDummy, orderUpdateDTO.getId());
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailRejectedOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailDifferentOrderIDAndRejectedOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.approveOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.approveOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrderFailApprovedOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.approveOrder(jwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrderFailDifferentOrderIDAndApprovedOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(jwtTokenDummy, id, orderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ShipOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.APPROVED);
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.shipOrder(jwtTokenDummy, orderUpdateDTO.getId()));

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
    }

    @Test
    void test_DeliverOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.SHIPPED);
        order1Updated.setStatus(OrderStatusEnum.SHIPPED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.SHIPPED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(jwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.deliverOrder(jwtTokenDummy, orderUpdateDTO.getId()));

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(jwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, order1.getId());
    }

}
