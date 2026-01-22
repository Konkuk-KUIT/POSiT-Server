package com.posit.posit.domain.user.repository;

import com.posit.posit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByPhone(String phone);
    Optional<User> findByLoginId(String loginId);
}
