package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
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
    void test_FullCancelShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_FullCancelShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelShippingOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_RejectShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectShippingOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }


    @Test
    void test_ApproveShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).approveShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_ApproveShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).approveShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ApproveShippingOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).approveShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ShippedShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_ShippedShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ShippedShippingOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeliveredShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_DeliveredShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeliveredShippingOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

}
