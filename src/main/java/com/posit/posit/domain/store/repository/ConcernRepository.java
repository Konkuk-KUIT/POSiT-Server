package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.concern.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcernRepository extends JpaRepository<Concern, Long> {
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
    ConcernPreviewProjection findLatestPreviewByStoreId(Long storeId);

    // [추가] 특정 가게의 고민글을 최신순으로 3개만 가져오기
    List<Concern> findTop3ByStoreIdOrderByCreatedAtDesc(Long storeId);
}
