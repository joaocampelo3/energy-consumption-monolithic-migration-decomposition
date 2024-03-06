package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    private int id;
    @Size(max = 50)
    @Column(name = "merchant_name", nullable = false)
    private String name;
    @Column(name = "merchant_email", unique = true)
    @Email
    private String email;

    @OneToOne(optional = false)
    private Address address;

    public Merchant(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }
}
