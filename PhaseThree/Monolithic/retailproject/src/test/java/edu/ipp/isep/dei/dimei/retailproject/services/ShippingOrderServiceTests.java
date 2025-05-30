package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
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

import static edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService.BADPAYLOADEXCEPTIONMESSAGE;
import static edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService.NOTFOUNDEXCEPTIONMESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingOrderServiceTests {
    final Instant currentDateTime = Instant.now();
    @InjectMocks
    ShippingOrderService shippingOrderService;
    @Mock
    ShippingOrderRepository shippingOrderRepository;
    @Mock
    MerchantOrderService merchantOrderService;
    @Mock
    OrderService orderService;
    @Mock
    ItemService itemService;

    double price;
    ShippingOrderDTO shippingOrderDTO1;
    ShippingOrderDTO shippingOrderDTO2;
    List<ShippingOrderDTO> shippingOrderDTOS = new ArrayList<>();
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    AddressDTO addressDTO;
    AddressDTO merchantAddressDTO;
    Order order1;
    Order order2;
    ShippingOrder shippingOrder1;
    ShippingOrder shippingOrder2;
    ShippingOrder shippingOrder1Updated;
    Merchant merchant;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    Item item;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList = new ArrayList<>();
    List<ShippingOrder> shippingOrders = new ArrayList<>();

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

        shippingOrder1 = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(addressDTO.getId())
                .order(order1)
                .merchantOrder(merchantOrder1)
                .userId(userDTO.getUserId())
                .build();

        shippingOrder2 = ShippingOrder.builder()
                .id(2)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(addressDTO.getId())
                .order(order2)
                .merchantOrder(merchantOrder2)
                .userId(userDTO.getUserId())
                .build();

        shippingOrder1Updated = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(addressDTO.getId())
                .order(order1)
                .merchantOrder(merchantOrder1)
                .userId(userDTO.getUserId())
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
                .addressId(addressDTO.getId())
                .orderId(order1.getId())
                .merchantOrderId(merchantOrder1.getId())
                .email(userDTO.getEmail())
                .build();

        shippingOrderDTO2 = ShippingOrderDTO.builder()
                .id(2)
                .shippingOrderDate(currentDateTime)
                .shippingOrderStatus(ShippingOrderStatusEnum.PENDING)
                .addressId(addressDTO.getId())
                .orderId(order2.getId())
                .merchantOrderId(merchantOrder2.getId())
                .email(userDTO.getEmail())
                .build();

        shippingOrderDTOS.add(shippingOrderDTO1);
        shippingOrderDTOS.add(shippingOrderDTO2);

        shippingOrderUpdateDTO = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO1.getId())
                .shippingOrderDate(shippingOrderDTO1.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO1.getShippingOrderStatus())
                .addressId(shippingOrderDTO1.getAddressId())
                .orderId(shippingOrderDTO1.getOrderId())
                .merchantOrderId(shippingOrderDTO1.getMerchantOrderId())
                .userId(userDTO.getUserId())
                .userDTO(userDTO)
                .build();
    }

    @Test
    void test_createShippingOrder() {
        // Call the service method that uses the Repository
        shippingOrderService.createShippingOrder(userDTO, order1, merchantOrder1, addressDTO.getId());

        // Perform assertions
        verify(shippingOrderRepository, times(1)).save(any(ShippingOrder.class));
    }

    @Test
    void test_GetAllShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderRepository.findAll()).thenReturn(shippingOrders);

        // Call the service method that uses the Repository
        List<ShippingOrderDTO> result = shippingOrderService.getAllShippingOrders(userDTO);
        List<ShippingOrderDTO> expected = shippingOrderDTOS;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderRepository.findByUserId(userDTO.getUserId())).thenReturn(shippingOrders);

        // Call the service method that uses the Repository
        List<ShippingOrderDTO> result = shippingOrderService.getUserShippingOrders(userDTO);
        List<ShippingOrderDTO> expected = shippingOrderDTOS;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByUserId(userDTO.getUserId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method that uses the Repository
        ShippingOrderDTO result = shippingOrderService.getUserShippingOrder(userDTO, id);
        ShippingOrderDTO expected = shippingOrderDTO1;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrderFail() {
        // Define the behavior of the mock
        int id = 1;
        shippingOrder1.setUserId(2);

        // Call the service method
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            shippingOrderService.getUserShippingOrder(userDTO, id);
        });

        // Perform assertions
        assertEquals(NOTFOUNDEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_GetUserShippingOrderByUserFail() {
        // Define the behavior of the mock
        int id = 1;
        userDTO.setUserId(2);

        // Call the service method
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            shippingOrderService.getUserShippingOrder(userDTO, id);
        });

        // Perform assertions
        assertEquals(NOTFOUNDEXCEPTIONMESSAGE, exception.getMessage());
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
                .merchant(merchant)
                .build();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);
        order1.setStatus(OrderStatusEnum.CANCELLED);
        when(orderService.fullCancelOrderByOrderId(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderUpdateDTO(order1, userDTO.getEmail()));
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        when(merchantOrderService.fullCancelMerchantOrderByShippingOrder(userDTO, shippingOrder1)).thenReturn(new MerchantOrderUpdateDTO(merchantOrder1, userDTO.getEmail()));
        itemUpdateDTO = new ItemUpdateDTO(shippingOrder1.getOrder().getItemQuantities().get(0).getItemId());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + shippingOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        verify(orderService, atLeastOnce()).fullCancelOrderByOrderId(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrderByShippingOrder(userDTO, shippingOrder1);
        verify(itemService, atLeastOnce()).addItemStock(itemUpdateDTO.getId(), itemUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrderFail() {
        // Define the behavior of the mock
        int id = 2;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        // Call the service method
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelShippingOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrderByOrder(userDTO, order1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrderByOrderByAdmin() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        userDTO.setRole(RoleEnum.ADMIN);
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrderByOrder(userDTO, order1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelShippingOrderByOrderByAdminFail() {
        // Define the behavior of the mock
        userDTO.setRole(RoleEnum.ADMIN);
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        // Call the service method
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            shippingOrderService.fullCancelShippingOrderByOrder(userDTO, order1);
        });

        // Perform assertions
        assertEquals(NOTFOUNDEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelShippingOrderByMerchantOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(merchantUserDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(merchantUserDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, merchantUserDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.fullCancelShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(merchantUserDTO, shippingOrder1.getOrder().getId());
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
                .merchant(merchant)
                .build();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO.setUserDTO(userDTO);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);
        order1.setStatus(OrderStatusEnum.REJECTED);
        when(orderService.rejectOrderByOrderId(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderUpdateDTO(order1, userDTO.getEmail()));
        merchantOrder1.setStatus(MerchantOrderStatusEnum.REJECTED);
        when(merchantOrderService.rejectMerchantOrderByShippingOrder(userDTO, shippingOrder1)).thenReturn(new MerchantOrderUpdateDTO(merchantOrder1, userDTO.getEmail()));
        itemUpdateDTO = new ItemUpdateDTO(shippingOrder1.getOrder().getItemQuantities().get(0).getItemId());
        itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + shippingOrder1.getOrder().getItemQuantities().get(0).getQuantityOrdered().getQuantity());
        when(itemService.addItemStock(itemUpdateDTO.getId(), itemUpdateDTO)).thenReturn(new ItemDTO(itemAux));

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        verify(orderService, atLeastOnce()).rejectOrderByOrderId(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrderByShippingOrder(userDTO, shippingOrder1);
        verify(itemService, atLeastOnce()).addItemStock(itemUpdateDTO.getId(), itemUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectShippingOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrderByOrder(userDTO, order1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectShippingOrderByOrderWhereShippingOrderAlreadyReject() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrderByOrder(userDTO, order1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectShippingOrderByOrderFail() {
        // Define the behavior of the mock
        int id = 2;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        // Call the service method
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderByMerchantOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderRepository.findByOrder(order1)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(merchantUserDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(merchantUserDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.rejectShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findByOrder(order1);
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(merchantUserDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveShippingOrderPending() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.PENDING);
        shippingOrder1Updated.setStatus(ShippingOrderStatusEnum.APPROVED);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
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
        shippingOrderUpdateDTO.setUserDTO(userDTO);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveShippingOrderFail() {
        // Define the behavior of the mock
        int id = 2;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);

        // Call the service method
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveShippingOrderFail2() {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.SHIPPED);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method
        WrongFlowException exception = assertThrows(WrongFlowException.class, () -> {
            shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        assertEquals("It is not possible to change Shipping Order status", exception.getMessage());
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
        shippingOrderUpdateDTO.setUserDTO(userDTO);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ShippedShippingOrderFail() {
        // Define the behavior of the mock
        int id = 2;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);

        // Call the service method
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
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

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));
        when(orderService.getUserOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new OrderDTO(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId())).thenReturn(new MerchantOrderDTO(merchantOrder1, userDTO.getEmail()));
        when(shippingOrderRepository.save(shippingOrder1)).thenReturn(shippingOrder1Updated);

        // Call the service method that uses the Repository
        ShippingOrderUpdateDTO result = shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO);
        shippingOrderUpdateDTO.setUserDTO(null);
        ShippingOrderUpdateDTO expected = shippingOrderUpdateDTO;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, shippingOrder1.getOrder().getId());
        verify(shippingOrderRepository, atLeastOnce()).save(shippingOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeliveredShippingOrderFail() {
        // Define the behavior of the mock
        int id = 2;
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);

        // Call the service method
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_GetUserShippingOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        userDTO.setRole(RoleEnum.ADMIN);

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method that uses the Repository
        ShippingOrderDTO result = shippingOrderService.getUserShippingOrder(userDTO, id);
        ShippingOrderDTO expected = shippingOrderDTO1;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrderByAdminFail() {
        // Define the behavior of the mock
        int id = 1;
        shippingOrder1.setUserId(2);
        userDTO.setRole(RoleEnum.ADMIN);

        // Call the service method
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            shippingOrderService.getUserShippingOrder(userDTO, id);
        });

        // Perform assertions
        assertEquals(NOTFOUNDEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_GetUserShippingOrderByMerchant() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        userDTO.setRole(RoleEnum.MERCHANT);
        shippingOrder1.getMerchantOrder().getMerchant().setEmail(userDTO.getEmail());

        when(shippingOrderRepository.findById(id)).thenReturn(Optional.ofNullable(shippingOrder1));

        // Call the service method that uses the Repository
        ShippingOrderDTO result = shippingOrderService.getUserShippingOrder(userDTO, id);
        ShippingOrderDTO expected = shippingOrderDTO1;

        // Perform assertions
        verify(shippingOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserShippingOrderByMerchantFail() {
        // Define the behavior of the mock
        int id = 1;
        shippingOrder1.setUserId(2);
        userDTO.setRole(RoleEnum.MERCHANT);

        // Call the service method
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            shippingOrderService.getUserShippingOrder(userDTO, id);
        });

        // Perform assertions
        assertEquals(NOTFOUNDEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_DeleteShippingOrderByOrderId() {
        // Define the behavior of the mock
        int id = 1;

        // Call the service method
        shippingOrderService.deleteShippingOrderByOrderId(id);

        // Perform assertions
        verify(shippingOrderRepository, times(1)).deleteByOrderId(id);
    }

}
