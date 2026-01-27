package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByStoreIdOrderBySortOrderAsc(Long storeId);
}