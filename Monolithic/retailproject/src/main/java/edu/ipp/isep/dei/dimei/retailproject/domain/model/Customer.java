package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "customers",
        indexes = {
                @Index(columnList = "customer_name"),
                @Index(columnList = "customer_email")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(name = "customer_name", nullable = false)
    private String name;
    @Size(max = 50)
    @Column(name = "customer_email", nullable = false)
    private String email;
    @Size(max = 250)
    @Column(name = "customer_address", nullable = false)
    private String address;
}
