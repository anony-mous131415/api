/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.clickdestination;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;

/**
 * The Interface ClickDestinationRepository.
 */
@Repository
public interface ClickDestinationRepository extends JpaRepository<ClickDestinationEntity, Long> {

  /**
   * Find all by licensee id.
   *
   * @param licenseeId the licensee id
   * @return the list
   */
  List<ClickDestinationEntity> findAllByLicenseeId(long licenseeId);
  
  Optional<ClickDestinationEntity> findByIdAndIsRefactored(Long id,Boolean isRefactor);
  
  List<ClickDestinationEntity> findAllByLicenseeIdAndIsRefactored(Long Id,Boolean isRefactor);
  
  
  
  



}
