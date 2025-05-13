package dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,
    @NotBlank(message = "Senha é obrigatória") String password,
    List<AddressDTO> addresses
) {
    public CreateUserDTO {
        if (addresses == null) {
            addresses = List.of();
        }
    }
} 