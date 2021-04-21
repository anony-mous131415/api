package io.revx.api.mysql.crmdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.crmdb.entity.ServerSyncActionEntity;

@Repository
public interface ServerSyncActionEntityRepository extends JpaRepository<ServerSyncActionEntity, Long>{

}
