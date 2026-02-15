package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.concern.entity.Concern;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcernRepository extends JpaRepository<Concern, Long> {

    // --- [기존 코드 유지] ---
    interface ConcernPreviewProjection {
        Long getConcernId();
        String getContent();
    }

    @Query(value = """
        select c.id as concernId, c.content as content
        from concern c
        where c.store_id = :storeId
        order by c.created_at desc
        limit 1
    """, nativeQuery = true)
    ConcernPreviewProjection findLatestPreviewByStoreId(@Param("storeId") Long storeId);

    List<Concern> findTop3ByStoreIdOrderByCreatedAtDesc(Long storeId);

    // --- [추가] 1단계: 내가 올린 고민 목록 (무한스크롤 + 댓글 수) ---
    // 1. Concern(c)과 Memo(m)를 LEFT JOIN (댓글 0개여도 글은 나와야 함)
    // 2. 사장님 ID(userId)로 필터링 (내 가게 글만)
    // 3. GROUP BY로 묶어서 댓글 개수(COUNT) 계산
    @Query("SELECT c, COUNT(m) " +
            "FROM Concern c " +
            "LEFT JOIN Memo m ON m.concern = c " +
            "WHERE c.store.owner.id = :userId " +
            "AND (:cursorId IS NULL OR c.id < :cursorId) " +
            "GROUP BY c " +
            "ORDER BY c.id DESC")
    Slice<Object[]> findMyConcernsWithCount(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 게스트용 (특정 가게의 고민 조회 - storeId 기준)
    @Query("SELECT c, COUNT(m) FROM Concern c LEFT JOIN Memo m ON m.concern = c " +
            "WHERE c.store.id = :storeId " +
            "AND (:cursorId IS NULL OR c.id < :cursorId) " +
            "GROUP BY c.id " +  // ★ c 대신 c.id로 수정
            "ORDER BY c.id DESC")
    Slice<Object[]> findStoreConcernsWithCount(@Param("storeId") Long storeId,
                                               @Param("cursorId") Long cursorId,
                                               Pageable pageable);
}