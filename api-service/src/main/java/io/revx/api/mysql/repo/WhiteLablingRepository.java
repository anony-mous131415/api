package io.revx.api.mysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.WhitelabelingEntity;

import java.util.List;

@Repository
public interface WhiteLablingRepository extends JpaRepository<WhitelabelingEntity, Integer> {

  List<WhitelabelingEntity> findBySubDomain(String subDomain);

  List<WhitelabelingEntity> findByLicenseeId(int licenseeId);

}
