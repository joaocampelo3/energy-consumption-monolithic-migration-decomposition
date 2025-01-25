package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.*;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ORDER_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    UserDTO userDTO;

    CategoryDTO categoryDTO;
    HttpHeaders headers;
    HttpEntity<CategoryDTO> requestEntity;
    HttpEntity<UserDTO> requestUserEntity;
    ParameterizedTypeReference<Object> responseType;
    LinkedHashMap<String, Object> mockUserControllerResponse;
    ResponseEntity<Object> mockCategoryResponseEntity;
    List<CategoryDTO> categoryDTOS = new ArrayList<>();

    @Mock
    private RestTemplate restTemplate = new RestTemplate();
    @InjectMocks
    private CategoryController categoryController;
    @Mock
    UserController userController;

    @BeforeEach
    void beforeEach() {
        userDTO = new UserDTO(1, "user_email@email.com", RoleEnum.USER);
        categoryDTO = new CategoryDTO(1, "Street 1", "1234", userDTO);

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        mockUserControllerResponse = new LinkedHashMap<>();
        mockUserControllerResponse.put("userId", userDTO.getUserId());
        mockUserControllerResponse.put("email", userDTO.getEmail());
        mockUserControllerResponse.put("role", userDTO.getRole().name());

        categoryDTOS.add(categoryDTO);

        responseType = new ParameterizedTypeReference<>() {
        };
    }

    @Test
    void test_GetAllCategories() {
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTOS, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL + "/all", HttpMethod.GET, requestEntity, responseType)).thenReturn(mockCategoryResponseEntity);

        ResponseEntity<Object> response = categoryController.getAllCategories(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL + "/all", HttpMethod.GET, requestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTOS, response.getBody());
    }

    @Test
    void test_GetAllCategoriesFail1() {
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTOS, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL + "/all", HttpMethod.GET, requestEntity, responseType)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = categoryController.getAllCategories(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL + "/all", HttpMethod.GET, requestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllCategoriesFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = categoryController.getAllCategories(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetCategoryById() {
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        headers.setContentType(MediaType.APPLICATION_JSON);
        requestUserEntity = new HttpEntity<>(userDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL + "/" + categoryDTO.getId(), HttpMethod.GET, requestUserEntity, Object.class)).thenReturn(mockCategoryResponseEntity);

        ResponseEntity<Object> response = categoryController.getCategoryById(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL + "/" + categoryDTO.getId(), HttpMethod.GET, requestUserEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void test_GetCategoryByIdFail1() {
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        headers.setContentType(MediaType.APPLICATION_JSON);
        requestUserEntity = new HttpEntity<>(userDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL + "/" + categoryDTO.getId(), HttpMethod.GET, requestUserEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = categoryController.getCategoryById(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL + "/" + categoryDTO.getId(), HttpMethod.GET, requestUserEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetCategoryByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = categoryController.getCategoryById(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateCategory() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(categoryDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL, HttpMethod.POST, requestEntity, Object.class)).thenReturn(mockCategoryResponseEntity);

        ResponseEntity<Object> response = categoryController.createCategory(jwtTokenDummy, categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL, HttpMethod.POST, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void test_CreateCategoryFail1() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(categoryDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(CATEGORY_URL, HttpMethod.POST, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = categoryController.createCategory(jwtTokenDummy, categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(CATEGORY_URL, HttpMethod.POST, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_CreateCategoryFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = categoryController.createCategory(jwtTokenDummy, categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_UpdateCategory() {
        String url = CATEGORY_URL + "/{id}";
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(categoryDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class, categoryDTO.getId())).thenReturn(mockCategoryResponseEntity);

        ResponseEntity<Object> response = categoryController.updateCategory(jwtTokenDummy, categoryDTO.getId(), categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, requestEntity, Object.class, categoryDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void test_UpdateCategoryFail1() {
        String url = CATEGORY_URL + "/{id}";
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestEntity = new HttpEntity<>(categoryDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class, categoryDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = categoryController.updateCategory(jwtTokenDummy, categoryDTO.getId(), categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, requestEntity, Object.class, categoryDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_UpdateCategoryFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = categoryController.updateCategory(jwtTokenDummy, categoryDTO.getId(), categoryDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_DeleteCategory() {
        String url = CATEGORY_URL + "/" + categoryDTO.getId();
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestUserEntity = new HttpEntity<>(userDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestUserEntity, Object.class)).thenReturn(mockCategoryResponseEntity);

        ResponseEntity<Object> response = categoryController.deleteCategory(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestUserEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void test_DeleteCategoryFail1() {
        String url = CATEGORY_URL + "/" + categoryDTO.getId();
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        requestUserEntity = new HttpEntity<>(userDTO, headers);
        mockCategoryResponseEntity = new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestUserEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = categoryController.deleteCategory(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestUserEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_DeleteCategoryFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = categoryController.deleteCategory(jwtTokenDummy, categoryDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}