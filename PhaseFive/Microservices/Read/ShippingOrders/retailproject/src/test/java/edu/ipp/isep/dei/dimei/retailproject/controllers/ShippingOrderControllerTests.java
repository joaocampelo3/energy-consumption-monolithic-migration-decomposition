package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ShippingOrderControllerTests {
    final String exceptionShippingOrderNotFound = "Shipping Order not found.";
    final String exceptionShippingOrderBadRequest = "Wrong shipping order payload.";
    @InjectMocks
    ShippingOrderController shippingOrderController;
    @Mock
    ShippingOrderService shippingOrderService;
    ShippingOrderDTO shippingOrderDTO1;
    ShippingOrderDTO shippingOrderDTO2;
    List<ShippingOrderDTO> shippingOrderDTOS = new ArrayList<>();
    ShippingOrderUpdateDTO shippingOrderDTOExpected;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    AddressDTO addressDTO;
    UserDTO userDTO;
    boolean isEvent;

    @BeforeEach
    void beforeEach() {
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        shippingOrderDTO1 = ShippingOrderDTO.builder()
                .id(1)
                .shippingOrderDate(Instant.now())
                .addressId(addressDTO.getId())
                .orderId(1)
                .merchantOrderId(1)
                .build();

        shippingOrderDTO2 = ShippingOrderDTO.builder()
                .id(2)
                .shippingOrderDate(Instant.now())
                .addressId(addressDTO.getId())
                .orderId(2)
                .merchantOrderId(1)
                .build();

        shippingOrderDTOS.add(shippingOrderDTO1);
        shippingOrderDTOS.add(shippingOrderDTO2);

        shippingOrderUpdateDTO = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO1.getId())
                .shippingOrderDate(shippingOrderDTO1.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO1.getShippingOrderStatus())
                .addressId(shippingOrderDTO1.getAddressId())
                .orderId(shippingOrderDTO1.getOrderId())
                .merchantOrderId(shippingOrderDTO1.getMerchantOrderId())
                .build();

        shippingOrderDTOExpected = shippingOrderUpdateDTO;

        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);

        isEvent = false;
    }

    @Test
    void test_GetAllShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderService.getAllShippingOrders(userDTO)).thenReturn(shippingOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.getAllShippingOrders(userDTO);
        ResponseEntity<List<ShippingOrderDTO>> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTOS);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getAllShippingOrders(userDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_GetUserShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderService.getUserShippingOrders(userDTO)).thenReturn(shippingOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.getUserShippingOrders(userDTO);
        ResponseEntity<List<ShippingOrderDTO>> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTOS);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrders(userDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_GetShippingOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(shippingOrderService.getUserShippingOrder(userDTO, id)).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.getUserShippingOrderById(userDTO, id);
        ResponseEntity<ShippingOrderDTO> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTO1);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, id);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_GetShippingOrderByIdFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(shippingOrderService.getUserShippingOrder(userDTO, id)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.getUserShippingOrderById(userDTO, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }
}
