package com.posit.posit.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "convince",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_convince_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_convince_display", columnNames = "display_name")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Convince {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", length = 20, nullable = false)
    private String displayName;

    @Column(name = "code", length = 20, nullable = false)
    private String code;
}