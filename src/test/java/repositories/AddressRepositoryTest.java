package repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import config.JpaTestConfig;
import entities.Address;
import entities.User;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = JpaTestConfig.class)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User user1;
    private User user2;
    private Address address1;
    private Address address2;
    private Address address3;
    
    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
        
        LocalDateTime now = LocalDateTime.now();
        
        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1.setPassword("password123");
        user1.setCreatedAt(now);
        user1.setUpdatedAt(now);
        user1.setCreatedBy("test-user");
        
        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2.setPassword("password123");
        user2.setCreatedAt(now);
        user2.setUpdatedAt(now);
        user2.setCreatedBy("test-user");
        
        userRepository.save(user1);
        userRepository.save(user2);
        
        address1 = new Address();
        address1.setStreet("Rua A");
        address1.setNumber("123");
        address1.setComplement("Apto 101");
        address1.setNeighborhood("Bairro Centro");
        address1.setCity("São Paulo");
        address1.setState("SP");
        address1.setZipCode("01001-000");
        address1.setUser(user1);
        address1.setCreatedAt(now);
        address1.setUpdatedAt(now);
        address1.setCreatedBy("test-user");
        
        address2 = new Address();
        address2.setStreet("Avenida B");
        address2.setNumber("456");
        address2.setComplement("Casa");
        address2.setNeighborhood("Bairro Norte");
        address2.setCity("Rio de Janeiro");
        address2.setState("RJ");
        address2.setZipCode("20000-000");
        address2.setUser(user1);
        address2.setCreatedAt(now);
        address2.setUpdatedAt(now);
        address2.setCreatedBy("test-user");
        
        address3 = new Address();
        address3.setStreet("Rua C");
        address3.setNumber("789");
        address3.setComplement("");
        address3.setNeighborhood("Bairro Sul");
        address3.setCity("Belo Horizonte");
        address3.setState("MG");
        address3.setZipCode("30000-000");
        address3.setUser(user2);
        address3.setCreatedAt(now);
        address3.setUpdatedAt(now);
        address3.setCreatedBy("test-user");
        
        addressRepository.saveAll(List.of(address1, address2, address3));
    }
    
    @Test
    void testFindByUserId() {
        List<Address> addresses = addressRepository.findByUserId(user1.getId());
        
        assertEquals(2, addresses.size());
        assertTrue(addresses.stream().anyMatch(a -> a.getStreet().equals("Rua A")));
        assertTrue(addresses.stream().anyMatch(a -> a.getStreet().equals("Avenida B")));
    }
    
    @Test
    void testFindByUserId_Paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Address> addressPage = addressRepository.findByUserId(user1.getId(), pageable);
        
        assertEquals(2, addressPage.getContent().size());
        assertEquals(2, addressPage.getTotalElements());
    }
    
    @Test
    void testFindByIdAndUserId() {
        Optional<Address> foundAddress = addressRepository.findByIdAndUserId(address1.getId(), user1.getId());
        
        assertTrue(foundAddress.isPresent());
        assertEquals("Rua A", foundAddress.get().getStreet());
        assertEquals("São Paulo", foundAddress.get().getCity());
    }
    
    @Test
    void testFindByIdAndUserId_NotFound() {
        Optional<Address> foundAddress = addressRepository.findByIdAndUserId(address3.getId(), user1.getId());
        
        assertFalse(foundAddress.isPresent());
    }
    
    @Test
    void testFindBySearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Address> resultsCity = addressRepository.findBySearchTerm("São Paulo", pageable);
        
        assertEquals(1, resultsCity.getContent().size());
        assertEquals("Rua A", resultsCity.getContent().get(0).getStreet());
        
        Page<Address> resultsState = addressRepository.findBySearchTerm("RJ", pageable);
        
        assertEquals(1, resultsState.getContent().size());
        assertEquals("Avenida B", resultsState.getContent().get(0).getStreet());
        
        Page<Address> resultsStreet = addressRepository.findBySearchTerm("Avenida", pageable);
        
        assertEquals(1, resultsStreet.getContent().size());
        assertEquals("Avenida B", resultsStreet.getContent().get(0).getStreet());
        
        Page<Address> resultsCep = addressRepository.findBySearchTerm("01001", pageable);
        
        assertEquals(1, resultsCep.getContent().size());
        assertEquals("Rua A", resultsCep.getContent().get(0).getStreet());
    }
    
    @Test
    void testFindByUserIdAndSearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Address> results = addressRepository.findByUserIdAndSearchTerm(user1.getId(), "Centro", pageable);
        
        assertEquals(1, results.getContent().size());
        assertEquals("Rua A", results.getContent().get(0).getStreet());
        
        Page<Address> noResults = addressRepository.findByUserIdAndSearchTerm(user1.getId(), "Belo Horizonte", pageable);
        
        assertEquals(0, noResults.getContent().size());
    }
} 