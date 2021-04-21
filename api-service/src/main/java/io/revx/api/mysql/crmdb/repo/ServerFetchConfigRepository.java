package io.revx.api.mysql.crmdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.crmdb.entity.ServerFetchConfigEntity;

@Repository
public interface ServerFetchConfigRepository extends JpaRepository<ServerFetchConfigEntity, Long>{

  ServerFetchConfigEntity findByPixelId(Long id);
  
}
