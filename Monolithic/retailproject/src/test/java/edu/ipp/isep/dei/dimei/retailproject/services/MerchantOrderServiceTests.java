package edu.ipp.isep.dei.dimei.retailproject.services;

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
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantOrderServiceTests {
    static final String EXCEPTION_BAD_PAYLOAD = "Wrong merchant order payload.";
    static final String EXCEPTION_NOT_FOUND = "Merchant Order not found.";
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    final Instant currentDateTime = Instant.now();
    @InjectMocks
    MerchantOrderService merchantOrderService;
    @Mock
    MerchantOrderRepository merchantOrderRepository;
    @Mock
    UserService userService;
    @Mock
    OrderService orderService;
    @Mock
    ItemService itemService;
    @Mock
    MerchantService merchantService;
    @Mock
    ShippingOrderService shippingOrderService;
    double price;
    MerchantOrderDTO merchantOrderDTO1;
    MerchantOrderDTO merchantOrderDTO2;
    List<MerchantOrderDTO> merchantOrderDTOS = new ArrayList<>();
    MerchantOrderUpdateDTO merchantOrderUpdateDTO1;
    AddressDTO addressDTO;
    Address address;
    Address merchantAddress;
    Order order1;
    Order order2;
    OrderDTO orderDTO1;
    OrderUpdateDTO orderUpdateDTO1;
    MerchantOrder newMerchantOrder1;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    MerchantOrder merchantOrder1Updated;
    Merchant merchant;
    Account account;
    Account merchantAccount;
    User user;
    User merchantUser;
    Item item;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList = new ArrayList<>();
    List<MerchantOrder> merchantOrders = new ArrayList<>();
    ShippingOrder shippingOrder1;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO1;

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

        address = Address.builder()
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

        itemQuantity2 = ItemQuantity.builder()
                .id(2)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();

        itemQuantityList.add(itemQuantity1);

        order1 = Order.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        itemQuantityList.remove(itemQuantity1);
        itemQuantityList.add(itemQuantity2);

        order2 = Order.builder()
                .id(2)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantityList)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        orderDTO1 = new OrderDTO(order1);
        orderUpdateDTO1 = new OrderUpdateDTO(order1);

        newMerchantOrder1 = MerchantOrder.builder()
                .id(0)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(merchantUser)
                .order(order1)
                .merchant(merchant)
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

        merchantOrder1Updated = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(merchantUser)
                .order(order1)
                .merchant(merchant)
                .build();

        merchantOrders.add(merchantOrder1);
        merchantOrders.add(merchantOrder2);

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantOrderDTO1 = MerchantOrderDTO.builder()
                .id(1)
                .merchantOrderDate(currentDateTime)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(merchantOrder1.getUser().getId())
                .email(merchantOrder1.getUser().getAccount().getEmail())
                .orderId(order1.getId())
                .merchantId(merchantOrder1.getMerchant().getId())
                .build();

        merchantOrderDTO2 = MerchantOrderDTO.builder()
                .id(2)
                .merchantOrderDate(currentDateTime)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(merchantOrder2.getUser().getId())
                .email(merchantOrder2.getUser().getAccount().getEmail())
                .orderId(order2.getId())
                .merchantId(merchantOrder2.getMerchant().getId())
                .build();

        merchantOrderDTOS.add(merchantOrderDTO1);
        merchantOrderDTOS.add(merchantOrderDTO2);

        merchantOrderUpdateDTO1 = MerchantOrderUpdateDTO.builder()
                .id(merchantOrderDTO1.getId())
                .merchantOrderDate(merchantOrderDTO1.getMerchantOrderDate())
                .merchantOrderStatus(merchantOrderDTO1.getMerchantOrderStatus())
                .email(merchantOrderDTO1.getEmail())
                .orderId(merchantOrderDTO1.getOrderId())
                .merchantId(merchantOrderDTO1.getMerchantId())
                .build();

        shippingOrder1 = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddress(address)
                .order(order1)
                .merchantOrder(merchantOrder1)
                .user(user)
                .build();

        shippingOrderUpdateDTO1 = new ShippingOrderUpdateDTO(shippingOrder1);
    }

    @Test
    void test_GetAllMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderRepository.findAll()).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getAllMerchantOrders();
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrders() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(user);
        when(merchantOrderRepository.findByMerchantEmail(user.getAccount().getEmail())).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getUserMerchantOrders(jwtTokenDummy);
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByMerchantEmail(user.getAccount().getEmail());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrderDTO result = merchantOrderService.getUserMerchantOrder(jwtTokenDummy, id);
        MerchantOrderDTO expected = merchantOrderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateMerchantOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(merchantService.getMerchant(id)).thenReturn(new MerchantDTO(merchant));
        when(merchantOrderRepository.save(newMerchantOrder1)).thenReturn(merchantOrder1);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.createMerchantOrder(merchantUser, order1, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchant(id);
        verify(merchantOrderRepository, atLeastOnce()).save(newMerchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrder() throws InvalidQuantityException, NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() - merchantOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        ItemUpdateDTO itemUpdateDTO;
        Item itemAux = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(1)
                .quantityInStock(new StockQuantity(10))
                .category(new Category(1, "Category 1", "Category"))
                .merchant(merchant)
                .build();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.getOrder().setStatus(OrderStatusEnum.CANCELLED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(orderService.fullCancelOrderByOrderId(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.fullCancelShippingOrderByMerchantOrder(jwtTokenDummy, merchantOrder1Updated)).thenReturn(shippingOrderUpdateDTO1);
        itemUpdateDTO = new ItemUpdateDTO(merchantOrder1Updated.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + merchantOrder1Updated.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(jwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(orderService, atLeastOnce()).fullCancelOrderByOrderId(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByMerchantOrder(jwtTokenDummy, merchantOrder1Updated);
        verify(itemService, atLeastOnce()).addItemStock(jwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO);
        verify(merchantOrderRepository, atLeastOnce()).findById(merchantOrder1.getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.fullCancelMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(EXCEPTION_BAD_PAYLOAD, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(jwtTokenDummy, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderByOrder2() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(jwtTokenDummy, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(jwtTokenDummy, order1);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdminFail() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(jwtTokenDummy, order1);
        });

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(EXCEPTION_NOT_FOUND, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByOrderByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getUser().equals(merchantUser))).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(jwtTokenDummy, order1);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByUserFail() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getUser().equals(merchantUser))).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(jwtTokenDummy, order1);
        });

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(EXCEPTION_NOT_FOUND, result.getMessage());
    }


    @Test
    void test_getUserMerchantOrderByIdByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(jwtTokenDummy, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByAdminFail() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(jwtTokenDummy, order1.getId());
        });

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(EXCEPTION_NOT_FOUND, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByIdByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUser().equals(merchantUser))).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(jwtTokenDummy, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByUserFail() throws NotFoundException {
        // Define the behavior of the mock
        merchantUser.getAccount().setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUser().equals(merchantUser))).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(jwtTokenDummy, order1.getId());
        });

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(EXCEPTION_NOT_FOUND, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByByShippingOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = shippingOrder1.getMerchantOrder().getId();
        shippingOrder1.setStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1.getOrder().setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(shippingOrder1.getOrder()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByShippingOrder(jwtTokenDummy, shippingOrder1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrder() throws NotFoundException, WrongFlowException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() - merchantOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        ItemUpdateDTO itemUpdateDTO;
        Item itemAux = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(1)
                .quantityInStock(new StockQuantity(10))
                .category(new Category(1, "Category 1", "Category"))
                .merchant(merchant)
                .build();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);
        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        merchantOrder1Updated.getOrder().setStatus(OrderStatusEnum.REJECTED);
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(orderService.rejectOrderByOrderId(jwtTokenDummy, merchantOrder1Updated.getOrder().getId())).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.rejectShippingOrderByMerchantOrder(jwtTokenDummy, merchantOrder1Updated)).thenReturn(shippingOrderUpdateDTO1);
        itemUpdateDTO = new ItemUpdateDTO(merchantOrder1Updated.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + merchantOrder1Updated.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(jwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        verify(orderService, atLeastOnce()).rejectOrderByOrderId(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrderByMerchantOrder(jwtTokenDummy, merchantOrder1Updated);
        verify(itemService, atLeastOnce()).addItemStock(jwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO);
        verify(merchantOrderRepository, atLeastOnce()).findById(merchantOrder1.getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.rejectMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(EXCEPTION_BAD_PAYLOAD, result.getMessage());
    }

    @Test
    void test_RejectMerchantOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        order1.setStatus(OrderStatusEnum.REJECTED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByOrder(jwtTokenDummy, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrderByShippingOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        shippingOrder1.setStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByShippingOrder(jwtTokenDummy, shippingOrder1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveMerchantOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUser.getAccount().getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.approveMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.approveMerchantOrder(jwtTokenDummy, id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(EXCEPTION_BAD_PAYLOAD, result.getMessage());
    }

    @Test
    void test_ShippedMerchantOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrder1.setStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.SHIPPED);
        order1.setStatus(OrderStatusEnum.APPROVED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.SHIPPED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.shipMerchantOrder(jwtTokenDummy, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeliveredMerchantOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrder1.setStatus(MerchantOrderStatusEnum.SHIPPED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.DELIVERED);
        order1.setStatus(OrderStatusEnum.SHIPPED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.DELIVERED);

        when(userService.getUserByToken(jwtTokenDummy)).thenReturn(merchantUser);
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.deliverMerchantOrder(jwtTokenDummy, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(jwtTokenDummy);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(jwtTokenDummy, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteMerchantOrderByOrderId() {
        // Call the service method that uses the Repository
        merchantOrderService.deleteMerchantOrderByOrderId(order1.getId());

        verify(merchantOrderRepository, times(1)).deleteByOrderId(order1.getId());
    }

}
