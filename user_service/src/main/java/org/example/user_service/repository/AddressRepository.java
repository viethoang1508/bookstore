package org.example.user_service.repository;

import org.example.user_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,String> {
    List<Address> findByUserIdAndDeletedFalse(String userId);

    Optional<Address> findByIdAndDeletedFalse(String id);

    List<Address> findByUserIdAndIsDefaultTrueAndDeletedFalse(String userId);

    @Modifying
    @Query("""
        UPDATE Address a
        SET a.isDefault = false
        WHERE a.userId = :userId
    """)
    void clearDefaultByUserId(@Param("userId") String userId);
}
