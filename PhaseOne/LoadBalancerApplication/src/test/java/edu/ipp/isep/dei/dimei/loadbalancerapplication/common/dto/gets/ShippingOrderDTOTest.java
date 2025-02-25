package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.ShippingOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

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
    ShippingOrderDTO shippingOrderDTOExpected;
    UserDTO userDTO;

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
