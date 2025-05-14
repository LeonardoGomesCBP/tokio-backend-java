package repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Page<Address> findByUserId(Long userId, Pageable pageable);
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT a FROM Address a WHERE " +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.state) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.street) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.neighborhood) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.zipCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Address> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT a FROM Address a WHERE " +
           "a.user.id = :userId AND (" +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.state) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.street) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.neighborhood) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.zipCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Address> findByUserIdAndSearchTerm(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);
} 