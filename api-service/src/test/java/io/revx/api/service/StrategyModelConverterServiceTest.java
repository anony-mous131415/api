package io.revx.api.service;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.campaign.AdvertiserIOPixelRepository;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.mysql.repo.creative.DcoAttributeRepository;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.campaign.CurrencyCacheService;
import io.revx.api.service.strategy.TargetingUtil;
import io.revx.core.model.Strategy;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyModelConverterServiceTest extends BaseTestService {

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private CurrencyCacheService currencyCache;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private AdvertiserLineItemPixelRepository lineItemPixelRepository;

    @Mock
    private StrategyEntity strategy;

    @Mock
    private AdvertiserLineItemCreativeRepository lineItemCreativeRepository;

    @Mock
    private CreativeRepository creativeRepo;

    @Mock
    private DcoAttributeRepository dcoAttributeRepository;

    @Mock
    private AdvertiserIOPixelRepository campPixelRepo;

    @Mock
    private StrategyQuickEditDTO strategyQuick;

    @Mock
    private TargetingUtil targetingUtil;

    @InjectMocks
    private StrategyModelConverterService strategyModelConverterService;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        strategyModelConverterService.setCampPixelRepo(campPixelRepo);
        strategyModelConverterService.setElasticSearch(elasticSearch);
        strategyModelConverterService.setLoginUserDetailsService(loginUserDetailsService);
        strategyModelConverterService.setTargetingUtil(targetingUtil);
        strategyModelConverterService.setLineItemCreativeRepository(lineItemCreativeRepository);
        strategyModelConverterService.setLineItemPixelRepository(lineItemPixelRepository);
        strategyModelConverterService.setCreativeRepo(creativeRepo);
        strategyModelConverterService.setApplicationProperties(applicationProperties);
        strategyModelConverterService.setCurrencyCache(currencyCache);
        strategyModelConverterService.setDcoAttributeRepository(dcoAttributeRepository);
    }

    /**
     * Test method for {@link io.revx.api.service.StrategyModelConverterService#populateStrategyForElasticSearch(StrategyDTO)}.
     */
    @Test
    public void testPopulateStrategyForElasticSearch() throws Exception {
        Strategy response = strategyModelConverterService.populateStrategyForElasticSearch(MockDataGenerator.createStrategyDTO());
        assertEquals(3875L,response.getCampaignId());
    }
}
