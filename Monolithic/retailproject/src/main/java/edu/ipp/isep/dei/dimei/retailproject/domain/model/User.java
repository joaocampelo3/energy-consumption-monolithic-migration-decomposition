package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 50)
    @Column(name = "user_firstname", nullable = false)
    private String firstname;
    @Size(max = 50)
    @Column(name = "user_lastname", nullable = false)
    private String lastname;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}
