package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.Filter;
import com.posit.posit.domain.store.entity.StoreFilterCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilterRepository extends JpaRepository<Filter, Long> {
    Optional<Filter> findByCategoryAndCode(String category, StoreFilterCategory code);
}
