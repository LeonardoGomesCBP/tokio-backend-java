package repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import config.JpaTestConfig;
import entities.User;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = JpaTestConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        LocalDateTime now = LocalDateTime.now();
        
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setCreatedAt(now);
        user1.setUpdatedAt(now);
        user1.setCreatedBy("test-user");
        
        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        user2.setCreatedAt(now);
        user2.setUpdatedAt(now);
        user2.setCreatedBy("test-user");
        
        User user3 = new User();
        user3.setName("Bob Smith");
        user3.setEmail("bob@example.com");
        user3.setPassword("password123");
        user3.setCreatedAt(now);
        user3.setUpdatedAt(now);
        user3.setCreatedBy("test-user");
        
        userRepository.saveAll(List.of(user1, user2, user3));
    }
    
    @Test
    void testFindByEmail() {
        // Act
        User foundUser = userRepository.findByEmail("john@example.com");
        
        // Assert
        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
    }
    
    @Test
    void testFindByEmail_NotFound() {
        // Act
        User foundUser = userRepository.findByEmail("notfound@example.com");
        
        // Assert
        assertNull(foundUser);
    }
    
    @Test
    void testFindByNameOrEmailContaining() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - busca por nome
        Page<User> resultsByName = userRepository.findByNameOrEmailContaining("Doe", pageable);
        
        // Assert
        assertEquals(2, resultsByName.getContent().size());
        assertTrue(resultsByName.getContent().stream().anyMatch(u -> u.getName().equals("John Doe")));
        assertTrue(resultsByName.getContent().stream().anyMatch(u -> u.getName().equals("Jane Doe")));
        
        // Act - busca por email
        Page<User> resultsByEmail = userRepository.findByNameOrEmailContaining("bob", pageable);
        
        // Assert
        assertEquals(1, resultsByEmail.getContent().size());
        assertEquals("Bob Smith", resultsByEmail.getContent().get(0).getName());
    }
    
    @Test
    void testFindAll_WithSorting() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        
        // Act
        Page<User> results = userRepository.findAll(pageable);
        
        // Assert
        assertEquals(3, results.getContent().size());
        assertEquals("Bob Smith", results.getContent().get(0).getName());
        assertEquals("Jane Doe", results.getContent().get(1).getName());
        assertEquals("John Doe", results.getContent().get(2).getName());
        
        // Act - ordem descendente
        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        results = userRepository.findAll(pageable);
        
        // Assert
        assertEquals(3, results.getContent().size());
        assertEquals("John Doe", results.getContent().get(0).getName());
        assertEquals("Jane Doe", results.getContent().get(1).getName());
        assertEquals("Bob Smith", results.getContent().get(2).getName());
    }
} 