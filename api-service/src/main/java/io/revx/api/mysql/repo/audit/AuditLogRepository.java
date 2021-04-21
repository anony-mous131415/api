package io.revx.api.mysql.repo.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.revx.api.mysql.entity.audit.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	@Query(value = "SELECT * FROM AuditLog WHERE aul_entity_id =(?1) and aul_entity_type= 'STRATEGY' and aul_timestamp >= (?2) and aul_timestamp <= (?3)", nativeQuery = true)
	List<AuditLog> findAuditLogByStrategyId(long id, long startTime, long endTime);

	@Query(value = "SELECT * FROM AuditLog WHERE aul_entity_id =(?1) and aul_entity_type= 'CAMPAIGN' and aul_timestamp >= (?2) and aul_timestamp <= (?3)", nativeQuery = true)
	List<AuditLog> findAuditLogByCampaignId(long id, long startTime, long endTime);
	
	@Query(value = "SELECT * FROM AuditLog WHERE ((aul_entity_id IN (?2) and aul_entity_type= 'STRATEGY') OR ( aul_entity_id=(?1) and aul_entity_type='CAMPAIGN')) and aul_timestamp >= (?3) and aul_timestamp <= (?4)", nativeQuery = true)
	List<AuditLog> findAuditLogByCampaignIdAndStrategyIds(long id , List<Long> ids, long startTime, long endTime);
}
