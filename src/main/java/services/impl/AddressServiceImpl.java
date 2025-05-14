package services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.AddressDTO;
import dto.CepDTO;
import entities.Address;
import entities.User;
import exception.ResourceNotFoundException;
import repositories.AddressRepository;
import repositories.UserRepository;
import services.AddressService;
import services.CepService;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CepService cepService;

    @Override
    @Transactional
    public AddressDTO createAddress(Long userId, AddressDTO addressDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        CepDTO cepInfo = cepService.getCepInfo(addressDTO.zipCode());
        
        Address address = new Address();
        address.setStreet(cepInfo.logradouro());
        address.setNumber(addressDTO.number());
        address.setComplement(addressDTO.complement());
        address.setNeighborhood(cepInfo.bairro());
        address.setCity(cepInfo.localidade());
        address.setState(cepInfo.uf());
        address.setZipCode(addressDTO.zipCode());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return new AddressDTO(
            savedAddress.getId(),
            savedAddress.getStreet(),
            savedAddress.getNumber(),
            savedAddress.getComplement(),
            savedAddress.getNeighborhood(),
            savedAddress.getCity(),
            savedAddress.getState(),
            savedAddress.getZipCode(),
            userId,
            savedAddress.getCreatedAt(),
            savedAddress.getUpdatedAt()
        );
    }

    @Override
    public List<AddressDTO> getAllAddressesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        List<Address> addresses = addressRepository.findByUserId(userId);

        return addresses.stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser().getId(),
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<AddressDTO> getAllAddressesByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        Page<Address> addressPage = addressRepository.findByUserId(userId, pageable);
        
        List<AddressDTO> addressDTOs = addressPage.getContent().stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser().getId(),
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
        
        return new PageImpl<>(addressDTOs, pageable, addressPage.getTotalElements());
    }

    @Override
    public AddressDTO getAddressById(Long userId, Long addressId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        return new AddressDTO(
            address.getId(),
            address.getStreet(),
            address.getNumber(),
            address.getComplement(),
            address.getNeighborhood(),
            address.getCity(),
            address.getState(),
            address.getZipCode(),
            userId,
            address.getCreatedAt(),
            address.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (!address.getZipCode().equals(addressDTO.zipCode())) {
            CepDTO cepInfo = cepService.getCepInfo(addressDTO.zipCode());
            
            address.setStreet(cepInfo.logradouro());
            address.setNeighborhood(cepInfo.bairro());
            address.setCity(cepInfo.localidade());
            address.setState(cepInfo.uf());
            address.setZipCode(addressDTO.zipCode());
        }

        address.setNumber(addressDTO.number());
        address.setComplement(addressDTO.complement());

        Address updatedAddress = addressRepository.save(address);

        return new AddressDTO(
            updatedAddress.getId(),
            updatedAddress.getStreet(),
            updatedAddress.getNumber(),
            updatedAddress.getComplement(),
            updatedAddress.getNeighborhood(),
            updatedAddress.getCity(),
            updatedAddress.getState(),
            updatedAddress.getZipCode(),
            userId,
            updatedAddress.getCreatedAt(),
            updatedAddress.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressRepository.delete(address);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser() != null ? address.getUser().getId() : null,
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<AddressDTO> getAllAddresses(Pageable pageable) {
        Page<Address> addressPage = addressRepository.findAll(pageable);
        
        List<AddressDTO> addressDTOs = addressPage.getContent().stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser() != null ? address.getUser().getId() : null,
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
        
        return new PageImpl<>(addressDTOs, pageable, addressPage.getTotalElements());
    }

    @Override
    public Page<AddressDTO> searchAddresses(String searchTerm, Pageable pageable) {
        Page<Address> addressPage = addressRepository.findBySearchTerm(searchTerm, pageable);
        
        List<AddressDTO> addressDTOs = addressPage.getContent().stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser() != null ? address.getUser().getId() : null,
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
        
        return new PageImpl<>(addressDTOs, pageable, addressPage.getTotalElements());
    }
    
    @Override
    public Page<AddressDTO> searchAddressesByUserId(Long userId, String searchTerm, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        Page<Address> addressPage = addressRepository.findByUserIdAndSearchTerm(userId, searchTerm, pageable);
        
        List<AddressDTO> addressDTOs = addressPage.getContent().stream()
            .map(address -> new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser().getId(),
                address.getCreatedAt(),
                address.getUpdatedAt()
            ))
            .collect(Collectors.toList());
        
        return new PageImpl<>(addressDTOs, pageable, addressPage.getTotalElements());
    }
}