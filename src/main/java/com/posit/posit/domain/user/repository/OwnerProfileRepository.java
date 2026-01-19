package com.posit.posit.domain.user.repository;

import com.posit.posit.domain.user.entity.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Long> {
    boolean existsByBusinessNumber(String businessNumber);
}

