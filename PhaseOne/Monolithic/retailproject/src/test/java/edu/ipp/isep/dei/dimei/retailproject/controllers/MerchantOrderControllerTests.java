package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantOrderService;
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
class MerchantOrderControllerTests {
    final String exceptionMerchantOrderNotFound = "Merchant Order not found.";
    final String exceptionMerchantOrderBadPayload = "Wrong merchant order payload.";
    @InjectMocks
    MerchantOrderController merchantOrderController;
    @Mock
    MerchantOrderService merchantOrderService;
    MerchantOrderDTO merchantOrderDTO1;
    MerchantOrderDTO merchantOrderDTO2;
    List<MerchantOrderDTO> merchantOrderDTOS = new ArrayList<>();
    MerchantOrderUpdateDTO merchantOrderDTOExpected;
    MerchantOrderUpdateDTO merchantOrderUpdateDTO;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        merchantOrderDTO1 = MerchantOrderDTO.builder().id(1).merchantOrderDate(Instant.now()).customerId(1).email("johndoe1234@gmail.com").orderId(1).merchantId(1).build();

        merchantOrderDTO2 = MerchantOrderDTO.builder().id(2).merchantOrderDate(Instant.now()).customerId(1).email("johndoe1234@gmail.com").orderId(1).merchantId(1).build();

        merchantOrderDTOS.add(merchantOrderDTO1);
        merchantOrderDTOS.add(merchantOrderDTO2);

        merchantOrderUpdateDTO = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO1.getId()).merchantOrderDate(merchantOrderDTO1.getMerchantOrderDate()).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).email(merchantOrderDTO1.getEmail()).orderId(merchantOrderDTO1.getOrderId()).merchantId(merchantOrderDTO1.getMerchantId()).build();

        merchantOrderDTOExpected = merchantOrderUpdateDTO;

        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);
    }

    @Test
    void test_GetAllMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderService.getAllMerchantOrders(userDTO)).thenReturn(merchantOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntity = merchantOrderController.getAllMerchantOrders(userDTO);
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTOS);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getAllMerchantOrders(userDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_GetUserMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderService.getUserMerchantOrders(userDTO)).thenReturn(merchantOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.getUserMerchantOrders(userDTO);
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTOS);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrders(userDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_GetMerchantOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantOrderService.getUserMerchantOrder(userDTO, id)).thenReturn(merchantOrderDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.getUserMerchantOrderById(userDTO, id);
        ResponseEntity<MerchantOrderDTO> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTO1);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, id);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_GetMerchantOrderByIdFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantOrderService.getUserMerchantOrder(userDTO, id)).thenThrow(new NotFoundException(exceptionMerchantOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.getUserMerchantOrderById(userDTO, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.fullCancelMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atMostOnce()).fullCancelMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_FullCancelMerchantOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionMerchantOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.fullCancelMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_FullCancelMerchantOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionMerchantOrderBadPayload));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.fullCancelMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderBadPayload, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.rejectMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_RejectMerchantOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionMerchantOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.rejectMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RejectMerchantOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionMerchantOrderBadPayload));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.rejectMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderBadPayload, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }


    @Test
    void test_ApproveMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        when(merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.approveMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).approveMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_ApproveMerchantOrderByIdFail1() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        when(merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new NotFoundException(exceptionMerchantOrderNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.approveMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).approveMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_ApproveMerchantOrderByIdFail2() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        when(merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO)).thenThrow(new BadPayloadException(exceptionMerchantOrderBadPayload));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantOrderController.approveMerchantOrderById(id, merchantOrderUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantOrderBadPayload, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).approveMerchantOrder(id, merchantOrderUpdateDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

}
