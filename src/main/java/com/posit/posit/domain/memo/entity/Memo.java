package com.posit.posit.domain.memo.entity;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemoStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

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

    public void update(String title, String content, String imageUrl, FreeType freeType) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (imageUrl != null) {
            this.image = imageUrl; // 엔티티 필드명은 image라고 가정
        }

        // freeType은 메모 타입이 'FREE'일 때만 수정 가능하도록 방어 로직 추가
        if (this.memoType == MemoType.FREE && freeType != null) {
            this.freeType = freeType;
        }
    }
}
