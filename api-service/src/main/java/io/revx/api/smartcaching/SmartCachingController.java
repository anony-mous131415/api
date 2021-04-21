package io.revx.api.smartcaching;

import io.revx.api.constants.ApiConstant;
import io.revx.api.service.SmartCachingService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmartCachingController {

    private static final Logger logger = LogManager.getLogger(SmartCachingController.class);

    @Autowired
    SmartCachingService cachingService;

    /**
     * GET /v2/api/smartcaching
     *
     * To schedule smart caching in case of deployment
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CACHE +
            GraphiteConstants.SMART_CACHING_SCHEDULE)
    @GetMapping(ApiConstant.SMART_CACHING)
    public ResponseEntity<String> scheduleSmartCaching() {
        logger.info("Smart caching scheduler started");
        cachingService.schedulingSmartCaching();
        return ResponseEntity.ok().body("Smart Caching scheduler completed successfully");
    }
}
