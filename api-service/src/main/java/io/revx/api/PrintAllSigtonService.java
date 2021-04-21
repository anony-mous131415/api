package io.revx.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.strategy.StrategyRepository;

@Component
public class PrintAllSigtonService {

  private static Logger logger = LogManager.getLogger(PrintAllSigtonService.class);

  @Autowired
  StrategyRepository strategyRepository;

  public void runSomeThing() {
    logger.debug("Running PrintAllSigtonService Method");
    int i = 0;
    for (StrategyEntity ele : strategyRepository.findByActive(true)) {
      logger.debug("i :: {} ,  {}  ", i, ele);
    }

  }

}
