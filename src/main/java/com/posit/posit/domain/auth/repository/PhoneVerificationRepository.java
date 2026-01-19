package com.posit.posit.domain.auth.repository;

import com.posit.posit.domain.auth.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    Optional<PhoneVerification> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
