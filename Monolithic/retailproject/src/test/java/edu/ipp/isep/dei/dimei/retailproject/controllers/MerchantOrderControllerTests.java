package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
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

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantOrderControllerTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    MerchantOrderController merchantOrderController;
    @Mock
    MerchantOrderService merchantOrderService;
    MerchantOrderDTO merchantOrderDTO1;
    MerchantOrderDTO merchantOrderDTO2;
    List<MerchantOrderDTO> merchantOrderDTOS = new ArrayList<>();
    MerchantOrderUpdateDTO merchantOrderDTOExpected;
    MerchantOrderUpdateDTO merchantOrderUpdateDTO;

    @BeforeEach
    void beforeEach() {
        merchantOrderDTO1 = MerchantOrderDTO.builder().id(1).merchantOrderDate(Instant.now()).customerId(1).email("johndoe1234@gmail.com").orderId(1).merchantId(1).build();

        merchantOrderDTO2 = MerchantOrderDTO.builder().id(2).merchantOrderDate(Instant.now()).customerId(1).email("johndoe1234@gmail.com").orderId(1).merchantId(1).build();

        merchantOrderDTOS.add(merchantOrderDTO1);
        merchantOrderDTOS.add(merchantOrderDTO2);

        merchantOrderUpdateDTO = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO1.getId()).merchantOrderDate(merchantOrderDTO1.getMerchantOrderDate()).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).email(merchantOrderDTO1.getEmail()).orderId(merchantOrderDTO1.getOrderId()).merchantId(merchantOrderDTO1.getMerchantId()).build();

        merchantOrderDTOExpected = merchantOrderUpdateDTO;
    }

    @Test
    void test_GetAllMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderService.getAllMerchantOrders()).thenReturn(merchantOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntity = merchantOrderController.getAllMerchantOrders();
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTOS);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getAllMerchantOrders();
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_GetUserMerchantOrders() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantOrderService.getUserMerchantOrders(JwtTokenDummy)).thenReturn(merchantOrderDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.getUserMerchantOrders(JwtTokenDummy);
        ResponseEntity<List<MerchantOrderDTO>> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTOS);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrders(JwtTokenDummy);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_GetMerchantOrderById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantOrderService.getUserMerchantOrder(JwtTokenDummy, id)).thenReturn(merchantOrderDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.getUserMerchantOrderById(JwtTokenDummy, id);
        ResponseEntity<MerchantOrderDTO> merchantOrderResponseEntityExpected = ResponseEntity.ok(merchantOrderDTO1);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(JwtTokenDummy, id);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_FullCancelMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderService.fullCancelMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.fullCancelMerchantOrderById(JwtTokenDummy, id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atMostOnce()).fullCancelMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

    @Test
    void test_RejectMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderService.rejectMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.rejectMerchantOrderById(JwtTokenDummy, id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).rejectMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }


    @Test
    void test_ApproveMerchantOrderById() throws NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrderDTOExpected.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        when(merchantOrderService.approveMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO)).thenReturn(merchantOrderDTOExpected);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantOrderResponseEntity = merchantOrderController.approveMerchantOrderById(JwtTokenDummy, id, merchantOrderUpdateDTO);
        ResponseEntity<MerchantOrderUpdateDTO> merchantOrderResponseEntityExpected = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).approveMerchantOrder(JwtTokenDummy, id, merchantOrderUpdateDTO);
        assertNotNull(merchantOrderResponseEntity);
        assertEquals(merchantOrderResponseEntityExpected, merchantOrderResponseEntity);
    }

}
