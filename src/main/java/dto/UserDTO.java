package dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
    Long id,
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,
    List<AddressDTO> addresses,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 