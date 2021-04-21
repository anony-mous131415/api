package io.revx.api.mysql.repo.pixel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;

@Repository
public interface AdvertiserLineItemPixelRepository
    extends JpaRepository<AdvertiserLineItemPixelEntity, Long> {
  

  List<AdvertiserLineItemPixelEntity> findAllByStrategyId(Long strategyId);

  List<AdvertiserLineItemPixelEntity> findAllByStrategyIdAndPixelIdIn(Long strategyId,
      List<Long> pixelIds);


  
}
