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
import edu.ipp.isep.dei.dimei.retailproject.repositories.ShippingOrderRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingOrderServiceTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    ShippingOrderService shippingOrderService;
    @Mock
    ShippingOrderRepository shippingOrderRepository;
    @Mock
    UserService userService;
    @Mock
    MerchantOrderService merchantOrderService;
    @Mock
    OrderService orderService;
    @Mock
    ItemService itemService;
    ShippingOrderDTO shippingOrderDTO1;
    ShippingOrderDTO shippingOrderDTO2;
    List<ShippingOrderDTO> shippingOrderDTOS = new ArrayList<>();
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    AddressDTO addressDTO;
    Address address;
    Address merchantAddress;
    Order order1;
    Order order2;
    ShippingOrder shippingOrder1;
    ShippingOrder shippingOrder2;
    ShippingOrder shippingOrder1Updated;
    Merchant merchant;
    Account account;
    Account merchantAccount;
    User user;
    User merchantUser;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    Item item;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList = new ArrayList<>();
    List<ShippingOrder> shippingOrders = new ArrayList<>();
    Instant currentDateTime = Instant.now();

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

        itemQuantity2 = ItemQuantity.builder()
                .id(2)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .build();

        itemQuantityList.add(itemQuantity1);

        order1 = Order.builder()
                .id(1)
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

        shippingOrder1 = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddress(address)
                .order(order1)
                .merchantOrder(merchantOrder1)
                .user(user)
                .build();

        shippingOrder2 = ShippingOrder.builder()
                .id(2)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddress(address)
                .order(order2)
                .merchantOrder(merchantOrder2)
                .user(user)
                .build();

        shippingOrder1Updated = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddress(address)
                .order(order1)
                .merchantOrder(merchantOrder1)
                .user(user)
                .build();

        shippingOrders.add(shippingOrder1);
        shippingOrders.add(shippingOrder2);

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        shippingOrderDTO1 = ShippingOrderDTO.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .shippingOrderStatus(ShippingOrderStatusEnum.PENDING)
                .addressDTO(addressDTO)
                .orderId(order1.getId())
                .merchantOrderId(merchantOrder1.getId())
                .email(user.getAccount().getEmail())
                .build();

        shippingOrderDTO2 = ShippingOrderDTO.builder()
                .id(2)
                .shippingOrderDate(currentDateTime)
                .shippingOrderStatus(ShippingOrderStatusEnum.PENDING)
                .addressDTO(addressDTO)
                .orderId(order2.getId())
                .merchantOrderId(merchantOrder2.getId())
                .email(user.getAccount().getEmail())
                .build();

        shippingOrderDTOS.add(shippingOrderDTO1);
        shippingOrderDTOS.add(shippingOrderDTO2);

        shippingOrderUpdateDTO = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO1.getId())
                .shippingOrderDate(shippingOrderDTO1.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO1.getShippingOrderStatus())
                .addressDTO(shippingOrderDTO1.getAddressDTO())
                .orderId(shippingOrderDTO1.getOrderId())
                .merchantOrderId(shippingOrderDTO1.getMerchantOrderId())
                .email(user.getAccount().getEmail())
                .build();
    }

    @Test
    void test_GetAllShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderRepository.findAll()).thenReturn(shippingOrders);

        // Call the service method that uses the Repository
        List<ShippingOrderDTO> result = shippingOrderService.getAllShippingOrders();
        List<ShippingOrderDTO> expected = shippingOrderDTOS;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrders() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findByUser(user)).thenReturn(shippingOrders);

        // Call the service method that uses the Repository
        List<ShippingOrderDTO> result = shippingOrderService.getUserShippingOrders(JwtTokenDummy);
        List<ShippingOrderDTO> expected = shippingOrderDTOS;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findByUser(user);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method that uses the Repository
        ShippingOrderDTO result = shippingOrderService.getUserShippingOrder(JwtTokenDummy, id);
        ShippingOrderDTO expected = shippingOrderDTO1;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
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
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);
        order1.setStatus(OrderStatusEnum.CANCELLED);
        when(orderService.fullCancelOrderByOrderId(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderUpdateDTO(order1));
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        when(merchantOrderService.fullCancelMerchantOrderByShippingOrder(JwtTokenDummy, shippingOrder1)).thenReturn(new MerchantOrderUpdateDTO(merchantOrder1));
        itemUpdateDTO = new ItemUpdateDTO(shippingOrder1.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + shippingOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(JwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        verify(orderService, atLeastOnce()).fullCancelOrderByOrderId(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrderByShippingOrder(JwtTokenDummy, shippingOrder1);
        verify(itemService, atLeastOnce()).addItemStock(JwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrderByOrder(JwtTokenDummy, order1);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrderByMerchantOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrderByMerchantOrder(JwtTokenDummy, merchantOrder1);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectShippingOrder() throws NotFoundException, WrongFlowException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
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
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);
        order1.setStatus(OrderStatusEnum.REJECTED);
        when(orderService.rejectOrderByOrderId(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderUpdateDTO(order1));
        merchantOrder1.setStatus(MerchantOrderStatusEnum.REJECTED);
        when(merchantOrderService.rejectMerchantOrderByShippingOrder(JwtTokenDummy, shippingOrder1)).thenReturn(new MerchantOrderUpdateDTO(merchantOrder1));
        itemUpdateDTO = new ItemUpdateDTO(shippingOrder1.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + shippingOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(JwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        verify(orderService, atLeastOnce()).rejectOrderByOrderId(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrderByShippingOrder(JwtTokenDummy, shippingOrder1);
        verify(itemService, atLeastOnce()).addItemStock(JwtTokenDummy, itemUpdateDTO.getId(), itemUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrderByOrder(JwtTokenDummy, order1);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByMerchantOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrderByMerchantOrder(JwtTokenDummy, merchantOrder1);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveShippingOrder() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.APPROVED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.approveShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ShippedShippingOrder() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.APPROVED);
        order1.setStatus(OrderStatusEnum.APPROVED);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.APPROVED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.SHIPPED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.shippedShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeliveredShippingOrder() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.SHIPPED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.DELIVERED);
        order1.setStatus(OrderStatusEnum.SHIPPED);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.SHIPPED);

        when(userService.getUserByToken(JwtTokenDummy)).thenReturn(user);
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.deliveredShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(userService, atLeastOnce()).getUserByToken(JwtTokenDummy);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
