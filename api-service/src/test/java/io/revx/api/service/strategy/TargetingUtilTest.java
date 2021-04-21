package io.revx.api.service.strategy;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemTargetingExpression;
import io.revx.api.mysql.entity.strategy.InventorySource;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemTargetingExpRepo;
import io.revx.api.mysql.repo.strategy.BidStrategyRepo;
import io.revx.api.mysql.repo.strategy.InventrySourceRepo;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.AppCategoryMaster;
import io.revx.core.model.BaseModel;
import io.revx.core.model.strategy.ConnectionType;
import io.revx.core.model.strategy.DealCategoryDTO;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.targetting.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class TargetingUtilTest extends BaseTestService {
    @Mock
    private MobileTargetingUtil mobileTargetingUtil;

    @Mock
    private AdvertiserLineItemTargetingExpRepo aliTRepo;

    @Mock
    private TargettingComponentRepository tcRepository;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private Utility utility;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private PlacementTargetingUtil placementTargetingUtil;

    @Mock
    private InventrySourceRepo inventrySourceRepo;

    @Mock
    private BidStrategyRepo bidStrategyRepo;

    @Mock
    private TargettingComponentRepository targettingComponentRepository;

    @Mock
    private AdvertiserLineItemTargetingExpression itemTargetingExpression;

    @Mock
    private Map<String, Long> filterTypeEnumMap;

    @InjectMocks
    private TargetingUtil targetingUtil;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        utility = new Utility();
        targetingUtil.setPlacementTargetingUtil(placementTargetingUtil);
        targetingUtil.setUtility(utility);
        targetingUtil.setTcRepository(tcRepository);
        targetingUtil.setInventrySourceRepo(inventrySourceRepo);
        targetingUtil.setAliTRepo(aliTRepo);
        targetingUtil.setElasticSearch(elasticSearch);
        utility.targettingComponentRepository = targettingComponentRepository;
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#getTargetingExpressionForStrategy(StrategyDTO, StrategyEntity)}.
     */
    @Test
    public void testGetTargetingExpressionForStrategy() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        Day day = new Day();
        day.setDay(7);
        Set<ConnectionType> set = new HashSet<>();
        set.add(ConnectionType.CELLULAR_NETWORK);
        strategyDTO.targetDays = new DayPart();
        strategyDTO.targetBrowsers = new TargetBrowsers();
        strategyDTO.setConnectionTypes(set);
        strategyDTO.targetGeographies = new TargetGeoDTO();
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response  = targetingUtil.getTargetingExpressionForStrategy(strategyDTO,MockDataGenerator.createStrategyEntity());
        assertNotNull(response);
        assertEquals("(7)",response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#updateTargetingExpressionOnDO(StrategyDTO, StrategyEntity)}.
     */
    @Test
    public void testUpdateTargetingExpressionOnDO() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        strategyEntity.setCampianId(3434L);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(2L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        Optional<AdvertiserLineItemTargetingExpression> opt = Optional
                .of(MockDataGenerator.advertiserLineItemTargetingExpression());
        Mockito.when(aliTRepo.findByStrategyId(Mockito.any())).thenReturn(opt);
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyEntity.getCampianId()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        targetingUtil.addBasicTargetingToStrategy(strategyDTO, strategyEntity);
        targetingUtil.updateBasicTargetingOnStrategy(strategyDTO, strategyEntity);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response  = targetingUtil.updateTargetingExpressionOnDO(strategyDTO,strategyEntity);
        assertNotNull(response);
    }

    @Test
    public void testUpdateTargetingExpressionOnDOConnectionType() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetAppRatings(4);
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        strategyEntity.setCampianId(4523L);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyEntity.getCampianId()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response  = targetingUtil.updateTargetingExpressionOnDO(strategyDTO,strategyEntity);
        assertNotNull(response);
    }

    @Test
    public void testUpdateTargetingExpressionOnDOTargetRTBInventory() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetAppRatings(4);
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        exceptionRule.expect(Exception.class);
        targetingUtil.targetRTBInventory(strategyDTO, strategyEntity);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response = targetingUtil.updateTargetingExpressionOnDO(strategyDTO, strategyEntity);
    }

    @Test
    public void testUpdateTargetingExpressionOnDOTargetOnlyPublishedApp() throws Exception {
        TargetAppCategories targetAppCategories = new TargetAppCategories();
        targetAppCategories.setOsId(34);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetOnlyPublishedApp(true);
        strategyDTO.setTargetIosCategories(targetAppCategories);
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        exceptionRule.expect(ApiException.class);
        targetingUtil.targetRTBInventory(strategyDTO, strategyEntity);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response = targetingUtil.updateTargetingExpressionOnDO(strategyDTO, strategyEntity);
    }

    @Test
    public void testUpdateTargetingExpressionOnDOTargetAppCategories() throws Exception {
        AppCategoryMaster master = new AppCategoryMaster();
        RTBAggregators rtbAggregators = new RTBAggregators();
        rtbAggregators.selectAllAggregators = false;
        master.setId(23L);
        master.setOsId(3);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        targetingObject.blockedList = list;
        TargetAppCategories targetAppCategories = new TargetAppCategories();
        targetAppCategories.setOsId(3);
        targetAppCategories.setSelectAll(false);
        targetAppCategories.appCategories = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        rtbAggregators.aggregators = targetingObject;
        strategyDTO.setRtbAggregators(rtbAggregators);
        strategyDTO.setBidPercentage(2.3f);
        strategyDTO.setTargetOnlyPublishedApp(true);
        strategyDTO.setTargetIosCategories(targetAppCategories);
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(master);
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        exceptionRule.expect(ApiException.class);
        targetingUtil.targetRTBInventory(strategyDTO, strategyEntity);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response = targetingUtil.updateTargetingExpressionOnDO(strategyDTO, strategyEntity);
    }

    @Test
    public void testUpdateTargetingExpressionOnDORTB() throws Exception {
        AppCategoryMaster master = new AppCategoryMaster();
        RTBAggregators rtbAggregators = new RTBAggregators();
        List<String> strings = new ArrayList<>();
        strings.add("Safaritest");
        InventorySource source = new InventorySource();
        source.setName("test");
        source.setId(76L);
        source.setTargetingExpression("Test");
        source.setBidStrategyId(3415L);
        rtbAggregators.selectAllAggregators = false;
        List<InventorySource> inventorySources = new ArrayList<>();
        inventorySources.add(source);
        inventorySources.add(source);
        master.setId(23L);
        master.setOsId(3);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        targetingObject.blockedList = list;
        TargetAppCategories targetAppCategories = new TargetAppCategories();
        targetAppCategories.setOsId(3);
        targetAppCategories.setSelectAll(false);
        targetAppCategories.appCategories = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        rtbAggregators.aggregators = targetingObject;
        strategyDTO.setRtbAggregators(rtbAggregators);
        strategyDTO.setBidPercentage(2.3f);
        strategyDTO.setTargetOnlyPublishedApp(true);
        strategyDTO.setTargetIosCategories(targetAppCategories);
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("01&");
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(master);
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        Mockito.when(inventrySourceRepo.findByStrategyId(Mockito.any())).thenReturn(inventorySources);
        exceptionRule.expect(ApiException.class);
        targetingUtil.updateRTBInventory(strategyDTO,strategyEntity);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        targetingUtil.updateTargetingExpressionOnDO(strategyDTO, strategyEntity);
    }

    @Test
    public void testUpdateTargetingExpressionOnDOBrowser() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        StrategyEntity strategyEntity = new StrategyEntity();
        strategyEntity.setTargetingExpression("7");
        strategyEntity.setCampianId(5647L);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(6L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        Optional<AdvertiserLineItemTargetingExpression> opt = Optional
                .of(MockDataGenerator.advertiserLineItemTargetingExpression());
        Mockito.when(aliTRepo.findByStrategyId(Mockito.any())).thenReturn(opt);
        Mockito.when(elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyEntity.getCampianId()))
                .thenReturn(MockDataGenerator.createCampaignESDTO());
        targetingUtil.populateStrategyDTOWithBasicTargeting(strategyEntity,strategyDTO);
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Day day = new Day();
        day.setDay(7);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(targetingComponent);
        String response  = targetingUtil.updateTargetingExpressionOnDO(strategyDTO,strategyEntity);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#createDayPartTargetingExpression(io.revx.core.model.strategy.StrategyDTO)}.
     */
    @Test
    public void testCreateDayPartTargetingExpression() throws Exception{
        Day day = new Day();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(4);
        day.setDay(3);
        day.setHours(integerList);
        List<Day> dayList = new ArrayList<>();
        dayList.add(day);
        DayPart dayPart = new DayPart();
        dayPart.setDaypart(dayList);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetBrowsers targetBrowsers = new TargetBrowsers();
        targetBrowsers.browsers = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetBrowsers(targetBrowsers);
        strategyDTO.setTargetDays(dayPart);
        String response = targetingUtil.createDayPartTargetingExpression(MockDataGenerator.createStrategyDTO());
        assertNotNull(response);
        String resp = targetingUtil.createDayPartTargetingExpression(strategyDTO);
        assertNotNull(resp);
    }

    @Test
    public void testCreateDayPartTargetingExpressionHoursNull() throws Exception{
        Day day = new Day();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(40);
        day.setDay(3);
        day.setHours(integerList);
        List<Day> dayList = new ArrayList<>();
        dayList.add(day);
        DayPart dayPart = new DayPart();
        dayPart.setDaypart(dayList);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetBrowsers targetBrowsers = new TargetBrowsers();
        targetBrowsers.browsers = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetBrowsers(targetBrowsers);
        strategyDTO.setTargetDays(dayPart);
        exceptionRule.expect(ApiException.class);
        String resp = targetingUtil.createDayPartTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDayPartTargetingExpressionDayNull() throws Exception{
        Day day = new Day();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(4);
        day.setDay(7);
        day.setHours(integerList);
        List<Day> dayList = new ArrayList<>();
        dayList.add(day);
        DayPart dayPart = new DayPart();
        dayPart.setDaypart(dayList);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetBrowsers targetBrowsers = new TargetBrowsers();
        targetBrowsers.browsers = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetBrowsers(targetBrowsers);
        strategyDTO.setTargetDays(dayPart);
        String response = targetingUtil.createDayPartTargetingExpression(MockDataGenerator.createStrategyDTO());
        assertNotNull(response);
        exceptionRule.expect(ApiException.class);
        targetingUtil.createDayPartTargetingExpression(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#createBrowserTargetingExpression(io.revx.core.model.strategy.StrategyDTO)}.
     */
    @Test
    public void testCreateBrowserTargetingExpression() throws Exception{
        Day day = new Day();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(4);
        day.setDay(3);
        day.setHours(integerList);
        List<Day> dayList = new ArrayList<>();
        dayList.add(day);
        DayPart dayPart = new DayPart();
        dayPart.setDaypart(dayList);
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        targetingObject.blockedList = list;
        exceptionRule.expect(Exception.class);
        TargetBrowsers targetBrowsers = new TargetBrowsers();
        targetBrowsers.browsers = targetingObject;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetBrowsers(targetBrowsers);
        strategyDTO.setTargetDays(dayPart);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createBaseModel());
        targetingUtil.createBrowserTargetingExpression(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#createNewGeoTargetingExpression(StrategyDTO)}.
     */
    @Test
    public void testCreateNewGeoTargetingExpressionTargetList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setCountry(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionBlockedList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.blockedList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setCountry(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionCityTargetList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setCity(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionCityBlockedList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.blockedList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setCity(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionStateTargetList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setState(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionStateBlockedList() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        TargetingComponent tc = new TargetingComponent();
        tc.setId(6L);
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.blockedList = list;
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setState(targetingObject);
        exceptionRule.expect(Exception.class);
        dto.setTargetGeographies(targetGeoDTO);
        Mockito.when(tcRepository.save(Mockito.any())).thenReturn(tc);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionFalse() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(false);
        targetGeoDTO.setCountry(null);
        dto.setTargetGeographies(targetGeoDTO);
        String response = targetingUtil.createNewGeoTargetingExpression(dto);
        assertNotNull(response);
    }

    @Test
    public void testCreateNewGeoTargetingExpressionNull() throws Exception {
        StrategyDTO dto = new StrategyDTO();
        TargetGeoDTO targetGeoDTO = new TargetGeoDTO();
        targetGeoDTO.setCustomGeoTargeting(true);
        targetGeoDTO.setCountry(null);
        dto.setTargetGeographies(targetGeoDTO);
        exceptionRule.expect(ApiException.class);
        targetingUtil.createNewGeoTargetingExpression(dto);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#createDmpAudienceTargetingExpression(StrategyDTO)}.
     */
    @Test
    public void testCreateDmpAudienceTargetingExpression() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.targetedSegments = list;
        audienceStrDTO.targetedSegmentsOperator = "AND";
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        exceptionRule.expect(Exception.class);
        String response = targetingUtil.createDmpAudienceTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDmpAudienceTargetingExpressionNUll() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        audienceStrDTO.setCustomSegmentTargeting(true);
        audienceStrDTO.targetedSegments = list;
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        exceptionRule.expect(ApiException.class);
        targetingUtil.createDmpAudienceTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDmpAudienceTargetingExpressionNull() throws Exception {
        StrategyDTO strategyDTO = new StrategyDTO();
        AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
        audienceStrDTO.setCustomSegmentTargeting(true);
        strategyDTO.setTargetDmpSegments(audienceStrDTO);
        exceptionRule.expect(ApiException.class);
        targetingUtil.createDmpAudienceTargetingExpression(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#createSegmentsTargetingObject(Segments)}.
     */
    @Test
    public void testCreateSegmentsTargetingObjectTrue() throws Exception {
        Segments segments = new Segments();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject segmentsTarObj = new TargetingObject();
        segmentsTarObj.blockedList = list;
        segmentsTarObj.targetList = list;
        segments.selectAllSegments = true;
        segments.segments = segmentsTarObj;
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createAudienceDTO());
        TargetSegments response = targetingUtil.createSegmentsTargetingObject(segments);
        assertNotNull(response);
    }

    @Test
    public void testCreateSegmentsTargetingObjectFalse() throws Exception {
        Segments segments = new Segments();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject segmentsTarObj = new TargetingObject();
        segmentsTarObj.blockedList = list;
        segmentsTarObj.targetList = list;
        segments.selectAllSegments = false;
        segments.segments = segmentsTarObj;
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createAudienceDTO());
        TargetSegments response = targetingUtil.createSegmentsTargetingObject(segments);
        assertNotNull(response);
    }

    @Test
    public void testCreateSegmentsTargetingObjectTrueNull() throws Exception {
        Segments segments = new Segments();
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject segmentsTarObj = new TargetingObject();
        segmentsTarObj.blockedList = list;
        segmentsTarObj.targetList = list;
        segments.selectAllSegments = true;
        segments.segments = segmentsTarObj;
        exceptionRule.expect(ApiException.class);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(null);
        targetingUtil.createSegmentsTargetingObject(segments);
    }

    @Test
    public void testCreateSegmentsTargetingObjectFalseNull() throws Exception {
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        TargetingObject segmentsTarObj = new TargetingObject();
        segmentsTarObj.blockedList = list;
        segmentsTarObj.targetList = list;
        Segments segments = new Segments();
        segments.selectAllSegments = false;
        segments.segments = segmentsTarObj;
        DealCategoryDTO dto = new DealCategoryDTO();
        dto.setSelectAll(false);
        dto.setDealCategory(segmentsTarObj);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createBaseModel());
        targetingUtil.saveAdvanceTargeting(dto);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createAudienceDTO());
        TargetSegments response = targetingUtil.createSegmentsTargetingObject(segments);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.TargetingUtil#migrateBrowserTCsFromExcludeToInclude(java.util.List)}.
     */
    @Test
    public void testMigrateBrowserTCsFromExcludeToInclude() throws Exception{
        TargetingComponentDTO dto = new TargetingComponentDTO();
        dto.setId(34L);
        dto.setFilter(MockDataGenerator.createBaseModel());
        dto.setOperator(MockDataGenerator.createBaseModel());
        List<BaseModel> baseModelList = new ArrayList<>();
        baseModelList.add(MockDataGenerator.createBaseModel());
        dto.setCriteriaEntities(baseModelList);
        Mockito.when(elasticSearch.searchList(Mockito.any())).thenReturn(baseModelList);
        List<TargetingComponent> list = new ArrayList<>();
        TargetingComponent targetingComponent = new TargetingComponent();
        targetingComponent.setId(7L);
        targetingComponent.setTargetingFilterId(21L);
        targetingComponent.setTargetingOperatorId(67L);
        targetingComponent.setCriteria("7");
        list.add(targetingComponent);
        List<TargetingComponent> componentList = new ArrayList<>();
        List<TargetingComponentDTO> targetingComponentDTOList = new ArrayList<>();
        targetingComponentDTOList.add(dto);
        componentList.add(targetingComponent);
        targetingUtil.populateBrowserTargetingDTOList(componentList,targetingComponentDTOList);
        List<Long> response = targetingUtil.migrateBrowserTCsFromExcludeToInclude(list);
        assertNotNull(response);
    }
}
