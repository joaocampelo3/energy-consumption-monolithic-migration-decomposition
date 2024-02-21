package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "customers")
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
    @Column(name = "customer_first_name", nullable = false)
    private String firstName;
    @Size(max = 50)
    @Column(name = "customer_last_name", nullable = false)
    private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}
