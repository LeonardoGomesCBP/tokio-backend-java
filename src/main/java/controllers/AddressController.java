package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.AddressDTO;
import dto.ApiResponse;
import jakarta.validation.Valid;
import services.AddressService;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO createdAddress = addressService.createAddress(userId, addressDTO);
            return new ResponseEntity<>(
                ApiResponse.success("Address created successfully", createdAddress),
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
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAllAddresses(@PathVariable Long userId) {
        try {
            List<AddressDTO> addresses = addressService.getAllAddressesByUserId(userId);
            return new ResponseEntity<>(
                ApiResponse.success("Addresses retrieved successfully", addresses),
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
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        try {
            AddressDTO address = addressService.getAddressById(userId, addressId);
            return new ResponseEntity<>(
                ApiResponse.success("Address retrieved successfully", address),
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
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO updatedAddress = addressService.updateAddress(userId, addressId, addressDTO);
            return new ResponseEntity<>(
                ApiResponse.success("Address updated successfully", updatedAddress),
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
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        try {
            addressService.deleteAddress(userId, addressId);
            return new ResponseEntity<>(
                ApiResponse.success("Address deleted successfully", null),
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