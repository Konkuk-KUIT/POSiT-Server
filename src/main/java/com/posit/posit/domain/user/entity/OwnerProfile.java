package com.posit.posit.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "owner_profile",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_owner_profile_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uq_owner_profile_business_number", columnNames = "business_number")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OwnerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "business_number", nullable = false, length = 10)
    private String businessNumber;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_owner_profile_user"))
    @Setter
    private User user;
}

