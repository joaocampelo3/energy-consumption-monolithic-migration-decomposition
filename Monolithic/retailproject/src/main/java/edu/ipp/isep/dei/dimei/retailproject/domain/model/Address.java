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
                @UniqueConstraint(columnNames = {"street", "zip_code", "city", "country", "user_id"})
        },
        indexes = {
                @Index(columnList = "street, zip_code, city, country")
        }
)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String zipCode;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String country;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String zipCode, String country, String city) {
        this.street = street;
        this.zipCode = zipCode;
        this.country = country;
        this.city = city;
    }
}