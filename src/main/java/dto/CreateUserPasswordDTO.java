package dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserPasswordDTO(
    @NotBlank(message = "Senha é obrigatória") String password
) {} 