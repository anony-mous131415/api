package io.revx.api.service.strategy;

import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemTargetingExpRepo;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.targetting.*;
import org.hibernate.annotations.ManyToAny;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class PlacementTargetingUtilTest {
    @Mock
    private MobileTargetingUtil mobileTargetingUtil;

    @Mock
    private AdvertiserLineItemTargetingExpRepo aliTRepo;

    @Mock
    TargettingComponentRepository tcRepository;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private Utility utility;

    @Mock
    ApplicationProperties properties;

    @InjectMocks
    PlacementTargetingUtil placementTargetingUtil;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        placementTargetingUtil.setMobileTargetingUtil(mobileTargetingUtil);
        placementTargetingUtil.setUtility(utility);
        placementTargetingUtil.setAliTRepo(aliTRepo);
        placementTargetingUtil.setElasticSearch(elasticSearch);
        placementTargetingUtil.setProperties(properties);
        placementTargetingUtil.setTcRepository(tcRepository);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.PlacementTargetingUtil#createPlacementSpecificTargetingExpression(StrategyDTO)}.
     */
    @Test
    public void testCreatePlacementSpecificTargetingExpression() throws Exception {
        List<BaseModel> list = new ArrayList<>();
        list.add(MockDataGenerator.createBaseModel());
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.placements = list;
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(new BaseModel(3L,"Desktop"));
        String response = placementTargetingUtil.createPlacementSpecificTargetingExpression(strategyDTO);
        assertNotNull(response);
    }

    @Test
    public void testCreatePlacementSpecificTargetingExpressionMobileWeb() throws Exception {
        List<BaseModel> baseModelList = new ArrayList<>();
        baseModelList.add(MockDataGenerator.createBaseModel());
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        targetMobileDevices.targetOperatingSystems = system;
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        TargetDeviceTypes targetDeviceTypes = new TargetDeviceTypes();
        targetDeviceTypes.selectAllMobileDeviceTypes = false;
        targetDeviceTypes.mobileDeviceTypes = targetingObject;
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.placements = baseModelList;
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(new BaseModel(3L,"Mobile Web"));
        String response = placementTargetingUtil.createPlacementSpecificTargetingExpression(strategyDTO);
        assertNotNull(response);
    }

    @Test
    public void testCreatePlacementSpecificTargetingExpressionNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        String response = placementTargetingUtil.createPlacementSpecificTargetingExpression(MockDataGenerator.createStrategyDTO());
    }
}
