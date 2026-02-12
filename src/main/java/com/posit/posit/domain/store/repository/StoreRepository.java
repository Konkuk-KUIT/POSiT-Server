package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.Store;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByBusinessNumber(String businessNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Store s where s.businessNumber = :businessNumber")
    Optional<Store> findByBusinessNumberForUpdate(@Param("businessNumber") String businessNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Store s where s.businessNumber = :businessNumber and s.owner is null")
    Optional<Store> findUnassignedByBusinessNumberForUpdate(@Param("businessNumber") String businessNumber);

    Optional<Store> findByOwnerId(Long ownerId);

}
