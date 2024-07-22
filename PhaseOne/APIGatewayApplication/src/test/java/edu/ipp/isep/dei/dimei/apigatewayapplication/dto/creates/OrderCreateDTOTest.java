package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.creates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.RoleEnum;
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
class OrderCreateDTOTest {

    final OrderStatusEnum orderStatusEnum = OrderStatusEnum.PENDING;
    int id = 1;
    Instant orderDate;
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
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        orderDate = Instant.now();
        customerId = 1;
        email = "merchant_email@gmail.com";
        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);
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
                .paymentDateTime(Instant.now())
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
        orderCreateDTOExpected = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO, userDTO);
    }

    @Test
    void test_createOrderCreateDTO() {
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO, userDTO);

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
        assertEquals(userDTO, orderCreateDTO.getUserDTO());
        assertEquals(orderCreateDTOExpected, orderCreateDTO);
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
                .userDTO(userDTO)
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
        assertEquals(userDTO, orderCreateDTO.getUserDTO());
        assertEquals(orderCreateDTOExpected, orderCreateDTO);
        assertEquals(orderCreateDTOExpected.hashCode(), orderCreateDTO.hashCode());
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
    void test_SetsOrderCreateDTO() {
        OrderCreateDTO expected = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO, userDTO);
        OrderCreateDTO result = OrderCreateDTO.builder().build();

        result.setId(id);
        result.setOrderDate(orderDate);
        result.setOrderStatus(orderStatusEnum);
        result.setCustomerId(customerId);
        result.setEmail(email);
        result.setOrderItems(itemQuantityDTOList);
        result.setTotalPrice(totalPrice);
        result.setPayment(paymentDTO);
        result.setMerchantId(merchantId);
        result.setAddress(addressDTO);
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatusEnum, result.getOrderStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(email, result.getEmail());
        assertEquals(itemQuantityDTOList, result.getOrderItems());
        assertEquals(totalPrice, result.getTotalPrice());
        assertEquals(paymentDTO, result.getPayment());
        assertEquals(merchantId, result.getMerchantId());
        assertEquals(addressDTO, result.getAddress());
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
    }

}
