package io.revx.api.mysql.amtdb.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.amtdb.entity.RuleComponent;

@Repository
public interface RuleComponentRepository extends JpaRepository<RuleComponent, Integer> {

  List<RuleComponent> findBySegmentPixelExpressionId(Long segmentPixelExpressionId);

  @Transactional
  void deleteBySegmentPixelExpressionId(Long segmentPixelExpressionId);
}
