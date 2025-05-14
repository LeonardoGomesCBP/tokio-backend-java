package services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dto.AddressDTO;
import dto.CepDTO;
import entities.Address;
import entities.User;
import exception.ResourceNotFoundException;
import repositories.AddressRepository;
import repositories.UserRepository;
import services.CepService;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;
    private Address address;
    private AddressDTO addressDTO;
    private CepDTO cepDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        address = new Address();
        address.setId(1L);
        address.setStreet("Test Street");
        address.setNumber("123");
        address.setComplement("Apt 456");
        address.setNeighborhood("Test Neighborhood");
        address.setCity("Test City");
        address.setState("TS");
        address.setZipCode("12345-678");
        address.setUser(user);
        address.setCreatedAt(now);
        address.setUpdatedAt(now);
        
        addressDTO = new AddressDTO(
            1L,
            "Test Street",
            "123",
            "Apt 456",
            "Test Neighborhood",
            "Test City",
            "TS",
            "12345-678",
            1L,
            now,
            now
        );
        
        cepDTO = new CepDTO(
            "12345-678",
            "Test Street",
            "",  // complemento
            "Test Neighborhood",
            "Test City",
            "TS"
        );
    }

    @Test
    void testCreateAddress() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cepService.getCepInfo("12345-678")).thenReturn(cepDTO);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Act
        AddressDTO result = addressService.createAddress(1L, addressDTO);

        // Assert
        assertNotNull(result);
        assertEquals(addressDTO.id(), result.id());
        assertEquals(addressDTO.street(), result.street());
        assertEquals(addressDTO.number(), result.number());
        assertEquals(addressDTO.zipCode(), result.zipCode());
        assertEquals(addressDTO.userId(), result.userId());
        
        verify(userRepository).findById(1L);
        verify(cepService).getCepInfo("12345-678");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddress_UserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.createAddress(99L, addressDTO);
        });
        
        verify(userRepository).findById(99L);
        verifyNoInteractions(cepService);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetAllAddressesByUserId() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByUserId(1L)).thenReturn(List.of(address));

        // Act
        List<AddressDTO> result = addressService.getAllAddressesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(addressDTO.id(), result.get(0).id());
        assertEquals(addressDTO.street(), result.get(0).street());
        assertEquals(addressDTO.zipCode(), result.get(0).zipCode());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByUserId(1L);
    }

    @Test
    void testGetAllAddressesByUserId_UserNotFound() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.getAllAddressesByUserId(99L);
        });
        
        verify(userRepository).existsById(99L);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetAllAddressesByUserIdPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(List.of(address));
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByUserId(1L, pageable)).thenReturn(addressPage);

        // Act
        Page<AddressDTO> result = addressService.getAllAddressesByUserId(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(addressDTO.id(), result.getContent().get(0).id());
        assertEquals(addressDTO.street(), result.getContent().get(0).street());
        assertEquals(addressDTO.zipCode(), result.getContent().get(0).zipCode());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByUserId(1L, pageable);
    }

    @Test
    void testSearchAddressesByUserId() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(List.of(address));
        String searchTerm = "test";
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByUserIdAndSearchTerm(1L, searchTerm, pageable)).thenReturn(addressPage);

        // Act
        Page<AddressDTO> result = addressService.searchAddressesByUserId(1L, searchTerm, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(addressDTO.id(), result.getContent().get(0).id());
        assertEquals(addressDTO.street(), result.getContent().get(0).street());
        assertEquals(addressDTO.zipCode(), result.getContent().get(0).zipCode());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByUserIdAndSearchTerm(1L, searchTerm, pageable);
    }

    @Test
    void testGetAddressById() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));

        // Act
        AddressDTO result = addressService.getAddressById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(addressDTO.id(), result.id());
        assertEquals(addressDTO.street(), result.street());
        assertEquals(addressDTO.zipCode(), result.zipCode());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void testGetAddressById_NotFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.getAddressById(1L, 99L);
        });
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByIdAndUserId(99L, 1L);
    }

    @Test
    void testUpdateAddress() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Act
        AddressDTO result = addressService.updateAddress(1L, 1L, addressDTO);

        // Assert
        assertNotNull(result);
        assertEquals(addressDTO.id(), result.id());
        assertEquals(addressDTO.street(), result.street());
        assertEquals(addressDTO.zipCode(), result.zipCode());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByIdAndUserId(1L, 1L);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testUpdateAddress_WithNewZipCode() {
        // Arrange
        AddressDTO updatedDTO = new AddressDTO(
            1L,
            "Test Street",
            "123",
            "Apt 456",
            "Test Neighborhood",
            "Test City",
            "TS",
            "87654-321", // different zip code
            1L,
            now,
            now
        );
        
        // Create a new mocked response for the new zip code
        CepDTO newCepDTO = new CepDTO(
            "87654-321",
            "New Street",
            "",  // complemento
            "New Neighborhood",
            "New City",
            "NC"
        );
        
        Address updatedAddress = new Address();
        updatedAddress.setId(1L);
        updatedAddress.setStreet("New Street");
        updatedAddress.setNumber("123");
        updatedAddress.setComplement("Apt 456");
        updatedAddress.setNeighborhood("New Neighborhood");
        updatedAddress.setCity("New City");
        updatedAddress.setState("NC");
        updatedAddress.setZipCode("87654-321");
        updatedAddress.setUser(user);
        updatedAddress.setCreatedAt(now);
        updatedAddress.setUpdatedAt(now);
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(cepService.getCepInfo("87654-321")).thenReturn(newCepDTO);
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);

        // Act
        AddressDTO result = addressService.updateAddress(1L, 1L, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAddress.getId(), result.id());
        assertEquals("New Street", result.street());
        assertEquals("87654-321", result.zipCode());
        assertEquals("New Neighborhood", result.neighborhood());
        assertEquals("New City", result.city());
        assertEquals("NC", result.state());
        
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByIdAndUserId(1L, 1L);
        verify(cepService).getCepInfo("87654-321");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testDeleteAddress() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);

        // Act
        addressService.deleteAddress(1L, 1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(addressRepository).findByIdAndUserId(1L, 1L);
        verify(addressRepository).delete(address);
    }

    @Test
    void testGetAllAddresses() {
        // Arrange
        when(addressRepository.findAll()).thenReturn(List.of(address));

        // Act
        List<AddressDTO> result = addressService.getAllAddresses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(addressDTO.id(), result.get(0).id());
        assertEquals(addressDTO.street(), result.get(0).street());
        assertEquals(addressDTO.zipCode(), result.get(0).zipCode());
        
        verify(addressRepository).findAll();
    }

    @Test
    void testGetAllAddressesPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(List.of(address));
        
        when(addressRepository.findAll(pageable)).thenReturn(addressPage);

        // Act
        Page<AddressDTO> result = addressService.getAllAddresses(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(addressDTO.id(), result.getContent().get(0).id());
        assertEquals(addressDTO.street(), result.getContent().get(0).street());
        assertEquals(addressDTO.zipCode(), result.getContent().get(0).zipCode());
        
        verify(addressRepository).findAll(pageable);
    }

    @Test
    void testSearchAddresses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(List.of(address));
        String searchTerm = "test";
        
        when(addressRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(addressPage);

        // Act
        Page<AddressDTO> result = addressService.searchAddresses(searchTerm, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(addressDTO.id(), result.getContent().get(0).id());
        assertEquals(addressDTO.street(), result.getContent().get(0).street());
        assertEquals(addressDTO.zipCode(), result.getContent().get(0).zipCode());
        
        verify(addressRepository).findBySearchTerm(searchTerm, pageable);
    }
} 