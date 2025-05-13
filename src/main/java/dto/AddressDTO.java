package dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
    Long id,
    String street,
    @NotBlank(message = "Número é obrigatório") String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    @NotBlank(message = "CEP é obrigatório") String zipCode,
    Long userId
) {} 