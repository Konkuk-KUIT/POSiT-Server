package com.posit.posit.domain.store.entity;

import com.posit.posit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "store",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_store_business_number", columnNames = "business_number")
        },
        indexes = {
                @Index(name = "idx_store_owner", columnList = "owner_id"),
                @Index(name = "idx_store_geo", columnList = "latitude, longitude"),
                @Index(name = "idx_store_business_number", columnList = "business_number")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "owner_id", nullable = true, foreignKey = @ForeignKey(name = "fk_store_owner"))
    private User owner;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "phone", length = 15, nullable = false)
    private String phone;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private StoreType category;

    @Column(name = "open_time", length = 20, nullable = false)
    private String openTime; // "HH:mm-HH:mm"

    @Enumerated(EnumType.STRING)
    @Column(name = "not_open")
    private Weekday notOpen;

    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private java.math.BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private java.math.BigDecimal longitude;

    @Column(name = "road_address", length = 255, nullable = false)
    private String roadAddress;

    @Column(name = "lot_address", length = 255)
    private String lotAddress;

    @Column(name = "sns_link", length = 255)
    private String snsLink;

    @Column(name = "coupon_pin_hash", length = 255, nullable = true)
    private String couponPinHash;

    @Column(name = "business_number", length = 10, nullable = false)
    private String businessNumber;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<StoreImage> images = new ArrayList<>();

    public void addImage(StoreImage image) {
        images.add(image);
        image.setStore(this);
    }

    public void removeImage(StoreImage image) {
        images.remove(image);
        image.setStore(null);
    }

    /** 대표 썸네일 규칙: sortOrder가 가장 작은 이미지 */
    public StoreImage getPrimaryImageOrNull() {
        return images.stream()
                .min(java.util.Comparator.comparing(StoreImage::getSortOrder))
                .orElse(null);
    }

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setStore(this);
    }

    public void removeMenu(Menu menu) {
        menus.remove(menu);
        menu.setStore(null);
    }

    /**
     * 사장님 회원가입 시, 사업자번호로 매칭된 "미리 생성된" 매장에 사장님을 할당합니다.
     * - 이미 owner가 있으면 재할당 불가
     * - couponPinHash는 해시값(BCrypt 등)으로 저장되어야 함
     */
    public void assignOwner(User owner, String couponPinHash) {
        if (owner == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        if (this.owner != null) {
            throw new IllegalStateException("이미 사장님이 등록된 매장입니다.");
        }
        if (couponPinHash == null || couponPinHash.isBlank()) {
            throw new IllegalArgumentException("couponPinHash must not be blank");
        }
        this.owner = owner;
        this.couponPinHash = couponPinHash;
    }

    public void updateCouponPin(String encodedPin) {
        this.couponPinHash = encodedPin;
    }

}
