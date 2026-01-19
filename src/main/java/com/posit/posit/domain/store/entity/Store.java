package com.posit.posit.domain.store.entity;

import com.posit.posit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "store",
        indexes = {
                @Index(name = "idx_store_owner", columnList = "owner_id"),
                @Index(name = "idx_store_geo", columnList = "latitude, longitude")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_owner"))
    private User owner;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private StoreCategory category;

    @Column(name = "open_time", length = 20, nullable = false)
    private String openTime; // "HH:mm-HH:mm"

    @Enumerated(EnumType.STRING)
    @Column(name = "not_open")
    private Weekday notOpen;

    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private java.math.BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private java.math.BigDecimal longitude;

    @Column(name = "road_address", length = 30, nullable = false)
    private String roadAddress;

    @Column(name = "lot_address", length = 30)
    private String lotAddress;

    @Column(name = "sns_link", length = 255)
    private String snsLink;

    @Column(name = "coupon_pin_hash", length = 255, nullable = false)
    private String couponPinHash;

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
}
