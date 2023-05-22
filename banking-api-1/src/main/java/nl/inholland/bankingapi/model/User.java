package nl.inholland.bankingapi.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String postalCode;
    private String address;
    private String city;
    private String phoneNumber;
    private UserType userType;
    private Boolean hasAccount;

}