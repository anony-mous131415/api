package io.revx.api.mysql.amtdb.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.amtdb.entity.SegmentPixelMap;

@Repository
public interface SegmentPixelMapRepository extends JpaRepository<SegmentPixelMap, Long> {

  List<SegmentPixelMap> findBySegmentId(Long segmentId);
}
