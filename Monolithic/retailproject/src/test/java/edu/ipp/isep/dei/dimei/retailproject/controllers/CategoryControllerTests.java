package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.CategoryService;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTests {

    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    CategoryController categoryController;
    @Mock
    CategoryService categoryService;

    Category category;
    CategoryDTO categoryDTO1;
    CategoryDTO categoryDTO1Update;
    CategoryDTO categoryDTO2;
    List<CategoryDTO> categories = new ArrayList<>();

    NotFoundException notFoundException;

    @BeforeEach
    void beforeEach() {

        category = Category.builder()
                .id(0)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        categoryDTO1 = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        categoryDTO1Update = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description Updated")
                .build();

        categoryDTO2 = CategoryDTO.builder()
                .id(2)
                .name("Category 2")
                .description("Category 2 Description")
                .build();

        categories.add(categoryDTO1);
        categories.add(categoryDTO2);
    }

    @Test
    void test_GetAllCategories() {
        // Define the behavior of the mock
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Call the service method that uses the UserRepository
        ResponseEntity<List<CategoryDTO>> categoryResponseEntity = categoryController.getAllCategories(JwtTokenDummy);
        ResponseEntity<List<CategoryDTO>> categoryResponseEntityExpected = ResponseEntity.ok(categories);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atLeastOnce()).getAllCategories();
    }

    @Test
    void test_GetCategoryById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryService.getCategory(id)).thenReturn(categoryDTO1);

        // Call the service method that uses the UserRepository
        ResponseEntity<?> categoryResponseEntity = categoryController.getCategoryById(JwtTokenDummy, id);
        ResponseEntity<CategoryDTO> categoryResponseEntityExpected = ResponseEntity.ok(categoryDTO1);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atLeastOnce()).getCategory(id);
    }

    @Test
    void test_GetCategoryByIdFailure() throws NotFoundException {
        // Define the behavior of the mock
        int id = 3;
        notFoundException = new NotFoundException("Category not found");
        when(categoryService.getCategory(id)).thenThrow(notFoundException);

        // Call the service method that uses the UserRepository
        ResponseEntity<?> categoryResponseEntity = categoryController.getCategoryById(JwtTokenDummy, id);
        ResponseEntity<String> categoryResponseEntityExpected = new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atLeastOnce()).getCategory(id);
    }

    @Test
    void test_CreateCategory() {
        // Define the behavior of the mock
        when(categoryService.createCategory(categoryDTO1)).thenReturn(categoryDTO1);

        // Call the service method that uses the UserRepository
        ResponseEntity<?> categoryResponseEntity = categoryController.createCategory(categoryDTO1);
        ResponseEntity<?> categoryResponseEntityExpected = new ResponseEntity<>(categoryDTO1, HttpStatus.CREATED);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atMostOnce()).createCategory(categoryDTO1);
    }

    @Test
    void test_UpdateCategory() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryService.updateCategory(id, categoryDTO1)).thenReturn(categoryDTO1Update);

        // Call the service method that uses the UserRepository
        ResponseEntity<?> categoryResponseEntity = categoryController.updateCategory(JwtTokenDummy, id, categoryDTO1);
        ResponseEntity<?> categoryResponseEntityExpected = new ResponseEntity<>(categoryDTO1Update, HttpStatus.ACCEPTED);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atMostOnce()).updateCategory(id, categoryDTO1);
    }

    @Test
    void test_DeleteCategory() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryService.deleteCategory(id)).thenReturn(categoryDTO1);

        // Call the service method that uses the UserRepository
        ResponseEntity<?> categoryResponseEntity = categoryController.deleteCategory(JwtTokenDummy, id);
        ResponseEntity<?> categoryResponseEntityExpected = new ResponseEntity<>(categoryDTO1, HttpStatus.OK);

        // Perform assertions
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);

        verify(categoryService, atMostOnce()).deleteCategory(id);
    }
}
