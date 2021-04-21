package io.revx.auth.repository;

import io.revx.auth.entity.LifeTimeAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface LifeTimeTokenRepository extends JpaRepository<LifeTimeAuthenticationEntity, Long> {
    List<LifeTimeAuthenticationEntity> findAllByLicenseeId(Long id);

    @Modifying
    @Transactional
    @Query("update LifeTimeAuthenticationEntity a set a.isActive = false where a.id = ?1")
    int deActivate(Long id);

    @Modifying
    @Transactional
    @Query("update LifeTimeAuthenticationEntity a set a.modifiedOn = ?2 where a.id = ?1")
    int updateModifiedOn(Long id, Long modifiedOn);

    @Query("select a from LifeTimeAuthenticationEntity a where a.lifeTimeAuthToken =?1")
    Optional<LifeTimeAuthenticationEntity> findByLifeTimeAuthToken(String token);
}
