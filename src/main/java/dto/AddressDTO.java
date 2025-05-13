package dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
    Long id,
    String street,
    @NotBlank(message = "Number is required") String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    @NotBlank(message = "ZipCode is required") String zipCode,
    Long userId
) {} 