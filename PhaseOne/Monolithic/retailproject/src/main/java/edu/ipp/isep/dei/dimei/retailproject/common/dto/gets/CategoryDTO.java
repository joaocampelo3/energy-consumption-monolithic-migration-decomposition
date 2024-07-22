package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class CategoryDTO {
    private int id;
    private String name;
    private String description;
    private UserDTO userDTO;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public Category dtoToEntity() {
        return Category.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }
}
