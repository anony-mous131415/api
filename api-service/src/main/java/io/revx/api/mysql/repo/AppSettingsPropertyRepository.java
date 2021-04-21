package io.revx.api.mysql.repo;

import io.revx.api.mysql.entity.AppSettingsPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingsPropertyRepository extends JpaRepository<AppSettingsPropertyEntity, Long> {
}
