package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<MemoPreviewProjection> findLatest2PreviewByStoreId(Long storeId);
}