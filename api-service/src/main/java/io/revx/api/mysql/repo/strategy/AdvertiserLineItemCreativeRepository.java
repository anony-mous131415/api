package io.revx.api.mysql.repo.strategy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemCreativeEntity;

@Repository
public interface AdvertiserLineItemCreativeRepository
    extends JpaRepository<AdvertiserLineItemCreativeEntity, Long> {
  

  List<AdvertiserLineItemCreativeEntity> findAllByStrategyId(Long strategyId); 
  
  List<AdvertiserLineItemCreativeEntity> findAllByStrategyIdAndCreativeIdIn(Long strategyId, List<Long> crIds);


}
