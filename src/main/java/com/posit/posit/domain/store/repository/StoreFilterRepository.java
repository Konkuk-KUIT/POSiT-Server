package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.StoreCategory;
import com.posit.posit.domain.store.entity.StoreFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreFilterRepository extends JpaRepository<StoreFilter, Long> {
    @Query("""
        select f.code
        from StoreFilter sf
        join sf.filter f
        where sf.store.id = :storeId
          and f.category = 'TYPE'
        order by f.id asc
    """)
    Optional<String> findFirstTypeCodeByStoreId(Long storeId);

    Optional<String> findByCategoryAndCode(String type, StoreCategory category);
}
