package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ShippingOrderUpdateDTOTest {
    int id;
    Instant shippingOrderDate;
    ShippingOrderStatusEnum shippingOrderStatus;
    AddressDTO addressDTO1;
    int orderId;
    int merchantOrderId;
    int userId;
    ShippingOrder shippingOrder;
    ShippingOrderUpdateDTO shippingUpgradeOrderDTO;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        shippingOrderStatus = ShippingOrderStatusEnum.PENDING;
        orderId = 1;
        userId = 1;
        merchantOrderId = 1;
        double price1 = 12.0;
        double price2 = 5.0;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        addressDTO1 = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        AddressDTO addressDTO2 = AddressDTO.builder()
                .id(2)
                .street("Other Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        List<ItemQuantity> orderItems = new ArrayList<>();

        int itemId1 = 1;
        int itemId2 = 2;

        ItemQuantity itemQuantity1 = new ItemQuantity(1, new OrderQuantity(3), itemId1, price1);
        ItemQuantity itemQuantity2 = new ItemQuantity(2, new OrderQuantity(5), itemId2, price2);

        orderItems.add(itemQuantity1);
        orderItems.add(itemQuantity2);

        Payment payment = Payment.builder()
                .id(1)
                .amount(price1 + price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        Order order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(orderItems)
                .payment(payment)
                .build();

        Merchant merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("johndoe1234@gmail.com")
                .addressId(addressDTO2.getId())
                .build();

        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .order(order)
                .merchant(merchant)
                .build();

        shippingOrder = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDate)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(addressDTO1.getId())
                .order(order)
                .merchantOrder(merchantOrder)
                .userId(userDTO.getUserId())
                .build();

        shippingUpgradeOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO1.getId(), orderId, merchantOrderId, userId, userDTO);
    }

    @Test
    void test_createShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO1.getId(), orderId, merchantOrderId, userId, userDTO);

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO1.getId(), shippingUpdOrderDTO.getAddressId());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(userId, shippingUpdOrderDTO.getUserId());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTOByShippingOrder() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = new ShippingOrderUpdateDTO(shippingOrder);
        shippingUpgradeOrderDTO.setUserDTO(null);

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO1.getId(), shippingUpdOrderDTO.getAddressId());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(userId, shippingUpdOrderDTO.getUserId());
        assertEquals(shippingUpgradeOrderDTO, shippingUpdOrderDTO);
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTOBuilder() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .shippingOrderStatus(shippingOrderStatus)
                .addressId(addressDTO1.getId())
                .orderId(orderId)
                .merchantOrderId(merchantOrderId)
                .userId(userId)
                .userDTO(userDTO)
                .build();

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO1.getId(), shippingUpdOrderDTO.getAddressId());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(userId, shippingUpdOrderDTO.getUserId());
        assertEquals(shippingUpgradeOrderDTO, shippingUpdOrderDTO);
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTONoArgsConstructor() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder().build();
        assertNotNull(shippingUpdOrderDTO);
    }

    @Test
    void test_SetsShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO expected = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO1.getId(), orderId, merchantOrderId, userId, userDTO);
        ShippingOrderUpdateDTO result = ShippingOrderUpdateDTO.builder().build();

        result.setId(id);
        result.setShippingOrderDate(shippingOrderDate);
        result.setShippingOrderStatus(shippingOrderStatus);
        result.setAddressId(addressDTO1.getId());
        result.setOrderId(orderId);
        result.setMerchantOrderId(merchantOrderId);
        result.setUserId(userId);
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(shippingOrderDate, result.getShippingOrderDate());
        assertEquals(shippingOrderStatus, result.getShippingOrderStatus());
        assertEquals(addressDTO1.getId(), result.getAddressId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantOrderId, result.getMerchantOrderId());
        assertEquals(userId, result.getUserId());
        assertEquals(expected, result);
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected.toString(), result.toString());
    }
}
