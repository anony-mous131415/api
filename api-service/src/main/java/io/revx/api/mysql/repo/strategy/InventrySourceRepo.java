package io.revx.api.mysql.repo.strategy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.InventorySource;


@Repository
public interface InventrySourceRepo extends JpaRepository<InventorySource, Long> {

  List<InventorySource> findByStrategyId(Long strategyId);

}
