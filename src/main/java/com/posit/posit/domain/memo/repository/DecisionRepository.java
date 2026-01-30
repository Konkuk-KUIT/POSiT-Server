package com.posit.posit.domain.memo.repository;

import com.posit.posit.domain.memo.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
}