package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.StockQuantity;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
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
    OrderDTO orderDTO1;
    OrderDTO orderDTO2;
    List<OrderDTO> orderDTOS = new ArrayList<>();
    OrderUpdateDTO orderUpdateDTO;
    AddressDTO shippingAddressDTO;
    Address shippingAddress;
    Address merchantAddress;
    Order order1;
    Order order2;
    Merchant merchant;
    Account account;
    Account merchantAccount;
    User user;
    User merchantUser;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    Item item;
    Item itemUpdated;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList1 = new ArrayList<>();
    List<ItemQuantity> itemQuantityList2 = new ArrayList<>();
    ItemQuantityDTO itemQuantityDTO1;
    ItemUpdateDTO itemUpdateDTO1;
    ItemDTO itemDTO1;
    ItemDTO itemDTO1Updated;
    List<Order> orders = new ArrayList<>();
    LocalDateTime currentDateTime = LocalDateTime.now();
    OrderCreateDTO orderCreateDTO;
    Payment payment;
    PaymentDTO paymentDTO;
    LocalDateTime currentTime = LocalDateTime.now();
    UUID orderUuid = UUID.randomUUID();
    Order order1Updated;
    MerchantOrderDTO merchantOrderDTO1;
    ShippingOrder shippingOrder1;
    ShippingOrderDTO shippingOrderDTO1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
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
                .email("merchant_email@gmail.com")
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
                .price(1)
                .quantityInStock(new StockQuantity(10))
                .category(new Category(1, "Category 1", "Category"))
                .merchant(merchant)
                .build();

        itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .build();

        itemQuantityDTO1 = new ItemQuantityDTO(itemQuantity1);

        itemQuantity2 = ItemQuantity.builder()
                .id(2)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .build();

        itemUpdateDTO1 = new ItemUpdateDTO(item.getId(), item.getSku(), item.getPrice(), item.getQuantityInStock().getQuantity());

        itemDTO1 = new ItemDTO(item);

        itemDTO1Updated = new ItemDTO(item);
        itemDTO1Updated.setQuantityInStock(itemDTO1Updated.getQuantityInStock() - 1);

        itemQuantityList1.add(itemQuantity1);

        order1 = Order.builder()
                .id(1)
                .uuid(orderUuid)
                .orderDate(currentTime)
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
                .uuid(UUID.randomUUID())
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
                .paymentDateTime(currentTime)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();

        paymentDTO = new PaymentDTO(payment);

        orderCreateDTO = OrderCreateDTO.builder()
                .orderDate(currentTime)
                .customerId(user.getId())
                .email(user.getAccount().getEmail())
                .orderItems(order1.getItemQuantities().stream().map(itemQuantity -> new ItemQuantityDTO(itemQuantity)).toList())
                .totalPrice(1)
                .payment(paymentDTO)
                .merchantId(merchant.getId())
                .address(shippingAddressDTO)
                .build();

        order1Updated = Order.builder()
                .id(1)
                .uuid(orderUuid)
                .orderDate(currentTime)
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
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // Call the service method that uses the Repository
        List<OrderDTO> result = orderService.getUserOrders(JwtTokenDummy);
        List<OrderDTO> expected = orderDTOS;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findByUser(user);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateOrder() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        itemUpdateDTO1.setQuantityInStock(itemUpdateDTO1.getQuantityInStock() - 1);
        itemUpdated.getQuantityInStock().setQuantity((itemUpdated.getQuantityInStock().getQuantity() - 1));
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(itemService.getUserItemDTO(JwtTokenDummy, itemQuantityDTO1.getId())).thenReturn(itemDTO1);
        when(addressService.createAddress(orderCreateDTO.getAddress(), user)).thenReturn(shippingAddress);
        when(paymentService.createPayment(orderCreateDTO.getPayment())).thenReturn(payment);
        when(itemQuantityService.createItemQuantity(itemQuantityDTO1)).thenReturn(itemQuantity1);
        when(itemService.removeItemStock(JwtTokenDummy, itemQuantity1.getItem().getId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty()))).thenReturn(itemDTO1Updated);
        when(itemService.getItemBySku(itemQuantityDTO1.getItemSku())).thenReturn(item);
        doReturn(Optional.ofNullable(order1)).when(orderRepository).findByUuid(any(UUID.class));
        when(merchantOrderService.createMerchantOrder(user, order1, orderCreateDTO.getMerchantId())).thenReturn(merchantOrder1);

        // Call the service method that uses the Repository
        Order result = orderService.createOrder(JwtTokenDummy, orderCreateDTO);
        Order expected = order1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(itemService, atLeastOnce()).getUserItemDTO(JwtTokenDummy, itemQuantityDTO1.getId());
        verify(addressService, atLeastOnce()).createAddress(orderCreateDTO.getAddress(), user);
        verify(paymentService, atLeastOnce()).createPayment(orderCreateDTO.getPayment());
        verify(itemQuantityService, atLeastOnce()).createItemQuantity(itemQuantityDTO1);
        verify(itemService, atLeastOnce()).removeItemStock(JwtTokenDummy, itemQuantity1.getItem().getId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty()));
        verify(itemService, atLeastOnce()).getItemBySku(itemQuantityDTO1.getItemSku());
        verify(orderRepository, atLeastOnce()).findByUuid(any(UUID.class));
        verify(merchantOrderService, atLeastOnce()).createMerchantOrder(user, order1, orderCreateDTO.getMerchantId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(JwtTokenDummy, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
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
        MerchantOrderUpdateDTO merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(merchantOrder1);
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        ShippingOrderUpdateDTO shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(shippingOrder1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);
        when(merchantOrderService.fullCancelMerchantOrderByOrder(JwtTokenDummy, order1Updated)).thenReturn(merchantOrderUpdateDTO);
        doReturn(shippingOrderUpdateDTO).when(shippingOrderService).fullCancelShippingOrderByOrder(JwtTokenDummy, order1Updated);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenAnswer(new Answer() {
            private int count = 0;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(order1);
                }
                return Optional.ofNullable(order1Updated);
            }
        });

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrder(JwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrderByOrder(JwtTokenDummy, order1Updated);
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByOrder(JwtTokenDummy, order1Updated);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrderByOrderId(JwtTokenDummy, orderUpdateDTO.getId());
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrder(JwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrderByOrderId(JwtTokenDummy, orderUpdateDTO.getId());
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.approveOrder(JwtTokenDummy, orderUpdateDTO.getId(), orderUpdateDTO);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ShipOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.APPROVED);
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.shipOrder(JwtTokenDummy, orderUpdateDTO.getId()));

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
    }

    @Test
    void test_DeliverOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.SHIPPED);
        order1Updated.setStatus(OrderStatusEnum.SHIPPED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.SHIPPED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUser().getId() == user.getId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.deliverOrder(JwtTokenDummy, orderUpdateDTO.getId()));

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, order1.getId());
    }

}
