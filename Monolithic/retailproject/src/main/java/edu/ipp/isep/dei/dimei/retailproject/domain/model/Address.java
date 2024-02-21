package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String street;
    private String zipCode;
    private String country;
    private String city;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Address(String street, String zipCode, String country, String city) {
        this.street = street;
        this.zipCode = zipCode;
        this.country = country;
        this.city = city;
    }
}