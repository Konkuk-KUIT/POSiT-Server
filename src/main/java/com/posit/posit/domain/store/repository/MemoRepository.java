package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // [중요] 이거 import 필수!

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    interface MemoPreviewProjection {
        Long getMemoId();
        String getContent();
    }

    @Query(value = """
        select m.id as memoId, m.content as content
        from memo m
        where m.store_id = :storeId
        order by m.created_at desc
        limit 2
    """, nativeQuery = true)
    List<MemoPreviewProjection> findLatest2PreviewByStoreId(@Param("storeId") Long storeId);

    // ==========================================
    // [신규 추가] 사장님 수신함 조회 (Tab + 무한스크롤)
    // ==========================================

    // 1. [고민 답변] 탭 (concern_id 있음 + REVIEWING 상태)
    @Query("SELECT m FROM Memo m WHERE m.store.id = :storeId AND m.concern IS NOT NULL AND m.status = 'REVIEWING' AND (:cursorId IS NULL OR m.id < :cursorId) ORDER BY m.id DESC")
    List<Memo> findAnswers(@Param("storeId") Long storeId, @Param("cursorId") Long cursorId, Pageable pageable);

    // 2. [자유 메모] 탭 (concern_id 없음 + REVIEWING 상태)
    @Query("SELECT m FROM Memo m WHERE m.store.id = :storeId AND m.concern IS NULL AND m.status = 'REVIEWING' AND (:cursorId IS NULL OR m.id < :cursorId) ORDER BY m.id DESC")
    List<Memo> findFreeMemos(@Param("storeId") Long storeId, @Param("cursorId") Long cursorId, Pageable pageable);

    // 3. [채택 완료] 탭 (status = ADOPTED)
    @Query("SELECT m FROM Memo m WHERE m.store.id = :storeId AND m.status = 'ADOPTED' AND (:cursorId IS NULL OR m.id < :cursorId) ORDER BY m.id DESC")
    List<Memo> findAdoptedMemos(@Param("storeId") Long storeId, @Param("cursorId") Long cursorId, Pageable pageable);

    // 4. [전체 대기] 탭 (REVIEWING 상태 전체) - 'REVIEWING' 탭일 때 사용
    @Query("SELECT m FROM Memo m WHERE m.store.id = :storeId AND m.status = 'REVIEWING' AND (:cursorId IS NULL OR m.id < :cursorId) ORDER BY m.id DESC")
    List<Memo> findAllReviewing(@Param("storeId") Long storeId, @Param("cursorId") Long cursorId, Pageable pageable);

    //가게의 총 메모 개수(누적 메모)
    long countByStoreId(Long storeId);

    //상태별 메모 개수 (신규: REVIEWING, 채택: ADOPTED)
    long countByStoreIdAndStatus(Long storeId, MemoStatus status);
    //특정 고민글에 달린 메모 개수 세기
    long countByConcernId(Long concernId);
}