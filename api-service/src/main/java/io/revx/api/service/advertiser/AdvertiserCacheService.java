package io.revx.api.service.advertiser;

import io.revx.api.constants.ApiConstant;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.strategy.StrategyCacheService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.objs.FilterComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static io.revx.api.utility.Util.*;
import static io.revx.core.constant.Constants.*;

@Component
public class AdvertiserCacheService {

    private static final Logger logger = LogManager.getLogger(AdvertiserCacheService.class);

    @Autowired
    AdvertiserRepository advertiserRepository;

    @Autowired
    ModelConverterService modelConverterService;

    @Autowired
    LoginUserDetailsService loginUserDetailsService;

    @Autowired
    CacheService cacheService;

    @Autowired
    EntityESService elasticService;

    @Autowired
    ValidationService validationService;

    /**
     * @param id - Advertiser id
     * @param refresh - flag to refresh
     * @return AdvertiserPojo containing advertiser details
     *
     * implementing cache service for advertiser fetch API for reducing latency
     *
     * @throws Exception
     */
    public AdvertiserPojo fetchAdvertiser(Long id, boolean refresh) throws Exception {
        AdvertiserPojo advertiserPojo;
        logger.debug("Inside advertiser fetch by id : {} , cache key : {}", id, getCacheKey(id));
        if (!refresh) {
            List<BaseModel> advertisers = cacheService.fetchListCachedData(getCacheKey(id),null,null);
            if (advertisers != null && advertisers.size() == 1) {
                advertiserPojo = (AdvertiserPojo)advertisers.get(0);
                return advertiserPojo;
            }
        }
        Optional<AdvertiserEntity> advEntity = advertiserRepository.findById(id);
        logger.debug("Is Advertiser present for id {} in table  -->  {} ", id, advEntity.isPresent());

        if (!advEntity.isPresent())
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
                    new Object[] {"Advertiser id in query params. "});

        logger.debug("Advertiser found : {}", advEntity.get());
        advertiserPojo = modelConverterService.populateAdvertiserFromEntity(advEntity.get());
        saveToCache(id, advertiserPojo);
        return advertiserPojo;
    }

    private String getCacheKey(Long id) {
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(ApiConstant.ADVERTISER_CACHE_KEY);
        stringBuilder.append(Constants.CACHE_KEY_SEPRATOR).append(id);
        stringBuilder.append(Constants.CACHE_KEY_SEPRATOR).append(ApiConstant.LICENSEE_CACHE_KEY);
        stringBuilder.append(Constants.CACHE_KEY_SEPRATOR).append(loginUserDetailsService.getLicenseeId());
        return stringBuilder.toString();
    }

    private void saveToCache(Long id, AdvertiserPojo advertiserPojo) {
        logger.debug("Inside saveToCache method. Saving advertiser POJO: {}", advertiserPojo);
        List<BaseModel> advertiser = new ArrayList<>();
        advertiser.add(advertiserPojo);
        if (CollectionUtils.isNotEmpty(advertiser)) {
            cacheService.populateCache(getCacheKey(id), advertiser, THREE_DAYS_TIME_IN_MILLI_SECONDS);
            logger.debug("Inside saveToCache method. Saved {} number of advertiser in cache. saved advertisers : {}"
                    , advertiser.size(), advertiser);
        }
    }

    public void removeFromCache(Long id) {
        String cacheKey = getCacheKey(id);
        logger.debug("cache removed for key : {} ",cacheKey);
        cacheService.removeBaseModelCache(cacheKey);
    }

    public void removeDashboardListCache(Long id) {
        Advertiser elasticEntity = elasticService.searchPojoById(TablesEntity.ADVERTISER,id);
        String dashboardEntity = DashboardEntities.list.name();
        long parentEntityValue = elasticEntity.getLicenseeId();
        deleteDashboardCache(dashboardEntity, DashBoardEntity.ADVERTISER,
                LICENSEE_ID, parentEntityValue, validationService, cacheService);
    }
}
