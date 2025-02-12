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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantOrderServiceTests {
    static final String exceptionBadPayload = "Wrong merchant order payload.";
    static final String exceptionNotFound = "Merchant Order not found.";
    final Instant currentDateTime = Instant.now();
    @InjectMocks
    MerchantOrderService merchantOrderService;
    @Mock
    MerchantOrderRepository merchantOrderRepository;
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
    AddressDTO merchantAddressDTO;
    Order order1;
    Order order2;
    OrderDTO orderDTO1;
    OrderUpdateDTO orderUpdateDTO1;
    MerchantOrder newMerchantOrder1;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    MerchantOrder merchantOrder1Updated;
    Merchant merchant;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
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

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        merchantUserDTO = UserDTO.builder()
                .userId(2)
                .email("merchant_email@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantAddressDTO = AddressDTO.builder()
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
                .addressId(merchantAddressDTO.getId())
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
                .userId(userDTO.getUserId())
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
                .userId(userDTO.getUserId())
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
        orderUpdateDTO1 = new OrderUpdateDTO(order1, userDTO.getEmail());

        newMerchantOrder1 = MerchantOrder.builder()
                .id(0)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(merchantUserDTO.getUserId())
                .order(order1)
                .merchant(merchant)
                .build();

        merchantOrder1 = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(merchantUserDTO.getUserId())
                .order(order1)
                .merchant(merchant)
                .build();

        merchantOrder2 = MerchantOrder.builder()
                .id(2)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(merchantUserDTO.getUserId())
                .order(order2)
                .merchant(merchant)
                .build();

        merchantOrder1Updated = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(merchantUserDTO.getUserId())
                .order(order1)
                .merchant(merchant)
                .build();

        merchantOrders.add(merchantOrder1);
        merchantOrders.add(merchantOrder2);

        merchantOrderDTO1 = MerchantOrderDTO.builder()
                .id(1)
                .merchantOrderDate(currentDateTime)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(merchantOrder1.getUserId())
                .email(merchantUserDTO.getEmail())
                .orderId(order1.getId())
                .merchantId(merchantOrder1.getMerchant().getId())
                .build();

        merchantOrderDTO2 = MerchantOrderDTO.builder()
                .id(2)
                .merchantOrderDate(currentDateTime)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(merchantOrder2.getUserId())
                .email(merchantUserDTO.getEmail())
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
                .shippingAddressId(addressDTO.getId())
                .order(order1)
                .merchantOrder(merchantOrder1)
                .userId(userDTO.getUserId())
                .build();

        shippingOrderUpdateDTO1 = new ShippingOrderUpdateDTO(shippingOrder1);
    }

    @Test
    void test_GetAllMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderRepository.findAll()).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getAllMerchantOrders(merchantUserDTO);
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderRepository.findByMerchantEmail(merchantUserDTO.getEmail())).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getUserMerchantOrders(merchantUserDTO);
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByMerchantEmail(merchantUserDTO.getEmail());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrderDTO result = merchantOrderService.getUserMerchantOrder(merchantUserDTO, id);
        MerchantOrderDTO expected = merchantOrderDTO1;

        // Perform assertions
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
        MerchantOrder result = merchantOrderService.createMerchantOrder(merchantUserDTO, order1, id);
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
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantUserDTO.getEmail()));
        when(orderService.fullCancelOrderByOrderId(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.fullCancelShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated)).thenReturn(shippingOrderUpdateDTO1);
        itemUpdateDTO = new ItemUpdateDTO(merchantOrder1Updated.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + merchantOrder1Updated.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
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
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO1);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(orderService, atLeastOnce()).fullCancelOrderByOrderId(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated);
        verify(itemService, atLeastOnce()).addItemStock(itemUpdateDTO.getId(), itemUpdateDTO);
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
            merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantUserDTO.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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

        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdminFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1);
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByOrderByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByUserFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(order1).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1);
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }


    @Test
    void test_getUserMerchantOrderByIdByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByAdminFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId());
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByIdByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByUserFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId());
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByByShippingOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = shippingOrder1.getMerchantOrder().getId();
        shippingOrder1.setStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1.getOrder().setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrder(shippingOrder1.getOrder()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, userDTO.getEmail()));
        merchantOrder1Updated.getOrder().setStatus(OrderStatusEnum.REJECTED);
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(orderService.rejectOrderByOrderId(merchantUserDTO, merchantOrder1Updated.getOrder().getId())).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.rejectShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated)).thenReturn(shippingOrderUpdateDTO1);
        itemUpdateDTO = new ItemUpdateDTO(merchantOrder1Updated.getOrder().getItemQuantities().get(0).getItem());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + merchantOrder1Updated.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
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
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO1);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        verify(orderService, atLeastOnce()).rejectOrderByOrderId(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated);
        verify(itemService, atLeastOnce()).addItemStock(itemUpdateDTO.getId(), itemUpdateDTO);
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
            merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
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

        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
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
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByOrder(merchantUserDTO, order1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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

        when(merchantOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
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
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchant().getEmail().compareTo(merchantUserDTO.getEmail()) == 0)).thenAnswer(new Answer<Optional<MerchantOrder>>() {
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
        MerchantOrderUpdateDTO result = merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO1);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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
            merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO1);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
    }

    @Test
    void test_ShippedMerchantOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrder1.setStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.SHIPPED);
        order1.setStatus(OrderStatusEnum.APPROVED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.SHIPPED);

        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.shipMerchantOrder(merchantUserDTO, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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

        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.deliverMerchantOrder(merchantUserDTO, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrder().getId());
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
