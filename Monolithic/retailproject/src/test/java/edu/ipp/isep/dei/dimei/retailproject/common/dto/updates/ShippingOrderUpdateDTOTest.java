package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
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
    AddressDTO addressDTO;
    int orderId;
    int merchantOrderId;
    String email;
    ShippingOrder shippingOrder;
    ShippingOrderUpdateDTO shippingUpgradeOrderDTO;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        shippingOrderStatus = ShippingOrderStatusEnum.PENDING;
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


        Address address1 = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        Address address2 = Address.builder()
                .id(2)
                .street("Other Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        Account account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        List<ItemQuantity> orderItems = new ArrayList<>();

        Item item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(12)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Item 2")
                .sku("ABC-12345-XS-BL")
                .description("Item 2 description")
                .price(5)
                .build();

        ItemQuantity itemQuantity1 = new ItemQuantity(1, new OrderQuantity(3), item1);
        ItemQuantity itemQuantity2 = new ItemQuantity(2, new OrderQuantity(5), item2);

        orderItems.add(itemQuantity1);
        orderItems.add(itemQuantity2);

        Payment payment = Payment.builder()
                .id(1)
                .amount(61)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        Order order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(orderItems)
                .payment(payment)
                .build();

        Merchant merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("johndoe1234@gmail.com")
                .address(address2)
                .build();

        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();

        shippingOrder = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDate)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddress(address1)
                .order(order)
                .merchantOrder(merchantOrder)
                .user(user)
                .build();

        shippingUpgradeOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO, orderId, merchantOrderId, email);
    }

    @Test
    void test_createShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO, orderId, merchantOrderId, email);

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO, shippingUpdOrderDTO.getAddressDTO());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingUpdOrderDTO.getEmail());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTOByShippingOrder() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = new ShippingOrderUpdateDTO(shippingOrder);

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO, shippingUpdOrderDTO.getAddressDTO());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingUpdOrderDTO.getEmail());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTOBuilder() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .shippingOrderStatus(shippingOrderStatus)
                .addressDTO(addressDTO)
                .orderId(orderId)
                .merchantOrderId(merchantOrderId)
                .email(email)
                .build();

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO, shippingUpdOrderDTO.getAddressDTO());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(email, shippingUpdOrderDTO.getEmail());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTONoArgsConstructor() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder().build();
        assertNotNull(shippingUpdOrderDTO);
    }

    @Test
    void test_SetsShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO expected = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO, orderId, merchantOrderId, email);
        ShippingOrderUpdateDTO result = ShippingOrderUpdateDTO.builder().build();

        result.setId(id);
        result.setShippingOrderDate(shippingOrderDate);
        result.setShippingOrderStatus(shippingOrderStatus);
        result.setAddressDTO(addressDTO);
        result.setOrderId(orderId);
        result.setMerchantOrderId(merchantOrderId);
        result.setEmail(email);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(shippingOrderDate, result.getShippingOrderDate());
        assertEquals(shippingOrderStatus, result.getShippingOrderStatus());
        assertEquals(addressDTO, result.getAddressDTO());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantOrderId, result.getMerchantOrderId());
        assertEquals(email, result.getEmail());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }
}
