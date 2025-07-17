package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShippingOrderUpdateDTOTest {
    int id;
    Instant shippingOrderDate;
    ShippingOrderStatusEnum shippingOrderStatus;
    AddressDTO addressDTO1;
    int orderId;
    int merchantOrderId;
    int userId;
    ShippingOrderUpdateDTO shippingUpgradeOrderDTO;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        Instant currentDate = Instant.now();
        shippingOrderDate = currentDate;
        shippingOrderStatus = ShippingOrderStatusEnum.PENDING;
        orderId = 1;
        userId = 1;
        merchantOrderId = 1;

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

        Order order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
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
