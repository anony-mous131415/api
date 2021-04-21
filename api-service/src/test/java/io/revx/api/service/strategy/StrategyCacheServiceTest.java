package io.revx.api.service.strategy;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.*;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.response.UserInfo;
import io.revx.core.service.CacheService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyCacheServiceTest extends BaseTestService {
    @Mock
    StrategyRepository strategyRepository;

    @Mock
    CacheService cacheService;

    @Mock
    LoginUserDetailsService loginUserDetailsService;

    @Mock
    ApplicationProperties applicationProperties;

    @Mock
    ModelConverterService modelConverterService;

    @Mock
    StrategyModelConverterService strategyModelConverterService;

    @Mock
    EntityESService elasticSearch;

    @Mock
    AdvertiserLineItemPixelRepository lineItemPixelRepository;

    @Mock
    AdvertiserLineItemCreativeRepository lineItemCreativeRepository;

    @Mock
    SmartCachingService smartCachingService;

    @Mock
    ValidationService validationService;

    @InjectMocks
    StrategyCacheService strategyCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        strategyCacheService.cacheService = cacheService;
        strategyCacheService.smartCachingService = smartCachingService;
        strategyCacheService.strategyRepository = strategyRepository;
        strategyCacheService.elasticSearch = elasticSearch;
        strategyCacheService.loginUserDetailsService = loginUserDetailsService;
        strategyCacheService.lineItemCreativeRepository = lineItemCreativeRepository;
        strategyCacheService.lineItemPixelRepository = lineItemPixelRepository;
        strategyCacheService.validationService = validationService;
        strategyCacheService.modelConverterService = modelConverterService;
        strategyCacheService.strategyModelConverterService = strategyModelConverterService;
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyCacheService#findByIdAndLicenseeId(Long, Boolean)}.
     */
    @Test
    public void testFindByIdAndLicenseeId() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        List<BaseEntity> baseModelList = new ArrayList<>();
        baseModelList.add(MockDataGenerator.createStrategyEntity());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4345L);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(baseModelList);
        StrategyEntity response = strategyCacheService.findByIdAndLicenseeId(23L,false);
        assertEquals("SafariTest",response.getName());

    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyCacheService#fetchStrategy(Long)}.
     */
    @Test
    public void testFetchStrategy() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        List<BaseModel> baseModelList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        longList.add(23L);
        longList.add(43L);
        baseModelList.add(MockDataGenerator.createStrategyDTO());
        Mockito.when(cacheService.fetchListCachedData(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(baseModelList);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4345L);
        Mockito.when(loginUserDetailsService.getAdvertisers()).thenReturn(longList);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN)) {
                        return MockDataGenerator.createBaseModel();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createBaseModel();
                    } else if (argument.equals(TablesEntity.LICENSEE)) {
                        return MockDataGenerator.createBaseModel();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        StrategyDTO response = strategyCacheService.fetchStrategy(45L);
        assertEquals("standard",response.strategyType);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyCacheService#fetchAllStrategy(Long, int, int, String, String, Boolean)}.
     */
    @Test
    public void testFetchAllStrategy() throws Exception {
        List<StrategyEntity> respone = strategyCacheService.fetchAllStrategy(23L,2,2,"test","test",false);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyCacheService#findByIdAndLicenseeIdAndAdvertiserIdIn(Long, Boolean)}.
     */
    @Test
    public void testFindByIdAndLicenseeIdAndAdvertiserIdIn() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        List<BaseEntity> baseModelList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        longList.add(23L);
        longList.add(43L);
        baseModelList.add(MockDataGenerator.createStrategyEntity());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4345L);
        Mockito.when(loginUserDetailsService.getAdvertisers()).thenReturn(longList);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(baseModelList);
        StrategyEntity response = strategyCacheService.findByIdAndLicenseeIdAndAdvertiserIdIn(23L,false);
        assertNotNull(response);
        assertEquals("SafariTest",response.getName());
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyCacheService#findById(Long)}.
     */
    @Test
    public void testFindById() throws Exception {
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createStrategyEntity());
        Optional<List<BaseEntity>> optionalBase = Optional.of(list);
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        exceptionRule.expect(Exception.class);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(list);
        Optional<StrategyEntity> response = strategyCacheService.findById(23L);
    }

}
