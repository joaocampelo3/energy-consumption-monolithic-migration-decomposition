package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "merchants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"merchant_email"})
        },
        indexes = {
                @Index(columnList = "merchant_name")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 50)
    @Column(name = "merchant_name", nullable = false)
    private String name;
    @Column(name = "merchant_email", nullable = false)
    @Email
    private String email;
    @Column(name = "addressId", nullable = false)
    private int addressId;

    public Merchant(String name, String email, int addressId) {
        this.name = name;
        this.email = email;
        this.addressId = addressId;
    }
}
