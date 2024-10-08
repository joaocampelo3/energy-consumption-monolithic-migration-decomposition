package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {
    @InjectMocks
    CategoryService categoryService;
    @Mock
    CategoryRepository categoryRepository;
    CategoryDTO categoryDTO1;
    CategoryDTO categoryDTO2;
    List<CategoryDTO> categoryDTOS = new ArrayList<>();
    Category newCategory1;
    Category category1;
    Category category2;
    List<Category> categories = new ArrayList<>();
    CategoryDTO categoryUpdateDTO;

    @BeforeEach
    void beforeEach() {
        newCategory1 = Category.builder()
                .id(0)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        category1 = Category.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        category2 = Category.builder()
                .id(2)
                .name("Category 2")
                .description("Category 2 Description")
                .build();

        categoryDTO1 = new CategoryDTO(category1);
        categoryDTO2 = new CategoryDTO(category2);

        categories.add(category1);
        categories.add(category2);

        categoryDTOS.add(categoryDTO1);
        categoryDTOS.add(categoryDTO2);

        categoryUpdateDTO = categoryDTO1;
    }

    @Test
    void test_GetAllCategories() {
        // Define the behavior of the mock
        when(categoryRepository.findAll()).thenReturn(categories);

        // Call the service method that uses the Repository
        List<CategoryDTO> result = categoryService.getAllCategories();
        List<CategoryDTO> expected = categoryDTOS;

        // Perform assertions
        verify(categoryRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetCategory() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryRepository.findById(id)).thenReturn(Optional.ofNullable(category1));

        // Call the service method that uses the Repository
        CategoryDTO result = categoryService.getCategory(id);
        CategoryDTO expected = categoryDTO1;

        // Perform assertions
        verify(categoryRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateCategory() {
        // Define the behavior of the mock
        when(categoryRepository.save(newCategory1)).thenReturn(category1);

        // Call the service method that uses the Repository
        CategoryDTO result = categoryService.createCategory(categoryDTO1);
        CategoryDTO expected = new CategoryDTO(category1);

        // Perform assertions
        verify(categoryRepository, atLeastOnce()).save(newCategory1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_UpdateCategory() throws NotFoundException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        categoryUpdateDTO.setName(categoryDTO1.getName() + " Changed");
        categoryUpdateDTO.setDescription(categoryDTO1.getDescription() + " Changed");
        when(categoryRepository.findById(id)).thenReturn(Optional.ofNullable(category1));
        when(categoryRepository.save(category1)).thenReturn(category1);

        // Call the service method that uses the Repository
        CategoryDTO result = categoryService.updateCategory(id, categoryUpdateDTO);
        CategoryDTO expected = categoryUpdateDTO;

        // Perform assertions
        verify(categoryRepository, atLeastOnce()).findById(id);
        verify(categoryRepository, atLeastOnce()).save(category1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteCategory() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(categoryRepository.findById(id)).thenReturn(Optional.ofNullable(category1));

        // Call the service method that uses the Repository
        CategoryDTO result = categoryService.deleteCategory(id);
        CategoryDTO expected = categoryDTO1;

        // Perform assertions
        verify(categoryRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
