package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

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
class MerchantOrderUpdateDTOTest {
    int id;
    Instant merchantOrderDate;
    MerchantOrderStatusEnum merchantOrderStatus = MerchantOrderStatusEnum.PENDING;
    String email;
    String userEmail;
    int orderId;
    int merchantId;
    MerchantOrderUpdateDTO merchantOrderUpdateDTOExpected;
    MerchantOrder merchantOrder;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        merchantOrderDate = currentDate;
        email = "merchantnumber1@gmail.com";
        userEmail = "johndoe1234@gmail.com";
        orderId = 1;
        merchantId = 1;
        merchantOrderUpdateDTOExpected = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId);

        Address address = Address.builder()
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
                .email(email)
                .address(address)
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .user(user)
                .order(order)
                .merchant(merchant)
                .build();
    }

    @Test
    void test_createMerchantOrderUpdateDTO() {
        MerchantOrderUpdateDTO merchantDTO = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderUpdateDTOBuilder() {
        MerchantOrderUpdateDTO merchantDTO = MerchantOrderUpdateDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(merchantOrderStatus)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }


    @Test
    void test_createMerchantOrderUpdateDTOByMerchantOrder() {
        MerchantOrderUpdateDTO merchantDTO = new MerchantOrderUpdateDTO(merchantOrder);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(userEmail, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderUpdateDTONoArgsConstructor() {
        MerchantOrderUpdateDTO merchantDTO = MerchantOrderUpdateDTO.builder().build();
        assertNotNull(merchantDTO);
    }
}
