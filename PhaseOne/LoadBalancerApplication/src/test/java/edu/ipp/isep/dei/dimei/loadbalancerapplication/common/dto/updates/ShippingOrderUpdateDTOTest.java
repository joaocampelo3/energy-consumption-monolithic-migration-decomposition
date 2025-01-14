package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.ShippingOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

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
    UserDTO userDTO;
    ShippingOrderUpdateDTO shippingUpgradeOrderDTO;

    @BeforeEach
    void beforeEach() {
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
        userDTO = new UserDTO(1, email, RoleEnum.USER);

        shippingUpgradeOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO.getId(), orderId, merchantOrderId, userDTO.getUserId(), userDTO);
    }

    @Test
    void test_createShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO.getId(), orderId, merchantOrderId, userDTO.getUserId(), userDTO);

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), shippingUpdOrderDTO.getAddressId());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingUpdOrderDTO.getUserId());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTOBuilder() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder()
                .id(id)
                .shippingOrderDate(shippingOrderDate)
                .shippingOrderStatus(shippingOrderStatus)
                .addressId(addressDTO.getId())
                .orderId(orderId)
                .merchantOrderId(merchantOrderId)
                .userId(userDTO.getUserId())
                .userDTO(userDTO)
                .build();

        assertNotNull(shippingUpdOrderDTO);
        assertEquals(id, shippingUpdOrderDTO.getOrderId());
        assertEquals(shippingOrderDate, shippingUpdOrderDTO.getShippingOrderDate());
        assertEquals(shippingOrderStatus, shippingUpdOrderDTO.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), shippingUpdOrderDTO.getAddressId());
        assertEquals(orderId, shippingUpdOrderDTO.getOrderId());
        assertEquals(merchantOrderId, shippingUpdOrderDTO.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), shippingUpdOrderDTO.getUserId());
        assertEquals(userDTO, shippingUpdOrderDTO.getUserDTO());
        assertEquals(shippingUpgradeOrderDTO.hashCode(), shippingUpdOrderDTO.hashCode());
    }

    @Test
    void test_createShippingOrderUpdateDTONoArgsConstructor() {
        ShippingOrderUpdateDTO shippingUpdOrderDTO = ShippingOrderUpdateDTO.builder().build();
        assertNotNull(shippingUpdOrderDTO);
    }

    @Test
    void test_SetsShippingOrderUpdateDTO() {
        ShippingOrderUpdateDTO expected = new ShippingOrderUpdateDTO(id, shippingOrderDate, shippingOrderStatus, addressDTO.getId(), orderId, merchantOrderId, userDTO.getUserId(), userDTO);
        ShippingOrderUpdateDTO result = ShippingOrderUpdateDTO.builder().build();

        result.setId(id);
        result.setShippingOrderDate(shippingOrderDate);
        result.setShippingOrderStatus(shippingOrderStatus);
        result.setAddressId(addressDTO.getId());
        result.setOrderId(orderId);
        result.setMerchantOrderId(merchantOrderId);
        result.setUserId(userDTO.getUserId());
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(shippingOrderDate, result.getShippingOrderDate());
        assertEquals(shippingOrderStatus, result.getShippingOrderStatus());
        assertEquals(addressDTO.getId(), result.getAddressId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantOrderId, result.getMerchantOrderId());
        assertEquals(userDTO.getUserId(), result.getUserId());
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }
}
