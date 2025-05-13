package dto;

import java.util.List;

public record UserWithAddressesDTO(
    Long id,
    String name,
    String email,
    List<AddressDTO> addresses
) {
    public UserWithAddressesDTO {
        if (addresses == null) {
            throw new IllegalArgumentException("Addresses list cannot be null");
        }
    }
} 