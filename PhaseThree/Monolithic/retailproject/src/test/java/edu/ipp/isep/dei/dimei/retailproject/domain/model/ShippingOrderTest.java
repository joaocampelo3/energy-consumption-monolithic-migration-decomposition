package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ShippingOrderTest {
    final ShippingOrderStatusEnum status = ShippingOrderStatusEnum.PENDING;
    int id;
    double price;
    Instant shippingOrderDate;
    AddressDTO shippingAddressDTO;
    Order order;
    MerchantOrder merchantOrder;
    UserDTO userDTO;
    ShippingOrder shippingOrderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        price = 12.0;

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

        List<ItemQuantity> itemQuantityList = new ArrayList<>();
        ItemQuantity itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .itemId(1)
                .price(price)
                .build();
        itemQuantityList.add(itemQuantity1);
        double totalPrice = itemQuantityList.stream().mapToDouble(value -> value.getPrice() * value.getQuantityOrdered().getQuantity()).sum();

        Payment payment = Payment.builder()
                .id(1)
                .amount(totalPrice)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList)
                .payment(payment)
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .order(order)
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
                .shippingOrderDate(shippingOrderDate)
                .status(status)
                .shippingAddressId(shippingAddressDTO.getId())
                .order(order)
                .merchantOrder(merchantOrder)
                .userId(userDTO.getUserId())
                .build();
    }

    @Test
    void test_createShippingOrder() {
        ShippingOrder shippingOrder = new ShippingOrder(id, shippingOrderDate, status, shippingAddressDTO.getId(), order, merchantOrder, userDTO.getUserId());

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
        assertEquals(userDTO.getUserId(), shippingOrder.getUserId());
        assertEquals(shippingOrderExpected.hashCode(), shippingOrder.hashCode());
        assertEquals(shippingOrderExpected, shippingOrder);
    }

    @Test
    void test_createShippingOrder2() {
        ShippingOrder shippingOrder = new ShippingOrder(userDTO.getUserId(), order, merchantOrder, shippingAddressDTO.getId());

        assertNotNull(shippingOrder);
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
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
                .order(order)
                .merchantOrder(merchantOrder)
                .userId(userDTO.getUserId())
                .build();

        assertNotNull(shippingOrder);
        assertEquals(id, shippingOrder.getId());
        assertEquals(shippingOrderDate, shippingOrder.getShippingOrderDate());
        assertEquals(status, shippingOrder.getStatus());
        assertEquals(shippingAddressDTO.getId(), shippingOrder.getShippingAddressId());
        assertEquals(order, shippingOrder.getOrder());
        assertEquals(merchantOrder, shippingOrder.getMerchantOrder());
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
        shippingOrder.setOrder(order);
        shippingOrder.setMerchantOrder(merchantOrder);
        shippingOrder.setUserId(userDTO.getUserId());

        assertEquals(shippingOrderExpected.getShippingOrderDate(), shippingOrder.getShippingOrderDate());
        assertEquals(shippingOrderExpected.getStatus(), shippingOrder.getStatus());
        assertEquals(shippingOrderExpected.getShippingAddressId(), shippingOrder.getShippingAddressId());
        assertEquals(shippingOrderExpected.getOrder(), shippingOrder.getOrder());
        assertEquals(shippingOrderExpected.getMerchantOrder(), shippingOrder.getMerchantOrder());
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
        shippingOrder.setOrder(order);
        shippingOrder.setMerchantOrder(merchantOrder);
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
