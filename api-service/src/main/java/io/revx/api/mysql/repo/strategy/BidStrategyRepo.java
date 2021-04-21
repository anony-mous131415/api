package io.revx.api.mysql.repo.strategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.BidStrategy;


@Repository
public interface BidStrategyRepo extends JpaRepository<BidStrategy, Long> {

}
