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
@Table(name = "addresses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"street", "zipCode", "city", "country", "user_id"})
        }
)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String street;
    private String zipCode;
    private String city;
    private String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String zipCode, String country, String city) {
        this.street = street;
        this.zipCode = zipCode;
        this.country = country;
        this.city = city;
    }
}