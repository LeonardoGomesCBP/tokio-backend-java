package services;

import java.util.List;

import dto.AddressDTO;

public interface AddressService {
    AddressDTO createAddress(Long userId, AddressDTO addressDTO);
    List<AddressDTO> getAllAddressesByUserId(Long userId);
    AddressDTO getAddressById(Long userId, Long addressId);
    AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);
} 