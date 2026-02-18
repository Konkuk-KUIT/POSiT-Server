package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.StoreConvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreConvinceRepository extends JpaRepository<StoreConvince, Long> {

    @Query("""
        select c.displayName
        from StoreConvince sc
        join sc.convince c
        where sc.store.id = :storeId
        order by c.id asc
    """)
    List<String> findDisplayNamesByStoreId(Long storeId);

    void deleteByStoreId(Long storeId);

    List<StoreConvince> findByStoreId(Long storeId);
}