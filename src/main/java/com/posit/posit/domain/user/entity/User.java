package com.posit.posit.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table (
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "uq_users_phone", columnNames = "phone")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, length = 15)
    private String loginId;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // hash값으로 저장

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private UserStatus status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "birth")
    private String birth;

    @Column(name = "gender")
    private Gender gender;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OwnerProfile ownerProfile;

    public void attachOwnerProfile(OwnerProfile profile) {
        this.ownerProfile = profile;
        profile.setUser(this);
    }

    @PrePersist
    void prePersist() {
        this.status = (this.status == null) ? UserStatus.ACTIVE : this.status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User create(String loginId,
                              String encodedPassword,
                              String name,
                              String phone,
                              UserRole role) {
        User user = new User();
        user.loginId = loginId;
        user.password = encodedPassword;
        user.name = name;
        user.phone = phone;
        user.role = role;
        user.status = UserStatus.ACTIVE;
        return user;
    }
}