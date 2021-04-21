package io.revx.api.mysql.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.LicenseeEntity;

@Repository
public interface LicenseeRepository extends JpaRepository<LicenseeEntity, Integer> {
  List<LicenseeEntity> findByIsActive(boolean isActive);
}
