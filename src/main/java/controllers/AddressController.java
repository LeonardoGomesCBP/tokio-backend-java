package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.AddressDTO;
import dto.ApiResponse;
import dto.PageDTO;
import jakarta.validation.Valid;
import services.AddressService;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;
    
    @GetMapping("/api/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageDTO<AddressDTO>>> getAllAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "zipCode") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        try {
            if (!sortBy.equals("zipCode") && !sortBy.equals("city") && !sortBy.equals("createdAt")) {
                sortBy = "zipCode";
            }
            
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
            
            Page<AddressDTO> addressesPage;
            String successMessage;
            
            if (StringUtils.hasText(search)) {
                addressesPage = addressService.searchAddresses(search, pageable);
                successMessage = "Endereços encontrados para o termo: " + search;
            } else {
                addressesPage = addressService.getAllAddresses(pageable);
                successMessage = "Endereços recuperados";
            }
            
            PageDTO<AddressDTO> pageDTO = PageDTO.from(addressesPage);
            
            return new ResponseEntity<>(
                ApiResponse.success(successMessage, pageDTO),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @RestController
    @RequestMapping("/api/users/{userId}/addresses")
    public static class UserAddressController {

        @Autowired
        private AddressService addressService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
        public ResponseEntity<ApiResponse<AddressDTO>> createAddress(
                @PathVariable Long userId,
                @Valid @RequestBody AddressDTO addressDTO) {
            try {
                AddressDTO createdAddress = addressService.createAddress(userId, addressDTO);
                return new ResponseEntity<>(
                    ApiResponse.success("Endereço criado com sucesso", createdAddress),
                    HttpStatus.CREATED
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
                );
            }
        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
        public ResponseEntity<ApiResponse<PageDTO<AddressDTO>>> getAllUserAddresses(
                @PathVariable Long userId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "zipCode") String sortBy,
                @RequestParam(defaultValue = "asc") String direction,
                @RequestParam(required = false) String search) {
            try {
                if (!sortBy.equals("zipCode") && !sortBy.equals("city") && !sortBy.equals("createdAt")) {
                    sortBy = "zipCode";
                }
                
                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
                
                Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
                
                Page<AddressDTO> addressesPage;
                String successMessage;
                
                if (StringUtils.hasText(search)) {
                    addressesPage = addressService.searchAddressesByUserId(userId, search, pageable);
                    successMessage = "Endereços encontrados para o termo: " + search;
                } else {
                    addressesPage = addressService.getAllAddressesByUserId(userId, pageable);
                    successMessage = "Endereços recuperados com sucesso";
                }
                
                PageDTO<AddressDTO> pageDTO = PageDTO.from(addressesPage);
                
                return new ResponseEntity<>(
                    ApiResponse.success(successMessage, pageDTO),
                    HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        }

        @GetMapping("/{addressId}")
        @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
        public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(
                @PathVariable Long userId,
                @PathVariable Long addressId) {
            try {
                AddressDTO address = addressService.getAddressById(userId, addressId);
                return new ResponseEntity<>(
                    ApiResponse.success("Endereço recuperado com sucesso", address),
                    HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.NOT_FOUND
                );
            }
        }

        @PutMapping("/{addressId}")
        @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
        public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
                @PathVariable Long userId,
                @PathVariable Long addressId,
                @Valid @RequestBody AddressDTO addressDTO) {
            try {
                AddressDTO updatedAddress = addressService.updateAddress(userId, addressId, addressDTO);
                return new ResponseEntity<>(
                    ApiResponse.success("Endereço atualizado com sucesso", updatedAddress),
                    HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
                );
            }
        }

        @DeleteMapping("/{addressId}")
        @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
        public ResponseEntity<ApiResponse<Void>> deleteAddress(
                @PathVariable Long userId,
                @PathVariable Long addressId) {
            try {
                addressService.deleteAddress(userId, addressId);
                return new ResponseEntity<>(
                    ApiResponse.success("Endereço excluído com sucesso", null),
                    HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
                );
            }
        }
    }
} 