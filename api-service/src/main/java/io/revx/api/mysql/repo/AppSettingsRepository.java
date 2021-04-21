package io.revx.api.mysql.repo;

import java.util.List;
import java.util.Set;

import io.revx.core.enums.AppSettingsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.AppSettingsEntity;

import javax.transaction.Transactional;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettingsEntity, Long> {

    List<AppSettingsEntity> findByAdvertiserIdAndKeyInAndActive
            (Long advertiserId, List<AppSettingsKey> key, boolean isActive);

    List<AppSettingsEntity> findByAdvertiserIdAndActive(Long advertiserId, boolean isActive);

    List<AppSettingsEntity> findByLicenseeIdAndKeyInAndActive
            (Long licenseeId, List<AppSettingsKey> key, boolean isActive);

    List<AppSettingsEntity> findByLicenseeIdAndActive(Long licenseeId, boolean isActive);

    List<AppSettingsEntity> findByIdIn(Set<Long> ids);

    @Modifying
    @Transactional
    @Query("update AppSettingsEntity a set a.active = false where a.id in ?1")
    void deactivate(Long id);
}
