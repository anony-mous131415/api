package io.revx.core.aop;

import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.timgroup.statsd.StatsDClient;

@Aspect
@Component
public class StatdPerformanceAspect {

  @Autowired
  private StatsDClient statsdClient;
  private static final Logger LOGGER = LogManager.getLogger(StatdPerformanceAspect.class);

  @Around("@annotation(logMetrics)")
  public Object processSystemRequest(final ProceedingJoinPoint pjp, LogMetrics logMetrics)
      throws Throwable {

    Object retVal = null;
    try {
      long start = System.currentTimeMillis();
      retVal = pjp.proceed();
      long end = System.currentTimeMillis();
      long differenceMs = end - start;
      MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
      Method targetMethod = methodSignature.getMethod();
      String keyName = logMetrics.name();
      String keyTimeTaken = keyName.concat(".timetaken");
      statsdClient.increment(keyName);
      statsdClient.time(keyTimeTaken, differenceMs);
      LOGGER.debug("Sending StatsD metric for key:{} from Methodname :---> {} ", keyName,
          targetMethod.getName());
    } finally {

    }
    return retVal;
  }

}
