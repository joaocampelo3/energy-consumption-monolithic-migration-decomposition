package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShippingOrderControllerTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
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
                .shippingOrderDate(LocalDateTime.now())
                .addressDTO(addressDTO)
                .orderId(1)
                .merchantOrderId(1)
                .build();

        shippingOrderDTO2 = ShippingOrderDTO.builder()
                .id(2)
                .shippingOrderDate(LocalDateTime.now())
                .addressDTO(addressDTO)
                .orderId(2)
                .merchantOrderId(1)
                .build();

        shippingOrderDTOS.add(shippingOrderDTO1);
        shippingOrderDTOS.add(shippingOrderDTO2);

        shippingOrderUpdateDTO = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO1.getId())
                .shippingOrderDate(shippingOrderDTO1.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO1.getShippingOrderStatus())
                .addressDTO(shippingOrderDTO1.getAddressDTO())
                .orderId(shippingOrderDTO1.getOrderId())
                .merchantOrderId(shippingOrderDTO1.getMerchantOrderId())
                .build();

        shippingOrderDTOExpected = shippingOrderUpdateDTO;
    }

    @Test
    void test_GetAllShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderService.getAllShippingOrders()).thenReturn(shippingOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.getAllShippingOrders();
        ResponseEntity<List<ShippingOrderDTO>> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTOS);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getAllShippingOrders();
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_GetUserShippingOrders() {
        // Define the behavior of the mock
        when(shippingOrderService.getUserShippingOrders(JwtTokenDummy)).thenReturn(shippingOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.getUserShippingOrders(JwtTokenDummy);
        ResponseEntity<List<ShippingOrderDTO>> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTOS);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrders(JwtTokenDummy);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_GetShippingOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(shippingOrderService.getUserShippingOrder(JwtTokenDummy, id)).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.getUserShippingOrderById(JwtTokenDummy, id);
        ResponseEntity<ShippingOrderDTO> shippingOrderResponseEntityExpected = ResponseEntity.ok(shippingOrderDTO1);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(JwtTokenDummy, id);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_FullCancelShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.fullCancelShippingOrderById(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_RejectShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.rejectShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.rejectShippingOrderById(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }


    @Test
    void test_ApproveShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);

        when(shippingOrderService.approveShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.approveShippingOrderById(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).approveShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_ShippedShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);

        when(shippingOrderService.shippedShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.shippedShippingOrderById(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).shippedShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_DeliveredShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);

        when(shippingOrderService.deliveredShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<?> shippingOrderResponseEntity = shippingOrderController.deliveredShippingOrderById(JwtTokenDummy, id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).deliveredShippingOrder(JwtTokenDummy, id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

}
