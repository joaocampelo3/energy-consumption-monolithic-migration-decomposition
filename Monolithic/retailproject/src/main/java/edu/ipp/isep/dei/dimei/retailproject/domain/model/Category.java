package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "categories",
        indexes = {
                @Index(columnList = "category_name")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 50)
    @Column(name = "category_name", nullable = false)
    private String name;
    @Size(max = 250)
    @Column(name = "category_description", nullable = false)
    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
