/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.creative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.creative.CDNEntity;


/**
 * The Interface CDNRepository.
 */
@Repository
public interface CDNRepository extends JpaRepository<CDNEntity, Long> {
  
}
