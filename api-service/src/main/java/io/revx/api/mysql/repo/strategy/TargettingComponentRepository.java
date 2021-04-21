package io.revx.api.mysql.repo.strategy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.TargetingComponent;


@Repository
public interface TargettingComponentRepository extends JpaRepository<TargetingComponent, Long> {

  
  List<TargetingComponent> findByTargetingFilterId(Long targetingFilterId);

  List<TargetingComponent> findByTargetingOperatorId(Long targetingOperatorId);

}
