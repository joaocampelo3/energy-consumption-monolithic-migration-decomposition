package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "merchants",
        indexes = {
                @Index(columnList = "merchant_name")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(name = "merchant_name", nullable = false)
    private String name;
    @Size(max = 50)
    @Column(name = "merchant_email", nullable = false)
    private String email;
    @Size(max = 250)
    @Column(name = "merchant_address", nullable = false)
    private String address;
}
