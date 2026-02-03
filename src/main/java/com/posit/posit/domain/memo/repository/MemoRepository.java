package com.posit.posit.domain.memo.repository;

import com.posit.posit.domain.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findAllByStoreIdOrderByCreatedAtDesc(Long storeId);
}