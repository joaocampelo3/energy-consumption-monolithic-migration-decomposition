package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.gets;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.CategoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CategoryDTOTest {
    int id;
    String name;
    String description;
    CategoryDTO categoryDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Category 1";
        description = "Category 1 description";
        categoryDTOExpected = new CategoryDTO(id, name, description);
    }

    @Test
    void test_createCategoryDTO() {
        CategoryDTO categoryDTO = new CategoryDTO(id, name, description);

        assertNotNull(categoryDTO);
        assertEquals(id, categoryDTO.getId());
        assertEquals(name, categoryDTO.getName());
        assertEquals(description, categoryDTO.getDescription());
        assertEquals(categoryDTOExpected.hashCode(), categoryDTO.hashCode());
    }

    @Test
    void test_createCategoryDTOBuilder() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        assertNotNull(categoryDTO);
        assertEquals(id, categoryDTO.getId());
        assertEquals(name, categoryDTO.getName());
        assertEquals(description, categoryDTO.getDescription());
        assertEquals(categoryDTOExpected.hashCode(), categoryDTO.hashCode());
    }

    @Test
    void test_createCategoryDTONoArgsConstructor() {
        CategoryDTO categoryDTO = CategoryDTO.builder().build();
        assertNotNull(categoryDTO);
    }

    @Test
    void test_SetsCategoryDTO() {
        CategoryDTO result = CategoryDTO.builder().build();

        result.setId(id);
        result.setName(name);
        result.setDescription(description);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(categoryDTOExpected.hashCode(), result.hashCode());
        assertEquals(categoryDTOExpected, result);
        assertEquals(categoryDTOExpected.toString(), result.toString());
    }

}
