package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.CategoryService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTests {
    final String exceptionCategoryUnableToPublish = "Unable to create category";
    final String exceptionCategoryNotFound = "Category not found";
    final String exceptionCategoryBadRequest = "Wrong category payload.";
    @InjectMocks
    CategoryController categoryController;
    @Mock
    CategoryService categoryService;
    CategoryDTO categoryDTO1;
    CategoryDTO categoryDTO1Update;
    CategoryDTO categoryDTO2;
    List<CategoryDTO> categories = new ArrayList<>();

    NotFoundException notFoundException;

    @BeforeEach
    void beforeEach() {
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

        // Call the service method that uses the Repository
        ResponseEntity<List<CategoryDTO>> categoryResponseEntity = categoryController.getAllCategories();
        ResponseEntity<List<CategoryDTO>> categoryResponseEntityExpected = ResponseEntity.ok(categories);

        // Perform assertions
        verify(categoryService, atLeastOnce()).getAllCategories();
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);
    }

    @Test
    void test_GetCategoryById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryService.getCategory(id)).thenReturn(categoryDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> categoryResponseEntity = categoryController.getCategoryById(id);
        ResponseEntity<CategoryDTO> categoryResponseEntityExpected = ResponseEntity.ok(categoryDTO1);

        // Perform assertions
        verify(categoryService, atLeastOnce()).getCategory(id);
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);
    }

    @Test
    void test_GetCategoryByIdFailure() throws NotFoundException {
        // Define the behavior of the mock
        int id = 3;
        notFoundException = new NotFoundException("Category not found");
        when(categoryService.getCategory(id)).thenThrow(notFoundException);

        // Call the service method that uses the Repository
        ResponseEntity<Object> categoryResponseEntity = categoryController.getCategoryById(id);
        ResponseEntity<String> categoryResponseEntityExpected = new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(categoryService, atLeastOnce()).getCategory(id);
        assertNotNull(categoryResponseEntity);
        assertEquals(categoryResponseEntityExpected, categoryResponseEntity);
    }
}
