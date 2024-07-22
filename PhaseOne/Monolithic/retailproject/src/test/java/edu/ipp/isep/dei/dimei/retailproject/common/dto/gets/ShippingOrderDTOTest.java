package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ShippingOrderDTOTest {
    int id;
    Instant shippingOrderDate;
    ShippingOrderStatusEnum shippingOrderStatus;
    AddressDTO addressDTO;
    int orderId;
    int merchantOrderId;
    String email;
    ShippingOrder shippingOrder;
    ShippingOrderDTO shippingOrderDTOExpected;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        shippingOrderStatus = ShippingOrderStatusEnum.PENDING;
        double price1 = 12.0;
        double price2 = 5.0;
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        orderId = 1;
        merchantOrderId = 1;
        email = "johndoe1234@gmail.com";


        AddressDTO shippingAddressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        AddressDTO merchantAddressDTO = AddressDTO.builder()
                .id(2)
                .street("Other Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);

        List<ItemQuantity> orderItems = new ArrayList<>();

        Item item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(price1)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Item 2")
                .sku("ABC-12345-XS-BL")
                .description("Item 2 description")
                .price(price2)
                .build();

        ItemQuantity itemQuantity1 = new ItemQuantity(1, new OrderQuantity(3), item1, price1);
        ItemQuantity itemQuantity2 = new ItemQuantity(2, new OrderQuantity(5), item2, price2);

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
                .addressId(merchantAddressDTO.getId())
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
                .shippingAddressId(shippingAddressDTO.getId())
                .order(order)
                .merchantOrder(merchantOrder)
                .userId(userDTO.getUserId())
                .build();

        shippingOrderDTOExpected = new ShippingOrderDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO.getId(), orderId, merchantOrderId, email);
    }

    @Test
    void test_createShippingOrderDTO() {
        ShippingOrderDTO shippingOrderDTO = new ShippingOrderDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO.getId(), orderId, merchantOrderId, email);

        assertNotNull(shippingOrderDTO);
        assertEquals(id, shippingOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), shippingOrderDTO.getAddressId());
        assertEquals(orderId, shippingOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingOrderDTO.getEmail());
        assertEquals(shippingOrderDTOExpected.hashCode(), shippingOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderDTOByShippingOrder() {
        ShippingOrderDTO shippingOrderDTO = new ShippingOrderDTO(shippingOrder, userDTO.getEmail());

        assertNotNull(shippingOrderDTO);
        assertEquals(id, shippingOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), shippingOrderDTO.getAddressId());
        assertEquals(orderId, shippingOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingOrderDTO.getEmail());
        assertEquals(shippingOrderDTOExpected.hashCode(), shippingOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderDTOBuilder() {
        ShippingOrderDTO shippingOrderDTO = ShippingOrderDTO.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .shippingOrderStatus(shippingOrderStatus)
                .addressId(addressDTO.getId())
                .orderId(orderId)
                .merchantOrderId(merchantOrderId)
                .email(email)
                .build();

        assertNotNull(shippingOrderDTO);
        assertEquals(id, shippingOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), shippingOrderDTO.getAddressId());
        assertEquals(orderId, shippingOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingOrderDTO.getEmail());
        assertEquals(shippingOrderDTOExpected.hashCode(), shippingOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderDTONoArgsConstructor() {
        ShippingOrderDTO shippingOrderDTO = ShippingOrderDTO.builder().build();
        assertNotNull(shippingOrderDTO);
    }

    @Test
    void test_isApprovedShippingOrderDTO() {
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        assertNotNull(shippingOrderDTOExpected);
        assertTrue(shippingOrderDTOExpected.isApproved());
    }

    @Test
    void test_isApprovedShippingOrderDTOFail() {
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isApproved());
    }

    @Test
    void test_isRejectedShippingOrderDTOFail() {
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isRejected());
    }

    @Test
    void test_isCancelledShippingOrderDTOFail() {
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isCancelled());
    }

    @Test
    void test_isShippedShippingOrderDTOFail() {
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isShipped());
    }

    @Test
    void test_isDeliveredShippingOrderDTOFail() {
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isDelivered());
    }

    @Test
    void test_isPendingOrApprovedShippingOrderDTO() {
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        assertNotNull(shippingOrderDTOExpected);
        assertTrue(shippingOrderDTOExpected.isPendingOrApproved());
    }

    @Test
    void test_isPendingOrApprovedShippingOrderDTOFail() {
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isPendingOrApproved());
    }

    @Test
    void test_isPendingOrApprovedOrRejectedShippingOrderDTOFail() {
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isPendingOrApprovedOrRejected());
    }

    @Test
    void test_isPendingOrApprovedOrCancelledShippingOrderDTOFail() {
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        assertNotNull(shippingOrderDTOExpected);
        assertFalse(shippingOrderDTOExpected.isPendingOrApprovedOrCancelled());
    }

    @Test
    void test_SetsShippingOrderDTO() {
        ShippingOrderDTO result = ShippingOrderDTO.builder().build();

        result.setId(id);
        result.setShippingOrderDate(shippingOrderDate);
        result.setShippingOrderStatus(shippingOrderStatus);
        result.setAddressId(addressDTO.getId());
        result.setOrderId(orderId);
        result.setMerchantOrderId(merchantOrderId);
        result.setEmail(email);

        assertNotNull(result);
        assertEquals(id, result.getOrderId());
        assertEquals(shippingOrderDate, result.getShippingOrderDate());
        assertEquals(shippingOrderStatus, result.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), result.getAddressId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantOrderId, result.getMerchantOrderId());
        assertEquals(email, result.getEmail());
        assertEquals(shippingOrderDTOExpected.hashCode(), result.hashCode());
        assertEquals(shippingOrderDTOExpected, result);
        assertEquals(shippingOrderDTOExpected.toString(), result.toString());
    }
}
