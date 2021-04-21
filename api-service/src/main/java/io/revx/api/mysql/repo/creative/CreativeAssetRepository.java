/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.creative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.creative.CreativeAssetEntity;

/**
 * The Interface CreativeRepository.
 */
@Repository
public interface CreativeAssetRepository extends JpaRepository<CreativeAssetEntity, Long> {
  
}
