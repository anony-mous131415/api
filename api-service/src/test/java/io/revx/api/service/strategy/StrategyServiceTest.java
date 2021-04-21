package io.revx.api.service.strategy;

import io.revx.api.audit.StrategyAuditService;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemCreativeEntity;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemTargetingExpression;
import io.revx.api.mysql.entity.strategy.InventorySource;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.campaign.AdvertiserIOPixelRepository;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.pixel.PixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemTargetingExpRepo;
import io.revx.api.mysql.repo.strategy.BidStrategyRepo;
import io.revx.api.mysql.repo.strategy.InventrySourceRepo;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.SmartCachingService;
import io.revx.api.service.StrategyModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.campaign.CampaignService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.common.ReadResponse;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.strategy.DuplicateStrategyRequestDTO;
import io.revx.core.model.strategy.LineItemType;
import io.revx.core.model.strategy.StrategyCreativeAssociationRequestDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.model.targetting.AudienceStrDTO;
import io.revx.core.model.targetting.SiteListDTO;
import io.revx.core.model.targetting.TargetGeoDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.UserInfo;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.objs.FilterComponent;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyServiceTest extends BaseTestService {

    @InjectMocks
    private StrategyService service;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private StrategyCacheService strategyCacheService;

    @Mock
    private ModelConverterService modelConverterService;

    @Mock
    private AdvertiserLineItemCreativeRepository lineItemCreativeRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private CreativeUtil creativeUtil;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private CustomESRepositoryImpl elasticRepo;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private CampaignService campaignService;

    @Mock
    private AdvertiserLineItemPixelRepository lineItemPixelRepository;

    @Mock
    private StrategyModelConverterService strategyModelConverterService;

    @Mock
    private TargetingUtil targetingUtil;

    @Mock
    private CreativeRepository creativeRepository;

    @Mock
    private PixelRepository pixelRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private InventrySourceRepo inventrySourceRepo;

    @Mock
    private AdvertiserLineItemTargetingExpRepo advertiserLineItemTargetingExpRepo;

    @Mock
    private BidStrategyRepo bidStrategyRepo;

    @Mock
    private ValidationService validationService;

    @Mock
    private AdvertiserIOPixelRepository advertiserIOPixelRepository;

    @Mock
    private SmartCachingService smartCachingService;

    @Mock
    private StrategyAuditService strategyAuditService;

    @InjectMocks
    private StrategyService strategyService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
       MockitoAnnotations.initMocks(this);
       strategyCacheService = new StrategyCacheService();
       strategyModelConverterService = new StrategyModelConverterService();
       targetingUtil = new TargetingUtil();
       targetingUtil.setElasticSearch(elasticSearch);
       targetingUtil.setInventrySourceRepo(inventrySourceRepo);
       targetingUtil.setAliTRepo(advertiserLineItemTargetingExpRepo);
       targetingUtil.setBidStrategyRepo(bidStrategyRepo);
       strategyCacheService.loginUserDetailsService = loginUserDetailsService;
       strategyCacheService.strategyRepository = strategyRepository;
       strategyCacheService.cacheService = cacheService;
       strategyCacheService.smartCachingService = smartCachingService;
       strategyCacheService.elasticSearch = elasticSearch;
       strategyCacheService.lineItemPixelRepository = lineItemPixelRepository;
       strategyCacheService.lineItemCreativeRepository = lineItemCreativeRepository;
       strategyModelConverterService.setLoginUserDetailsService(loginUserDetailsService);
       strategyModelConverterService.setElasticSearch(elasticSearch);
       strategyModelConverterService.setCampPixelRepo(advertiserIOPixelRepository);
       strategyModelConverterService.setTargetingUtil(targetingUtil);
       strategyModelConverterService.setLineItemPixelRepository(lineItemPixelRepository);
       strategyModelConverterService.setLineItemCreativeRepository(lineItemCreativeRepository);
       strategyModelConverterService.setCreativeRepo(creativeRepository);
       strategyService.setStrategyCacheService(strategyCacheService);
       strategyService.setStrategyModelConverterService(strategyModelConverterService);
       strategyService.setElasticSearch(elasticSearch);
       strategyService.setAdvertiserLineItemCreativeRepository(lineItemCreativeRepository);
       strategyService.setAdvertiserLineItemPixelRepository(lineItemPixelRepository);
       strategyService.setPixelRepository(pixelRepository);
       strategyService.setModelConverterService(modelConverterService);
       strategyService.setCreativeRepository(creativeRepository);
       strategyService.setStrategyRepository(strategyRepository);
       strategyService.setStrategyAuditService(strategyAuditService);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#get(Long, boolean)}.
     */
    @Test
    public void testGet() throws Exception {
        Mockito.when(smartCachingService.getLicenseeId()).thenReturn(6578L);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(strategyRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        ApiResponseObject<StrategyDTO> response = strategyService.get(1920L, true);
        assertEquals("standard", response.getRespObject().getStrategyType());
    }

    @Test
    public void testGetFetchStrategyNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        strategyService.get(1920L, true);
    }

    @Test
    public void testGetWithFalse() throws Exception {
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createStrategyDTO());
        List<AdvertiserLineItemPixelEntity> lists = new ArrayList<>();
        List<AdvertiserLineItemCreativeEntity> lineItemCreativeEntities = new ArrayList<>();
        lineItemCreativeEntities.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        lists.add(MockDataGenerator.createAdvertiserLineItemPixelEntity());
        Mockito.when(cacheService.fetchListCachedData(Mockito.anyString(),Mockito.anySet(),Mockito.any())).thenReturn(list);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN)) {
                        return MockDataGenerator.createBaseModel();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createBaseModel();
                    } else if (argument.equals(TablesEntity.LICENSEE))  {
                        return MockDataGenerator.createBaseModel();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(lineItemPixelRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(lists);
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(lineItemCreativeEntities);
        Mockito.when(smartCachingService.getLicenseeId()).thenReturn(3454L);
        ApiResponseObject<StrategyDTO> response = strategyService.get(1920L, false);
        assertEquals("standard", response.getRespObject().getStrategyType());
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#createStrategy(StrategyDTO)}.
     */
    @Test
    public void testCreateStrategy() throws Exception {
        List<AdvertiserLineItemPixelEntity> list = new ArrayList<>();
        List<AdvertiserLineItemCreativeEntity> lists = new ArrayList<>();
        List<InventorySource> sourceList = new ArrayList<>();
        Optional<AdvertiserLineItemTargetingExpression> optional = Optional.of(MockDataGenerator.advertiserLineItemTargetingExpression());
        sourceList.add(MockDataGenerator.createInventorySource());
        lists.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemPixelEntity());
        StrategyDTO strategy = MockDataGenerator.createStrategyDTO();
        StrategyDTO strategyDTO = MockDataGenerator.createStrategyDTO();
        StrategyEntity strategyDO = MockDataGenerator.createStrategyEntity();
        List<AdvertiserIOPixel> campPixel = new ArrayList<>();
        campPixel.add(MockDataGenerator.createAdvertiserIOPixel());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(advertiserLineItemTargetingExpRepo.findByStrategyId(strategyDO.getId())).thenReturn(optional);
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(lists);
        Mockito.when(lineItemPixelRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(list);
        Mockito.when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        Mockito.when(advertiserIOPixelRepository.findAllByCampaignId(Mockito.anyLong())).thenReturn(campPixel);
        Mockito.when(inventrySourceRepo.findByStrategyId(Mockito.any())).thenReturn(sourceList);
        Mockito.when(creativeRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createCreativeEntity());
        Mockito.when(bidStrategyRepo.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createBidStrategy());
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.PLATFORM, 1)).thenReturn(MockDataGenerator.createPlatform());
        ApiResponseObject<StrategyDTO> response = strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
        assertEquals("standard", response.getRespObject().getStrategyType());
    }

    @Test
    public void testCreateStrategyAdvertiserId() throws Exception {
        CreativeEntity creativeEntity = new CreativeEntity();
        creativeEntity.setId(3875L);
        creativeEntity.setAdvertiserId(375L);
        creativeEntity.setName("SafariTestImg");
        creativeEntity.setUrlPath("/3874/windows_earth_flat_ad.png");
        creativeEntity.setWidth(380);
        creativeEntity.setHeight(780);
        creativeEntity.setStatus(CreativeStatus.inactive);
        creativeEntity.setCreatedBy(1269590256L);
        List<AdvertiserLineItemPixelEntity> list = new ArrayList<>();
        List<AdvertiserLineItemCreativeEntity> lists = new ArrayList<>();
        List<InventorySource> sourceList = new ArrayList<>();
        Optional<AdvertiserLineItemTargetingExpression> optional = Optional.of(MockDataGenerator.advertiserLineItemTargetingExpression());
        sourceList.add(MockDataGenerator.createInventorySource());
        lists.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemPixelEntity());
        StrategyDTO strategy = MockDataGenerator.createStrategyDTO();
        StrategyDTO strategyDTO = MockDataGenerator.createStrategyDTO();
        StrategyEntity strategyDO = MockDataGenerator.createStrategyEntity();
        List<AdvertiserIOPixel> campPixel = new ArrayList<>();
        campPixel.add(MockDataGenerator.createAdvertiserIOPixel());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(advertiserLineItemTargetingExpRepo.findByStrategyId(strategyDO.getId())).thenReturn(optional);
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(lists);
        Mockito.when(lineItemPixelRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(list);
        Mockito.when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        Mockito.when(advertiserIOPixelRepository.findAllByCampaignId(Mockito.anyLong())).thenReturn(campPixel);
        Mockito.when(inventrySourceRepo.findByStrategyId(Mockito.any())).thenReturn(sourceList);
        Mockito.when(creativeRepository.getOne(Mockito.anyLong())).thenReturn(creativeEntity);
        Mockito.when(bidStrategyRepo.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createBidStrategy());
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.PLATFORM, 1)).thenReturn(MockDataGenerator.createPlatform());
        //exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyCreativeEntityNull() throws Exception {
        List<AdvertiserLineItemPixelEntity> list = new ArrayList<>();
        List<AdvertiserLineItemCreativeEntity> lists = new ArrayList<>();
        List<InventorySource> sourceList = new ArrayList<>();
        Optional<AdvertiserLineItemTargetingExpression> optional = Optional.of(MockDataGenerator.advertiserLineItemTargetingExpression());
        sourceList.add(MockDataGenerator.createInventorySource());
        lists.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemPixelEntity());
        StrategyDTO strategy = MockDataGenerator.createStrategyDTO();
        StrategyDTO strategyDTO = MockDataGenerator.createStrategyDTO();
        StrategyEntity strategyDO = MockDataGenerator.createStrategyEntity();
        List<AdvertiserIOPixel> campPixel = new ArrayList<>();
        campPixel.add(MockDataGenerator.createAdvertiserIOPixel());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(advertiserLineItemTargetingExpRepo.findByStrategyId(strategyDO.getId())).thenReturn(optional);
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(lists);
        Mockito.when(lineItemPixelRepository.findAllByStrategyId(strategyDO.getId())).thenReturn(list);
        Mockito.when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        Mockito.when(advertiserIOPixelRepository.findAllByCampaignId(Mockito.anyLong())).thenReturn(campPixel);
        Mockito.when(inventrySourceRepo.findByStrategyId(Mockito.any())).thenReturn(sourceList);
        Mockito.when(creativeRepository.getOne(Mockito.anyLong())).thenReturn(null);
        Mockito.when(bidStrategyRepo.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createBidStrategy());
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.PLATFORM, 1)).thenReturn(MockDataGenerator.createPlatform());
        //exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyWithCampaignIdNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.strategyType = "standard";
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.ONE);
        strategyDTO.setBudgetValue(BigDecimal.TEN);
        strategyDTO.setStartTime(BigInteger.ONE);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyWithCampaignNull() throws Exception {
        StrategyDTO strategy = new StrategyDTO();
        strategy.setCampaign(MockDataGenerator.createBaseModel());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        exceptionRule.expect(ApiException.class);
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategy.getCampaign().id))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyWithPricingTypeNull() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return null;
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyWithPacingTypeNull() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return null;
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyWithDeliveryNull() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return null;
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testCreateStrategyWithStartTimeGreater() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setActive(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(BigDecimal.TEN.toBigInteger());
        strategyDTO.setEndTime(BigInteger.ONE);
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyWithStartTimeZero() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setActive(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(BigDecimal.ONE.toBigInteger());
        strategyDTO.setEndTime(BigInteger.ONE);
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyWithSoonerDateValue() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setActive(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(new BigInteger("1"));
        strategyDTO.setEndTime(BigInteger.TEN);
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyIsNotValid() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setActive(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(new BigInteger("2"));
        strategyDTO.setEndTime(new BigInteger("1000"));
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyFcap() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setFcapFrequency(null);
        strategyDTO.setActive(true);
        strategyDTO.setFcapEnabled(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(new BigInteger("2"));
        strategyDTO.setEndTime(new BigInteger("10"));
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(false);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyFcapNegative() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setFcapFrequency(-11L);
        strategyDTO.setActive(true);
        strategyDTO.setFcapEnabled(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(new BigInteger("2"));
        strategyDTO.setEndTime(new BigInteger("10"));
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(false);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyENdTimeNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        BaseModel baseModel = new BaseModel();
        baseModel.setName("test");
        baseModel.setId(76L);
        List<BaseModel> list = new ArrayList<>();
        List<BaseModel> list1 = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        list1.add(baseModel);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.setBlockedSegments(list);
        audienceStrDTO.setTargetedSegments(list1);
        audienceStrDTO.setTargetedSegmentsOperator("and");
        strategyDTO.setName("SafariTest");
        strategyDTO.setId(1920L);
        strategyDTO.setFcap(6);
        strategyDTO.setFcapFrequency(-11L);
        strategyDTO.setActive(true);
        strategyDTO.setFcapEnabled(true);
        strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
        strategyDTO.setStrategyType(LineItemType.standard.name());
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
        strategyDTO.setCampaignFcap(true);
        strategyDTO.setStartTime(new BigInteger("2"));
        strategyDTO.setTargetWebSegments(audienceStrDTO);
        strategyDTO.setTargetAppSegments(audienceStrDTO);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        strategyDTO.setPlacements(list);
        strategyDTO.setAdGroupCount(11);
        strategyDTO.setCampaignFcap(false);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setBudgetBy(26959032);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyWithNative() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.strategyType = "standard";
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.ONE);
        strategyDTO.setBudgetValue(BigDecimal.TEN);
        strategyDTO.setStartTime(BigInteger.ONE);
        strategyDTO.isNative = true;
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateStrategyWithPlacementsNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(4l);
        baseModel.setName("test");
        list.add(baseModel);
        strategyDTO.strategyType = "standard";
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.ONE);
        strategyDTO.setBudgetValue(BigDecimal.TEN);
        strategyDTO.setStartTime(BigInteger.ONE);
        strategyDTO.isNative = true;
        strategyDTO.setPlacements(list);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createStrategy(strategyDTO);
    }

    @Test
    public void testCreateANewStrategyClickTracker() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("clickTracker");
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyNameMissing() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyStartDateMissing() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.TEN);
        strategyDTO.setBudgetValue(BigDecimal.ONE);
        strategyDTO.setStartTime(BigInteger.ZERO);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyBudgetValueNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.TEN);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyPricingValueNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyPacingNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        exceptionRule.expect(ApiException.class);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyPricingNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyStrategy() throws Exception {
        List<StrategyEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findByNameAndCampianId(Mockito.any(),Mockito.anyLong())).thenReturn(list);
        StrategyDTO strategyDTO = new StrategyDTO();
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.TEN);
        strategyDTO.setBudgetValue(BigDecimal.ONE);
        strategyDTO.setStartTime(BigInteger.TEN);
        strategyDTO.setTargetGeographies(targetGeoDTO);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    @Test
    public void testCreateANewStrategyStrategyGeographies() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        strategyDTO.setStrategyType("standard");
        strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
        strategyDTO.setName("test");
        strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
        strategyDTO.setPricingValue(BigDecimal.TEN);
        strategyDTO.setBudgetValue(BigDecimal.ONE);
        strategyDTO.setStartTime(BigInteger.TEN);
        strategyDTO.setTargetGeographies(targetGeoDTO);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.createANewStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#updateStrategy(StrategyDTO)}.
     */
    @Test
    public void testUpdateStrategy() throws Exception {
        CampaignESDTO campaignESDTO = new CampaignESDTO();
        campaignESDTO.setAdvertiserId(3874L);
        campaignESDTO.setName("SafariTestIO");
        campaignESDTO.setId(3875L);
        campaignESDTO.setEndTime(100L);
        campaignESDTO.setStartTime(2L);
        campaignESDTO.setFcap(2);
        campaignESDTO.setCurrencyCode("1");
        Set<FilterComponent> set = new HashSet<>();
        set.add(MockDataGenerator.filterComponent());
        Map<FilterType,Set<FilterComponent>> map = new HashMap<>();
        map.put(FilterType.TABLE_COLUMN,set);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return campaignESDTO;
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    } else if (argument.equals(TablesEntity.STRATEGY))  {
                        return MockDataGenerator.createStrategy();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.doReturn(map).when(validationService).getFiltersMap(Mockito.anyList());
        exceptionRule.expect(Exception.class);
        Mockito.when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(strategyRepository.getOne(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(validationService.getFiltersMap(Mockito.any())).thenReturn(map);
        ApiResponseObject<StrategyDTO> response = strategyService.updateStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testUpdateStrategyWithNullEntity() throws Exception {
        exceptionRule.expect(ApiException.class);
        strategyService.updateStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testUpdateStrategyWithClickTracker() throws Exception {
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setType(LineItemType.clickTracker);
        Mockito.when(strategyRepository.getOne(Mockito.any())).thenReturn(strategyEntity);
        exceptionRule.expect(Exception.class);
        strategyService.updateStrategy(MockDataGenerator.createStrategyDTO());
    }

    @Test
    public void testUpdateStrategyWithNull() throws Exception {
        StrategyEntity strategyEntity = new StrategyEntity();
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("clickTracker");
        strategyEntity.setType(LineItemType.standard);
        Mockito.when(strategyRepository.getOne(Mockito.any())).thenReturn(strategyEntity);
        exceptionRule.expect(Exception.class);
        strategyService.updateStrategy(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#duplicateStrategy(Long, DuplicateStrategyRequestDTO)}.
     */
    @Test
    public void testDuplicateStrategyWithIdNull() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        exceptionRule.expect(ApiException.class);
        strategyService.duplicateStrategy(23L,MockDataGenerator.createDuplicateStrategyRequestDTO());
    }

    @Test
    public void testUpdateStrategyWithStrategyNull() throws Exception {
        StrategyEntity strategyEntity = new StrategyEntity();
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setStrategyType("standard");
        strategyEntity.setType(LineItemType.clickTracker);
        Mockito.when(strategyRepository.getOne(Mockito.any())).thenReturn(strategyEntity);
        exceptionRule.expect(Exception.class);
        strategyService.updateStrategy(strategyDTO);
    }

    @Test
    public void testDuplicateStrategyWithEndTimeNull() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        DuplicateStrategyRequestDTO duplicateStrategyRequestDTO = new DuplicateStrategyRequestDTO();
        duplicateStrategyRequestDTO.name = "SafariTest";
        duplicateStrategyRequestDTO.startTime = BigInteger.ONE;
        exceptionRule.expect(ApiException.class);
        strategyService.duplicateStrategy(3425L, duplicateStrategyRequestDTO);
    }

    @Test
    public void testDuplicateStrategyWithStartTimeNull() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        DuplicateStrategyRequestDTO duplicateStrategyRequestDTO = new DuplicateStrategyRequestDTO();
        duplicateStrategyRequestDTO.name = "SafariTest";
        duplicateStrategyRequestDTO.endTime = BigInteger.ONE;
        exceptionRule.expect(ApiException.class);
        strategyService.duplicateStrategy(3425L, duplicateStrategyRequestDTO);
    }

    @Test
    public void testDuplicateStrategyCampaignExpired() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(null);
        exceptionRule.expect(ApiException.class);
        strategyService.duplicateStrategy(3425L, MockDataGenerator.createDuplicateStrategyRequestDTO());
    }

    @Test
    public void testDuplicateStrategyWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(null);
        strategyService.duplicateStrategy(3425L, MockDataGenerator.createDuplicateStrategyRequestDTO());
    }

    @Test
    public void testDuplicateStrategyWithNullName() throws Exception {
        DuplicateStrategyRequestDTO request = new DuplicateStrategyRequestDTO();
        request.name = null;
        exceptionRule.expect(ApiException.class);
        strategyService.duplicateStrategy(3425L, request);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#getCreativesForStrategy(long)}.
     */
    @Test
    public void testGetCreativesForStrategy() throws Exception {
        List<AdvertiserLineItemCreativeEntity> list = new ArrayList<>();
        List<CreativeEntity> lists = new ArrayList<>();
        List<CreativeDTO> dtoList = new ArrayList<>();
        lists.add(MockDataGenerator.createCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        dtoList.add(MockDataGenerator.createCreativeDTO());
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(creativeRepository.findByIdIn(Mockito.any())).thenReturn(lists);
        Mockito.when(creativeUtil.populateCreativeDTOsFromEntities(Mockito.any())).thenReturn(dtoList);
        ApiResponseObject<ReadResponse<CreativeDTO>> response = strategyService.getCreativesForStrategy(33L);
        assertNotNull(response);
    }

    @Test
    public void testGetCreativesForStrategyEntityNull() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(null);
        exceptionRule.expect(ApiException.class);
        strategyService.getCreativesForStrategy(33L);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#getPixelsForStrategy(long)}.
     */
    @Test
    public void testGetPixelsForStrategy() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyEntity());
        List<AdvertiserLineItemPixelEntity> list = new ArrayList<>();
        List<ConversionPixelEntity> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createConversionPixelEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemPixelEntity());
        Mockito.when(lineItemPixelRepository.findAllByStrategyId(Mockito.any())).thenReturn(list);
        Mockito.when(pixelRepository.findAllByIdIn(Mockito.any())).thenReturn(lists);
        Mockito.when(modelConverterService.convertPixelToDTO(Mockito.any())).thenReturn(MockDataGenerator.createPixel());
        ApiResponseObject<ReadResponse<Pixel>> response = strategyService.getPixelsForStrategy(33L);
        assertNotNull(response);
    }

    @Test
    public void testGetPixelsForStrategyNull() throws Exception {
        Mockito.when(strategyRepository.getOne(Mockito.anyLong())).thenReturn(null);
        exceptionRule.expect(ApiException.class);
        strategyService.getPixelsForStrategy(33L);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#validateSiteList(List)}.
     */
    @Test
    public void testValidateSiteList() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("response");
        ApiResponseObject<SiteListDTO> response = strategyService.validateSiteList(list);
        assertEquals(1L, response.getRespObject().getClass().getModifiers());
    }

    @Test
    public void testValidateSiteListWithNull() throws Exception {
        Mockito.when(elasticSearch.searchByNameExactMatch(Mockito.any(),Mockito.anySet())).thenReturn(null);
        strategyService.validateSiteList(null);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#activate(String)}.
     */
    @Test
    public void testActivate() throws Exception {
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setName("SafariTest");
        strategyEntity.setActive(false);
        strategyEntity.setId(3875L);
        List<StrategyEntity> list = new ArrayList<>();
        list.add(strategyEntity);
        Mockito.when(strategyRepository.findAllByIdIn(Mockito.any())).thenReturn(list);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createStrategy());
        exceptionRule.expect(Exception.class);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.activate("21");
        assertEquals(1, response.getRespObject().size());
    }

    @Test
    public void testActivateWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.activate("");
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#activate(List)}.
     */
    @Test
    public void testActivateWithList() throws Exception {
        List<StrategyEntity> list = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        longList.add(234L);
        list.add(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findAllByIdIn(Mockito.any())).thenReturn(list);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.activate(longList);
        assertEquals("strategy :234", response.getRespObject().get(234L).getMessage());
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#deactivate(String)}.
     */
    @Test
    public void testDeactivate() throws Exception {
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setName("SafariTest");
        strategyEntity.setActive(false);
        strategyEntity.setId(3875L);
        List<StrategyEntity> list = new ArrayList<>();
        list.add(strategyEntity);
        Mockito.when(strategyRepository.findAllByIdIn(Mockito.any())).thenReturn(list);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.deactivate("21");
        assertEquals(2, response.getRespObject().size());
    }

    @Test
    public void testDeactivateWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.deactivate("");
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.StrategyService#deactivate(List)}.
     */
    @Test
    public void testDeactivateWithList() throws Exception {
        List<StrategyEntity> list = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        longList.add(234L);
        list.add(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findAllByIdIn(Mockito.any())).thenReturn(list);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createStrategy());
        exceptionRule.expect(Exception.class);
        ApiResponseObject<Map<Long, ResponseMessage>> response = strategyService.deactivate(longList);
        assertEquals("strategy :234", response.getRespObject().get(234L).getMessage());
    }

    /**
     * Test method for {@link StrategyService#getMinMaxSettings()}.
     */
    @Test
    public void testGetMinMaxSettings() throws Exception {
        ApiResponseObject<Map<String, Object>> response = strategyService.getMinMaxSettings();
        assertNull(response);
    }

    /**
     * Test method for {@link StrategyService#getStrategyTargetingTillNow(Long)}.
     */
    @Test
    public void testGetStrategyTargetingTillNow() throws Exception {
        ApiResponseObject<Map<String, Object>> response = strategyService.getStrategyTargetingTillNow(234L);
        assertNull(response);
    }

    /**
     * Test method for {@link StrategyService#associateCreativesToStrategies(StrategyCreativeAssociationRequestDTO)}.
     */
    @Test
    public void testAssociateCreativesToStrategies() throws Exception {
        StrategyCreativeAssociationRequestDTO create = new StrategyCreativeAssociationRequestDTO();
        List<CreativeEntity> list = new ArrayList<>();
        List<StrategyEntity> lists = new ArrayList<>();
        List<BaseModel> baseModelList = new ArrayList<>();
        baseModelList.add(MockDataGenerator.createBaseModel());
        create.creativesList = baseModelList;
        create.strategyList = baseModelList;
        lists.add(MockDataGenerator.createStrategyEntity());
        list.add(MockDataGenerator.createCreativeEntity());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3875L);
        Mockito.when(creativeRepository.findByIdIn(Mockito.any())).thenReturn(list);
        Mockito.when(strategyRepository.findAllByLicenseeIdAndIdIn(Mockito.anyLong(), Mockito.any())).thenReturn(lists);
        ApiResponseObject<StrategyCreativeAssociationResponseDTO> response = strategyService.associateCreativesToStrategies(create);
        assertNotNull(response);
    }

    @Test
    public void testAssociateCreativesToStrategiesWithEntityNull() throws Exception {
        StrategyCreativeAssociationRequestDTO create = new StrategyCreativeAssociationRequestDTO();
        List<CreativeEntity> list = new ArrayList<>();
        List<StrategyEntity> lists = new ArrayList<>();
        List<BaseModel> baseModelList = new ArrayList<>();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(2324L);
        baseModelList.add(baseModel);
        create.creativesList = baseModelList;
        lists.add(MockDataGenerator.createStrategyEntity());
        list.add(MockDataGenerator.createCreativeEntity());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3425L);
        Mockito.when(creativeRepository.findByIdIn(Mockito.any())).thenReturn(list);
        Mockito.when(strategyRepository.findAllByLicenseeIdAndIdIn(Mockito.anyLong(), Mockito.any())).thenReturn(lists);
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<StrategyCreativeAssociationResponseDTO> response = strategyService.associateCreativesToStrategies(create);
    }

    @Test
    public void testAssociateCreativesToStrategiesWithNull() throws Exception {
        StrategyCreativeAssociationRequestDTO create = new StrategyCreativeAssociationRequestDTO();
        List<CreativeEntity> list = new ArrayList<>();
        List<StrategyEntity> lists = new ArrayList<>();
        List<BaseModel> ModelList = new ArrayList<>();
        List<BaseModel> baseModelList = new ArrayList<>();
        baseModelList.add(MockDataGenerator.createBaseModel());
        create.creativesList = baseModelList;
        BaseModel baseModel = new BaseModel();
        baseModel.setId(2324L);
        ModelList.add(baseModel);
        create.creativesList = baseModelList;
        create.strategyList = ModelList;
        lists.add(MockDataGenerator.createStrategyEntity());
        list.add(MockDataGenerator.createCreativeEntity());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(101L);
        Mockito.when(creativeRepository.findByIdIn(Mockito.any())).thenReturn(list);
        Mockito.when(strategyRepository.findAllByLicenseeIdAndIdIn(Mockito.anyLong(), Mockito.any())).thenReturn(lists);
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<StrategyCreativeAssociationResponseDTO> response = strategyService.associateCreativesToStrategies(create);
    }

    /**
     * Test method for {@link StrategyService#associateCreativesToStrategy(StrategyEntity, List, boolean)}.
     */
    @Test
    public void testAssociateCreativesToStrategyFalse() throws Exception {
        List<AdvertiserLineItemCreativeEntity> list = new ArrayList<>();
        List<CreativeEntity> creativeEntityList = new ArrayList<>();
        creativeEntityList.add(MockDataGenerator.createCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(list);
        Set<Long> response = strategyService.associateCreativesToStrategy(MockDataGenerator.createStrategyEntity(), creativeEntityList, false);
        assertEquals(1, response.size());
    }

    @Test
    public void testAssociateCreativesToStrategyTrue() throws Exception {
        List<AdvertiserLineItemCreativeEntity> list = new ArrayList<>();
        List<CreativeEntity> creativeEntityList = new ArrayList<>();
        creativeEntityList.add(MockDataGenerator.createCreativeEntity());
        list.add(MockDataGenerator.createAdvertiserLineItemCreativeEntity());
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(list);
        Set<Long> response = strategyService.associateCreativesToStrategy(MockDataGenerator.createStrategyEntity(), creativeEntityList, true);
        assertEquals(1, response.size());
    }

    @Test
    public void testAssociateCreativesToStrategyWithNull() throws Exception {
        List<CreativeEntity> creativeEntityList = new ArrayList<>();
        Mockito.when(lineItemCreativeRepository.findAllByStrategyId(Mockito.anyLong())).thenReturn(null);
        Set<Long> response = strategyService.associateCreativesToStrategy(MockDataGenerator.createStrategyEntity(), creativeEntityList, false);
        assertEquals(0, response.size());
    }

    /**
     * Test method for {@link StrategyService#updateStrategyWithoutStartingTransaction(StrategyDTO)}.
     */
    @Test
    public void testUpdateStrategyWithoutStartingTransaction() throws Exception {
        ApiResponseObject<StrategyDTO> response = strategyService.updateStrategyWithoutStartingTransaction(MockDataGenerator.createStrategyDTO());
        assertNull(response);
    }

    /**
     * Test method for {@link StrategyService#getStrategyWithoutStartingTransaction(Integer)}.
     */
    @Test
    public void testGetStrategyWithoutStartingTransaction() throws Exception {
        StrategyDTO response = strategyService.getStrategyWithoutStartingTransaction(23);
        assertNull(response);
    }

    /**
     * Test method for {@link StrategyService#getStrategyQuickEditDetails(Long)}.
     */
    @Test
    public void testGetStrategyQuickEditDetails() throws Exception {
        Mockito.when(strategyRepository.getQuickEditDetails(Mockito.anyLong())).thenReturn(MockDataGenerator.createStrategyQuickEditDTO());
        ApiResponseObject<StrategyQuickEditDTO> response = strategyService.getStrategyQuickEditDetails(33L);
        assertEquals("clickTracker",response.getRespObject().getStrategyType().name());
    }

    @Test
    public void testGetStrategyQuickEditDetailsWithNull() throws Exception {
        Mockito.when(strategyRepository.getQuickEditDetails(Mockito.anyLong())).thenReturn(null);
        exceptionRule.expect(ApiException.class);
        strategyService.getStrategyQuickEditDetails(33L);
    }

    /**
     * Test method for {@link StrategyService#updateQuickEditDetails(StrategyQuickEditDTO)}.
     */
    @Test
    public void testUpdateQuickEditDetails() throws Exception {
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        ApiResponseObject<StrategyDTO> response = strategyService.updateQuickEditDetails(MockDataGenerator.createStrategyQuickEditDTO());
        assertEquals("standard",response.getRespObject().strategyType);
    }

    @Test
    public void testUpdateQuickEditDetailsClickTracker() throws Exception {
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.CAMPAIGN))
                        return MockDataGenerator.createCampaignESDTO();
                    else if (argument.equals(TablesEntity.PRICING))
                        return MockDataGenerator.createPricing();
                    else if (argument.equals(TablesEntity.PACING_TYPE))
                        return MockDataGenerator.createPacingType();
                    else if (argument.equals(TablesEntity.DELIVERY_PRIORITY))
                        return MockDataGenerator.createDeliveryPriority();
                    else if (argument.equals(TablesEntity.PLATFORM)) {
                        return MockDataGenerator.createPlatform();
                    } else if (argument.equals(TablesEntity.ADVERTISER)) {
                        return MockDataGenerator.createAdvertiser();
                    } else if (argument.equals(TablesEntity.TIMEZONE)) {
                        return MockDataGenerator.createTimeZoneDTO();
                    }
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchDetailById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createParentBasedObject());
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        ApiResponseObject<StrategyDTO> response = strategyService.updateQuickEditDetails(MockDataGenerator.createStrategyQuickEditDTO());
        assertEquals("standard",response.getRespObject().strategyType);
    }

    @Test
    public void testUpdateQuickEditDetailsWithNull() throws Exception {
        Optional<StrategyEntity> optional = Optional.empty();
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        exceptionRule.expect(ApiException.class);
        strategyService.updateQuickEditDetails(MockDataGenerator.createStrategyQuickEditDTO());
    }

    @Test
    public void testUpdateQuickEditDetailsWithEntityNameNull() throws Exception {
        StrategyQuickEditDTO strategyQuickEditDTO = new StrategyQuickEditDTO();
        strategyQuickEditDTO.setStrategyType(LineItemType.clickTracker);
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        exceptionRule.expect(ApiException.class);
        strategyService.updateQuickEditDetails(strategyQuickEditDTO);
    }

    @Test
    public void testUpdateQuickEditDetailsWithEntityPricingTypeNull() throws Exception {
        StrategyQuickEditDTO strategyQuickEditDTO = new StrategyQuickEditDTO();
        strategyQuickEditDTO.setName("test");
        strategyQuickEditDTO.setPricingType(-1);
        strategyQuickEditDTO.setStrategyType(LineItemType.clickTracker);
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        exceptionRule.expect(ApiException.class);
        strategyService.updateQuickEditDetails(strategyQuickEditDTO);
    }

    @Test
    public void testUpdateQuickEditDetailsWithEntityPricingValueNull() throws Exception {
        StrategyQuickEditDTO strategyQuickEditDTO = new StrategyQuickEditDTO();
        strategyQuickEditDTO.setName("test");
        strategyQuickEditDTO.setPricingType(23);
        strategyQuickEditDTO.setStrategyType(LineItemType.clickTracker);
        Optional<StrategyEntity> optional = Optional.of(MockDataGenerator.createStrategyEntity());
        Mockito.when(strategyRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(strategyRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createStrategyEntity());
        exceptionRule.expect(ApiException.class);
        strategyService.updateQuickEditDetails(strategyQuickEditDTO);
    }

    @Test
    public void testGetSkadSettings() {
        Mockito.when(properties.getSkadSettingsFileLocation()).thenReturn("/atom/origin/skad/settings.json");

        ApiResponseObject<String> responseObject = service.getSkadSettings();
        assertNotNull(responseObject);
    }
}
