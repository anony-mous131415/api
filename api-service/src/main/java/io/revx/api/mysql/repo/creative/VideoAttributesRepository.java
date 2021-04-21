/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.creative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.creative.VideoAttributeEntity;

/**
 * The Interface CreativeRepository.
 */
@Repository
public interface VideoAttributesRepository extends JpaRepository<VideoAttributeEntity, Long> {
  
}
