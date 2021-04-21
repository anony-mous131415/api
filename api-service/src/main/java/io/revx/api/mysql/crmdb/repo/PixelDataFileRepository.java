package io.revx.api.mysql.crmdb.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.crmdb.entity.PixelDataFileEntity;

@Repository
public interface PixelDataFileRepository extends JpaRepository<PixelDataFileEntity, Long> {

  List<PixelDataFileEntity> findByPixelId(Long id);
  
  PixelDataFileEntity findByPixelIdAndNewFile(Long id, Boolean newFile);
  
}
