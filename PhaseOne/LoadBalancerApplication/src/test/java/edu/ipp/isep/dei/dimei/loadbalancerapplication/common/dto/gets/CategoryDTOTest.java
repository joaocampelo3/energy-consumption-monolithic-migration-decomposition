package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
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
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Category 1";
        description = "Category 1 description";
        userDTO = new UserDTO(1, "admin@email.com", RoleEnum.ADMIN);
        categoryDTOExpected = new CategoryDTO(id, name, description, userDTO);
    }

    @Test
    void test_createCategoryDTO() {
        CategoryDTO categoryDTO = new CategoryDTO(id, name, description, userDTO);

        assertNotNull(categoryDTO);
        assertEquals(id, categoryDTO.getId());
        assertEquals(name, categoryDTO.getName());
        assertEquals(description, categoryDTO.getDescription());
        assertEquals(userDTO, categoryDTO.getUserDTO());
        assertEquals(categoryDTOExpected.hashCode(), categoryDTO.hashCode());
    }

    @Test
    void test_createCategoryDTOBuilder() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .userDTO(userDTO)
                .build();

        assertNotNull(categoryDTO);
        assertEquals(id, categoryDTO.getId());
        assertEquals(name, categoryDTO.getName());
        assertEquals(description, categoryDTO.getDescription());
        assertEquals(userDTO, categoryDTO.getUserDTO());
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
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(categoryDTOExpected.hashCode(), result.hashCode());
        assertEquals(categoryDTOExpected, result);
        assertEquals(categoryDTOExpected.toString(), result.toString());
    }

}
