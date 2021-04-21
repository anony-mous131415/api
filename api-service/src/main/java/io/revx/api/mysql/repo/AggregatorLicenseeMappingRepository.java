package io.revx.api.mysql.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.revx.api.mysql.entity.AggregatorLicenseeMappingEntity;

public interface AggregatorLicenseeMappingRepository extends JpaRepository<AggregatorLicenseeMappingEntity, Integer>{

	List<AggregatorLicenseeMappingEntity> findAllByLicenseeId(int licenseeId );

}
