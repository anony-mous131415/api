package io.revx.api.mysql.repo.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.revx.api.mysql.entity.audit.AuditChange;


@Repository
public interface AuditChangeRepository extends JpaRepository<AuditChange, Long> {
	
	@Query(value = "SELECT * FROM AuditChange WHERE auc_aul_id IN (?1)", nativeQuery = true)
	List<AuditChange> findByAuditLogId(List<Long> ids);
}
