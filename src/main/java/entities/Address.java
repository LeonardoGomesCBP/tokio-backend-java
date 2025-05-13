package entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;

    @NotEmpty(message = "Number is required")
    private String number;

    private String complement;

    private String neighborhood;

    private String city;

    private String state;

    @NotEmpty(message = "ZipCode is required")
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
} 