package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ShippingOrderTest {
    final ShippingOrderStatusEnum status = ShippingOrderStatusEnum.PENDING;
    int id;
    Instant shippingOrderDate;
    AddressDTO shippingAddressDTO;
    Order order;
    MerchantOrder merchantOrder;
    UserDTO userDTO;
    ShippingOrder shippingOrderExpected;
    int orderId = 1;
    int merchantOrderId = 1;
    int merchantId = 1;

    @BeforeEach
    void beforeEach() {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        order = Order.builder()
                .id(orderId)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(merchantOrderId)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(order.getId())
                .merchantId(merchantId)
                .build();

        shippingAddressDTO = AddressDTO.builder()
                .id(2)
                .street("Another Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        shippingOrderExpected = ShippingOrder.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .status(status)
                .shippingAddressId(shippingAddressDTO.getId())
                .orderId(order.getId())
                .merchantOrderId(merchantOrder.getId())
                .userId(userDTO.getUserId())
                .build();
    }

    @Test
    void test_createShippingOrder() {
        ShippingOrder shippingOrder = new ShippingOrder(id, shippingOrderDate, status, shippingAddressDTO.getId(), orderId, merchantOrderId, userDTO.getUserId());

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(orderId, shippingOrder.getOrderId());
        assertEquals(merchantOrderId, shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrder2() {
        ShippingOrder shippingOrder = new ShippingOrder(userDTO.getUserId(), order.getId(), order.getOrderDate(), merchantOrderId, shippingAddressDTO.getId());

        assertNotNull(shippingOrder);
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(orderId, shippingOrder.getOrderId());
        assertEquals(merchantOrderId, shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        shippingOrderExpected.setId(0);
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrderBuilder() {
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .status(status)
                .shippingAddressId(shippingAddressDTO.getId())
                .orderId(orderId)
                .merchantOrderId(merchantOrderId)
                .userId(userDTO.getUserId())
                .build();

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(orderId, shippingOrder.getOrderId());
        assertEquals(merchantOrderId, shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_ShippingOrderSets() {
        ShippingOrder shippingOrder = ShippingOrder.builder().build();

        shippingOrder.setId(id);
        shippingOrder.setShippingOrderDate(shippingOrderDate);
        shippingOrder.setStatus(status);
        shippingOrder.setShippingAddressId(shippingAddressDTO.getId());
        shippingOrder.setOrderId(orderId);
        shippingOrder.setMerchantOrderId(merchantOrderId);
        shippingOrder.setUserId(userDTO.getUserId());

        assertEquals(shippingOrderExpected.getShippingOrderDate(), shippingOrder.getShippingOrderDate());
        assertEquals(shippingOrderExpected.getStatus(), shippingOrder.getStatus());
        assertEquals(shippingOrderExpected.getShippingAddressId(), shippingOrder.getShippingAddressId());
        assertEquals(shippingOrderExpected.getOrderId(), shippingOrder.getOrderId());
        assertEquals(shippingOrderExpected.getMerchantOrderId(), shippingOrder.getMerchantOrderId());
        assertEquals(shippingOrderExpected.getUserId(), shippingOrder.getUserId());

        assertEquals(shippingOrderExpected, shippingOrder);
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
    }

    @Test
    void test_ShippingOrderSets2() {
        ShippingOrder shippingOrder = ShippingOrder.builder().build();

        shippingOrder.setId(2);
        shippingOrder.setShippingOrderDate(Instant.MAX);
        shippingOrder.setStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder.setShippingAddressId(shippingAddressDTO.getId());
        shippingOrder.setOrderId(orderId);
        shippingOrder.setMerchantOrderId(merchantOrderId);
        shippingOrder.setUserId(userDTO.getUserId());

        assertNotEquals(shippingOrder.getId(), id);
        assertNotEquals(shippingOrder.getShippingOrderDate(), shippingOrderDate);
        assertNotEquals(shippingOrder.getStatus(), status);

        assertNotEquals(shippingOrderExpected, shippingOrder);
        assertNotEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
    }

    @Test
    void test_isApprovedShippingOrderFail() {
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isApproved());
    }

    @Test
    void test_isShippedShippingOrderFail() {
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isShipped());
    }

    @Test
    void test_isPendingOrApprovedShippingOrder() {
        shippingOrderExpected.setStatus(ShippingOrderStatusEnum.APPROVED);
        assertNotNull(shippingOrderExpected);
        assertTrue(shippingOrderExpected.isPendingOrApproved());
    }

    @Test
    void test_isPendingOrApprovedShippingOrderFail() {
        shippingOrderExpected.setStatus(ShippingOrderStatusEnum.SHIPPED);
        assertNotNull(shippingOrderExpected);
        assertFalse(shippingOrderExpected.isPendingOrApproved());
    }
}
