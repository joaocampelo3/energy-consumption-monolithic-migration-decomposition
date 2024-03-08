package edu.ipp.isep.dei.dimei.retailproject.commom.dto.creates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderCreateDTOTest {

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

    @BeforeEach
    void beforeEach() {
        id = 1;
        orderDate = LocalDateTime.now();
        customerId = 1;
        email = "merchant_email@gmail.com";
        itemQuantityDTO1 = ItemQuantityDTO.builder()
                .id(1)
                .itemId(1)
                .itemName("Item 1")
                .itemDescription("Item 1 Description")
                .qty(10)
                .price(5)
                .build();
        itemQuantityDTO2 = ItemQuantityDTO.builder()
                .id(2)
                .itemId(2)
                .itemName("Item 2")
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
    }

    @Test
    public void test_createOrderCreateDTO() {
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(id, orderDate, orderStatusEnum, customerId, email, itemQuantityDTOList, totalPrice, paymentDTO, merchantId, addressDTO);

        assertNotNull(orderCreateDTO);
        assertEquals(id, orderCreateDTO.getId());
        assertEquals(orderDate, orderCreateDTO.getOrderDate());
        assertEquals(orderStatusEnum, orderCreateDTO.getOrderStatus());
        assertEquals(customerId, orderCreateDTO.getCustomerId());
        assertEquals(email, orderCreateDTO.getEmail());
        assertEquals(itemQuantityDTOList, orderCreateDTO.getOrderItems());
        assertEquals(totalPrice, orderCreateDTO.getTotalPrice());
        assertEquals(paymentDTO, orderCreateDTO.getPaymentDTO());
        assertEquals(merchantId, orderCreateDTO.getMerchantId());
        assertEquals(addressDTO, orderCreateDTO.getAddressDTO());
    }

    @Test
    public void test_createOrderCreateDTOBuilder() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatusEnum)
                .customerId(customerId)
                .email(email)
                .orderItems(itemQuantityDTOList)
                .totalPrice(totalPrice)
                .paymentDTO(paymentDTO)
                .merchantId(merchantId)
                .addressDTO(addressDTO)
                .build();

        assertNotNull(orderCreateDTO);
        assertEquals(id, orderCreateDTO.getId());
        assertEquals(orderDate, orderCreateDTO.getOrderDate());
        assertEquals(orderStatusEnum, orderCreateDTO.getOrderStatus());
        assertEquals(customerId, orderCreateDTO.getCustomerId());
        assertEquals(email, orderCreateDTO.getEmail());
        assertEquals(itemQuantityDTOList, orderCreateDTO.getOrderItems());
        assertEquals(totalPrice, orderCreateDTO.getTotalPrice());
        assertEquals(paymentDTO, orderCreateDTO.getPaymentDTO());
        assertEquals(merchantId, orderCreateDTO.getMerchantId());
        assertEquals(addressDTO, orderCreateDTO.getAddressDTO());
    }

    @Test
    public void test_createOrderCreateDTOToString() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatusEnum)
                .customerId(customerId)
                .email(email)
                .orderItems(itemQuantityDTOList)
                .totalPrice(totalPrice)
                .paymentDTO(paymentDTO)
                .merchantId(merchantId)
                .addressDTO(addressDTO)
                .build();

        assertNotNull(orderCreateDTO);
        assertNotNull(orderCreateDTO.toString());
    }

    @Test
    public void test_createOrderCreateDTONoArgsConstructor() {
        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder().build();
        assertNotNull(orderCreateDTO);
    }

}
