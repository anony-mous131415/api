package io.revx.api.mysql.repo.strategy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemTargetingExpression;


@Repository
public interface AdvertiserLineItemTargetingExpRepo
    extends JpaRepository<AdvertiserLineItemTargetingExpression, Long> {


  Optional<AdvertiserLineItemTargetingExpression> findByStrategyId(Long strategyId);

}
