package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CategoryTest {
    int id;
    String name;
    String description;
    Category categoryExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Category 1";
        description = "Category 1 Description";
        categoryExpected = Category.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }

    @Test
    void test_createCategory() {
        Category category = new Category(id, name, description);

        assertNotNull(category);
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(categoryExpected.hashCode(), category.hashCode());
        assertEquals(categoryExpected, category);
    }

    @Test
    void test_createCategoryBuilder() {
        Category category = Category.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        assertNotNull(category);
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(categoryExpected.hashCode(), category.hashCode());
        assertEquals(categoryExpected, category);
    }
}
