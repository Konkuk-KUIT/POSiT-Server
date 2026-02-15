package com.posit.posit.domain.memo.entity;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "memo",
        indexes = {
                @Index(name = "idx_memo_store", columnList = "store_id"),
                @Index(name = "idx_memo_user", columnList = "user_id"),
                @Index(name = "idx_memo_concern", columnList = "concern_id"),
                @Index(name = "idx_memo_store_created", columnList = "store_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Memo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "memo_type", nullable = false)
    private MemoType memoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "free_type")
    private FreeType freeType;

    @Column(name = "title", length = 20, nullable = false)
    private String title;

    @Column(name = "content", length = 150, nullable = false)
    private String content;

    // [삭제됨] private String image;

    // [추가됨] 1:N 관계 설정 (이미지 리스트)
    @OneToMany(mappedBy = "memo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌더 패턴 사용 시 초기화 유지
    private List<MemoImage> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemoStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "owner_read", nullable = false)
    private boolean ownerRead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_memo_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_memo_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern_id", foreignKey = @ForeignKey(name = "fk_memo_concern"))
    private Concern concern;

    // 상태 변경 편의 메서드 (채택/거절 시 사용)
    public void updateStatus(MemoStatus status) {
        this.status = status;
    }

    // [수정됨] 이미지 URL 수정 로직 제거 (이미지는 별도 API로 관리하거나 리스트 조작 필요)
    public void update(String title, String content, FreeType freeType) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        // 이미지는 텍스트처럼 단순 교체가 아니므로 여기서 처리하지 않음

        // freeType은 메모 타입이 'FREE'일 때만 수정 가능하도록 방어 로직 추가
        if (this.memoType == MemoType.FREE && freeType != null) {
            this.freeType = freeType;
        }
    }

    public void markOwnerRead() {
        if(!this.ownerRead) {
            this.ownerRead = true;
        }
    }

    // [추가됨] 이미지 추가 편의 메서드
    public void addImage(MemoImage image) {
        this.images.add(image);
        // MemoImage 생성 시점에 memo를 넣지 않았다면 여기서 넣어줄 수도 있음
        // image.setMemo(this);
    }
}