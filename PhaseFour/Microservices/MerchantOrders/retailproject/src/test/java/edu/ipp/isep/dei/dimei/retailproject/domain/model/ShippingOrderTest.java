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

        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        Merchant merchant = Merchant.builder()
                .id(1)
                .name("MerchantOrder 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO.getId())
                .build();


        order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(order.getId())
                .merchant(merchant)
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
                .status(status)
                .orderId(order.getId())
                .merchantOrderId(merchantOrder.getId())
                .userId(userDTO.getUserId())
                .build();
    }

    @Test
    void test_createShippingOrder() {
        ShippingOrder shippingOrder = new ShippingOrder(id, status, order.getId(), merchantOrder.getId(), userDTO.getUserId());

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(order.getId(), shippingOrder.getOrderId());
        assertEquals(merchantOrder.getId(), shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrder2() {
        ShippingOrder shippingOrder = new ShippingOrder(userDTO.getUserId(), order.getId(), merchantOrder.getId());

        assertNotNull(shippingOrder);
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(order.getId(), shippingOrder.getOrderId());
        assertEquals(merchantOrder.getId(), shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        shippingOrderExpected.setId(0);
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrderBuilder() {
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .id(id)
                .status(status)
                .orderId(order.getId())
                .merchantOrderId(merchantOrder.getId())
                .userId(userDTO.getUserId())
                .build();

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(order.getId(), shippingOrder.getOrderId());
        assertEquals(merchantOrder.getId(), shippingOrder.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_ShippingOrderSets() {
        ShippingOrder shippingOrder = ShippingOrder.builder().build();

        shippingOrder.setId(id);
        shippingOrder.setStatus(status);
        shippingOrder.setOrderId(order.getId());
        shippingOrder.setMerchantOrderId(merchantOrder.getId());
        shippingOrder.setUserId(userDTO.getUserId());

        assertEquals(shippingOrderExpected.getStatus(), shippingOrder.getStatus());
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
        shippingOrder.setStatus(ShippingOrderStatusEnum.APPROVED);
        shippingOrder.setOrderId(order.getId());
        shippingOrder.setMerchantOrderId(merchantOrder.getId());
        shippingOrder.setUserId(userDTO.getUserId());

        assertNotEquals(shippingOrder.getId(), id);
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
