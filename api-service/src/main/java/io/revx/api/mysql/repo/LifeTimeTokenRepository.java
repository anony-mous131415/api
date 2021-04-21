package io.revx.api.mysql.repo;


import io.revx.api.mysql.entity.LifeTimeAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LifeTimeTokenRepository extends JpaRepository<LifeTimeAuthenticationEntity, Long> {
    List<LifeTimeAuthenticationEntity> findAllByLicenseeId(Long id);

    @Query("select a from LifeTimeAuthenticationEntity a where a.lifeTimeAuthToken =?1")
    Optional<LifeTimeAuthenticationEntity> findByLifeTimeAuthToken(String token);
}
