package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.Convince;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvinceRepository extends JpaRepository<Convince, Long> {
    Optional<Convince> findByCode(String code);
}
