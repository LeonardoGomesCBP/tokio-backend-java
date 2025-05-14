package services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dto.AddressDTO;

public interface AddressService {
    AddressDTO createAddress(Long userId, AddressDTO addressDTO);
    List<AddressDTO> getAllAddressesByUserId(Long userId);
    Page<AddressDTO> getAllAddressesByUserId(Long userId, Pageable pageable);
    Page<AddressDTO> searchAddressesByUserId(Long userId, String searchTerm, Pageable pageable);
    AddressDTO getAddressById(Long userId, Long addressId);
    AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);

    List<AddressDTO> getAllAddresses();
    Page<AddressDTO> getAllAddresses(Pageable pageable);
    Page<AddressDTO> searchAddresses(String searchTerm, Pageable pageable);
} 