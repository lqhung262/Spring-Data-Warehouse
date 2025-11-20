package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldWardRepository extends JpaRepository<OldWard, Long> {
    Optional<OldWard> findBySourceId(String sourceId);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả oldWards tồn tại trong 1 câu query.
//     */
////    List<OldWard> findByOldWardCodeIn(Collection<String> oldWardCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 oldWard bằng oldWardCode
//     */
//    Optional<OldWard> findByOldWardId(Long oldWardId);
//
//    Long countByOldWardIdIn(Collection<Long> oldWardIds);
}
