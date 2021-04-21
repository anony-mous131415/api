package io.revx.api.mysql.amtdb.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.Segments;

@Repository
public interface SegmentsRepository extends JpaRepository<Segments, Long> {

  List<Segments> findByLicenseeId(Long licenseeId);
  
  Segments findByIdAndLicenseeId(Long id, Long licenseeId);
  
  Segments findByRemoteSegmentId(String id);
  
  @Transactional
  @Modifying
  @Query("update Segments set status = :status where id = :idList")
  int updateStatus(@Param("status") Status status, @Param("idList") Long id);
}
