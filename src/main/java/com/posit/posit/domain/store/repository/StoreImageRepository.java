package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    List<StoreImage> findAllByStoreIdOrderBySortOrderAsc(Long storeId);
}
