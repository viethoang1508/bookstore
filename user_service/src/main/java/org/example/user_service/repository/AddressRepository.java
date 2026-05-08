package org.example.user_service.repository;

import org.example.user_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,String> {
    List<Address> findByUserIdAndIsDeletedFalse(String userId);

    Optional<Address> findByIdAndIsDeletedFalse(String id);

    List<Address> findByUserIdAndIsDefaultTrueAndIsDeletedFalse(String userId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Address a
        SET a.isDefault = false
        WHERE a.userId = :userId
          AND a.isDeleted = false
    """)
    void clearDefaultByUserId(@Param("userId") String userId);
}
