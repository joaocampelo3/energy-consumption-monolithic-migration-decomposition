package edu.ipp.isep.dei.dimei.retailproject.common.dto.creates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class OrderCreateDTOTest {

    int id = 1;
    LocalDateTime orderDate;
    OrderStatusEnum orderStatusEnum = OrderStatusEnum.PENDING;
    int customerId;
    String email;
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantityDTO itemQuantityDTO2;
    List<ItemQuantityDTO> itemQuantityDTOList = new ArrayList<>();
    double totalPrice;
    PaymentDTO paymentDTO;
    int merchantId;
    AddressDTO addressDTO;
    OrderCreateDTO orderCreateDTOExpected;
    User user;
    Payment payment;
    List<ItemQuantity> itemQuantities = new ArrayList<>();
    Order orderExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        orderDate = LocalDateTime.now();
        customerId = 1;
        email = "merchant_email@gmail.com";
        itemQuantityDTO1 = ItemQuantityDTO.builder()
                .id(1)
                .itemId(1)
                .itemName("Item 1")
                .itemSku("ABC-12345-S-BL")
                .itemDescription("Item 1 Description")
                .qty(10)
                .price(5)
                .build();
        itemQuantityDTO2 = ItemQuantityDTO.builder()
                .id(2)
                .itemId(2)
                .itemName("Item 2")
                .itemSku("ABC-12345-M-BL")
                .itemDescription("Item 2 Description")
                .qty(5)
                .price(5)
                .build();
        itemQuantityDTOList.add(itemQuantityDTO1);
        itemQuantityDTOList.add(itemQuantityDTO2);
        totalPrice = itemQuantityDTOList.stream().mapToDouble(value -> value.getPrice() * value.getQty()).sum();
        paymentDTO = PaymentDTO.builder()
                .id(1)
                .amount(totalPrice)
                .paymentDateTime(LocalDateTime.now())
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();
        merchantId = 1;
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        orderCreateDTOExpected = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO);

        Account account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        payment = paymentDTO.dtoToEntity();

        itemQuantities.add(itemQuantityDTO1.dtoToEntity());
        itemQuantities.add(itemQuantityDTO2.dtoToEntity());

        orderExpected = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(orderStatusEnum)
                .user(user)
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();
    }

    @Test
    void test_createOrderCreateDTO() {
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO);

        assertNotNull(orderCreateDTO);
        assertEquals(id, orderCreateDTO.getId());
        assertEquals(orderDate, orderCreateDTO.getOrderDate());
        assertEquals(orderStatusEnum, orderCreateDTO.getOrderStatus());
        assertEquals(customerId, orderCreateDTO.getCustomerId());
        assertEquals(email, orderCreateDTO.getEmail());
        assertEquals(itemQuantityDTOList, orderCreateDTO.getOrderItems());
        assertEquals(totalPrice, orderCreateDTO.getTotalPrice());
        assertEquals(paymentDTO, orderCreateDTO.getPayment());
        assertEquals(merchantId, orderCreateDTO.getMerchantId());
        assertEquals(addressDTO, orderCreateDTO.getAddress());
        assertEquals(orderCreateDTOExpected.hashCode(), orderCreateDTO.hashCode());
    }

    @Test
    void test_createOrderCreateDTOBuilder() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatusEnum)
                .customerId(customerId)
                .email(email)
                .orderItems(itemQuantityDTOList)
                .totalPrice(totalPrice)
                .payment(paymentDTO)
                .merchantId(merchantId)
                .address(addressDTO)
                .build();

        assertNotNull(orderCreateDTO);
        assertEquals(id, orderCreateDTO.getId());
        assertEquals(orderDate, orderCreateDTO.getOrderDate());
        assertEquals(orderStatusEnum, orderCreateDTO.getOrderStatus());
        assertEquals(customerId, orderCreateDTO.getCustomerId());
        assertEquals(email, orderCreateDTO.getEmail());
        assertEquals(itemQuantityDTOList, orderCreateDTO.getOrderItems());
        assertEquals(totalPrice, orderCreateDTO.getTotalPrice());
        assertEquals(paymentDTO, orderCreateDTO.getPayment());
        assertEquals(merchantId, orderCreateDTO.getMerchantId());
        assertEquals(addressDTO, orderCreateDTO.getAddress());
        assertEquals(orderCreateDTOExpected.hashCode(), orderCreateDTO.hashCode());
        assertEquals(orderCreateDTOExpected, orderCreateDTO);
    }

    @Test
    void test_createOrderCreateDTOToString() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatusEnum)
                .customerId(customerId)
                .email(email)
                .orderItems(itemQuantityDTOList)
                .totalPrice(totalPrice)
                .payment(paymentDTO)
                .merchantId(merchantId)
                .address(addressDTO)
                .build();

        assertNotNull(orderCreateDTO);
        assertNotNull(orderCreateDTO.toString());
    }

    @Test
    void test_createOrderCreateDTONoArgsConstructor() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder().build();
        assertNotNull(orderCreateDTO);
    }

    @Test
    void test_DtoToEntityOrderCreateDTO() {
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO);
        Order order = orderCreateDTO.dtoToEntity(user, payment, itemQuantities);

        assertNotNull(orderCreateDTO);
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

}
