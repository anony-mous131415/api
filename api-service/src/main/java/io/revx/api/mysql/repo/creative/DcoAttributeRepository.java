/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.mysql.repo.creative;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.creative.DcoAttributesEntity;

/**
 * The Interface CreativeRepository.
 */
@Repository
public interface DcoAttributeRepository extends JpaRepository<DcoAttributesEntity, Long> {

  
  Optional<DcoAttributesEntity> findById(Long id);
  
  DcoAttributesEntity findByCreativeId(Long cid);

  List<DcoAttributesEntity> findByCreativeIdIn(Set<Long> creativeIds);
}
