package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CategoryDTOTest {
    int id;
    String name;
    String description;
    CategoryDTO categoryDTOExpected;
    Category categoryExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Category 1";
        description = "Category 1 description";
        categoryDTOExpected = new CategoryDTO(id, name, description);
        categoryExpected = Category.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
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
    void test_dtoToEntityCategoryDTO() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        Category category = categoryDTO.dtoToEntity();

        assertNotNull(category);
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(categoryExpected.hashCode(), category.hashCode());
    }

}
