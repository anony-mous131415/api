package io.revx.api.mysql.amtdb.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;

@Repository
public interface DataPixelsRepository extends JpaRepository<DataPixelsEntity, Long> {
  
  Optional<DataPixelsEntity> findByIdAndStatus(Long id, Status status);
  
}
