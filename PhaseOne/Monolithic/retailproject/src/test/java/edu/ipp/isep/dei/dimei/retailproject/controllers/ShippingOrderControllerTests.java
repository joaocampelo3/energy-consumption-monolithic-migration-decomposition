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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void test_FullCancelShippingOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_FullCancelShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.fullCancelShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.fullCancelShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).fullCancelShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_RejectShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.rejectShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.rejectShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).rejectShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).approveShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_ApproveShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).approveShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.approveShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.approveShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).approveShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_ShippedShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.shippedShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.shippedShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).shippedShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO)).thenReturn(shippingOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> shippingOrderResponseEntity = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<ShippingOrderUpdateDTO> shippingOrderResponseEntityExpected = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(shippingOrderService, atLeastOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(shippingOrderResponseEntity);
        assertEquals(shippingOrderResponseEntityExpected, shippingOrderResponseEntity);
    }

    @Test
    void test_DeliveredShippingOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        shippingOrderDTOExpected.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionShippingOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO);
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

        when(shippingOrderService.deliveredShippingOrder(id, shippingOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionShippingOrderBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = shippingOrderController.deliveredShippingOrderById(id, shippingOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionShippingOrderBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(shippingOrderService, atMostOnce()).deliveredShippingOrder(id, shippingOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

}
