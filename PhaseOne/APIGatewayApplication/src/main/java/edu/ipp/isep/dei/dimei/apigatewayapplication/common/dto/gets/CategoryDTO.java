package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets;

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
}
