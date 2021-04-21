package io.revx.api.mysql.crmdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.crmdb.entity.ServerSyncCoordinatorEntity;

@Repository
public interface ServerSyncCoordinatorRepository extends JpaRepository<ServerSyncCoordinatorEntity, Integer> {

}
