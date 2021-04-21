package io.revx.api.service.strategy;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
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
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MobileTargetingUtilTest extends BaseTestService {
    @Mock
    TargettingComponentRepository tcRepository;

    @Mock
    EntityESService elasticSearch;

    @Mock
    Utility utility;

    @Mock
    TargettingComponentRepository targettingComponentRepository;

    @InjectMocks
    MobileTargetingUtil mobileTargetingUtil;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        utility = new Utility();
        utility.targettingComponentRepository = targettingComponentRepository;
        mobileTargetingUtil.elasticSearch = elasticSearch;
        mobileTargetingUtil.utility = utility;
        mobileTargetingUtil.tcRepository = tcRepository;
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.MobileTargetingUtil#createMobileTargetingExpression(StrategyDTO)}.
     */
    @Test
    public void testCreateMobileTargetingExpression() throws Exception {
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
        exceptionRule.expect(Exception.class);
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionOsNull() throws Exception {
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
        targetDeviceTypes.mobileDeviceTypes = null;
        exceptionRule.expect(ApiException.class);
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionOSMasterNull() throws Exception {
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
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        exceptionRule.expect(Exception.class);
        mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionOSNull() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
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
        exceptionRule.expect(Exception.class);
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionDeviceTypesNull() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
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
        exceptionRule.expect(ApiException.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionDeviceBrands() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        exceptionRule.expect(Exception.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    else if (argument.equals(TablesEntity.DEVICE_MODEL))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingMobileDeviceBrands() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        TargetingObject targetingObject = new TargetingObject();
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        targetingObject.targetList = lists;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        exceptionRule.expect(Exception.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    else if (argument.equals(TablesEntity.DEVICE_MODEL))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionDeviceBrandsTrue() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = true;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    else if (argument.equals(TablesEntity.DEVICE_MODEL))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
        assertNotNull(response);
    }

    @Test
    public void testCreateMobileTargetingExpressionDeviceBrandsNull() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        exceptionRule.expect(Exception.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateMobileTargetingExpressionDeviceBrandsNegative() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(-1,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        exceptionRule.expect(ApiException.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.MobileTargetingUtil#createDeviceTypeTargetingExpression(StrategyDTO)}.
     */
    @Test
    public void testCreateDeviceTypeTargetingExpression() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        targetMobileDevices.targetOperatingSystems = system;
        List<BaseModel> lists = new ArrayList<>();
        lists.add(MockDataGenerator.createBaseModel());
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        TargetDeviceTypes targetDeviceTypes = new TargetDeviceTypes();
        targetDeviceTypes.selectAllMobileDeviceTypes = true;
        targetDeviceTypes.mobileDeviceTypes = targetingObject;
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        exceptionRule.expect(Exception.class);
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createDeviceTypeTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDeviceTypeTargetingExpressionNegative() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        targetMobileDevices.targetOperatingSystems = system;
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(-1,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        exceptionRule.expect(ApiException.class);
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        TargetDeviceTypes targetDeviceTypes = new TargetDeviceTypes();
        targetDeviceTypes.selectAllMobileDeviceTypes = true;
        targetDeviceTypes.mobileDeviceTypes = targetingObject;
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createDeviceTypeTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDeviceTypeTargetingExpressionNull() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        targetMobileDevices.targetOperatingSystems = system;
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(90,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        targetMobileDevices.targetMobileModels = targetMobileDeviceModels;
        TargetDeviceTypes targetDeviceTypes = new TargetDeviceTypes();
        targetDeviceTypes.selectAllMobileDeviceTypes = true;
        targetDeviceTypes.mobileDeviceTypes = targetingObject;
        targetMobileDevices.targetDeviceTypes = targetDeviceTypes;
        StrategyDTO strategyDTO = new StrategyDTO();
        exceptionRule.expect(Exception.class);
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE))
                        return null;
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        mobileTargetingUtil.createDeviceTypeTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDeviceTypeTargetingExpressionBrands() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(90,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        StrategyDTO strategyDTO = new StrategyDTO();
        exceptionRule.expect(Exception.class);
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    else if (argument.equals(TablesEntity.DEVICE_BRAND))
                        return MockDataGenerator.createBaseModel();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDeviceTypeTargetingExpressionBrandsNull() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(90,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        StrategyDTO strategyDTO = new StrategyDTO();
        exceptionRule.expect(Exception.class);
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    @Test
    public void testCreateDeviceTypeTargetingExpressionBrandsNegative() throws Exception {
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(-1,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = false;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        StrategyDTO strategyDTO = new StrategyDTO();
        exceptionRule.expect(Exception.class);
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(), Mockito.anyLong())).thenAnswer(
                invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if (argument.equals(TablesEntity.OS))
                        return MockDataGenerator.osMaster();
                    else if (argument.equals(TablesEntity.OS_VERSION))
                        return MockDataGenerator.osVersionMaster();
                    throw new InvalidUseOfMatchersException(
                            String.format("Argument %s does not match", argument)
                    );
                }
        );
        String response = mobileTargetingUtil.createMobileTargetingExpression(strategyDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.MobileTargetingUtil#updateMobileCommonExpression(StrategyDTO, StrategyEntity, PlacementExpressionObject)}.
     */
    @Test
    public void testUpdateMobileCommonExpression() throws Exception {
        PlacementExpressionObject placementExpressionObject = new PlacementExpressionObject();
        TargetingComponent targetingComponent = new TargetingComponent("7",8L,100L);
        placementExpressionObject.setDpTC(targetingComponent);
        placementExpressionObject.setFanTC(targetingComponent);
        placementExpressionObject.setMapTC(targetingComponent);
        placementExpressionObject.setMwTC(targetingComponent);
        placementExpressionObject.setmComTEx("test");
        Map<String, BaseModel> properties = new HashMap<>();
        properties.put("test",MockDataGenerator.createBaseModel());
        ExtendedBaseModel extendedBaseModel = new ExtendedBaseModel(4756,"test1",properties);
        List<ExtendedBaseModel> list = new ArrayList<>();
        list.add(extendedBaseModel);
        ExtendedTargetingObject object = new ExtendedTargetingObject();
        object.includeList = list;
        object.excludeList = list;
        TargetOperatingSystem system = new TargetOperatingSystem();
        system.operatingSystems = object;
        system.selectAllOperatingSystems = false;
        TargetMobileDevices targetMobileDevices = new TargetMobileDevices();
        List<BaseModel> lists = new ArrayList<>();
        lists.add(new BaseModel(90,"test"));
        TargetingObject targetingObject = new TargetingObject();
        targetingObject.targetList = lists;
        targetingObject.blockedList = lists;
        TargetMobileDeviceBrands brands = new TargetMobileDeviceBrands();
        brands.selectAllMobileDeviceBrands = true;
        brands.mobileDeviceBrands = targetingObject;
        targetMobileDevices.targetMobileDeviceBrands = brands;
        TargetMobileDeviceModels targetMobileDeviceModels = new TargetMobileDeviceModels();
        targetMobileDeviceModels.selectAllMobileDeviceModels = false;
        targetMobileDeviceModels.mobileDeviceModels = object;
        exceptionRule.expect(Exception.class);
        StrategyDTO strategyDTO = new StrategyDTO();
        strategyDTO.setTargetMobileDevices(targetMobileDevices);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        String response = mobileTargetingUtil.updateMobileCommonExpression(strategyDTO,MockDataGenerator.createStrategyEntity(),placementExpressionObject);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.MobileTargetingUtil#isMobilePlacementSpecificExpression(String)}.
     */
    @Test
    public void testIsMobilePlacementSpecificExpression() throws Exception {
        TargetingComponent targetingComponent = new TargetingComponent("2",18L,100L);
        PlacementExpressionObject placementExpressionObject = new PlacementExpressionObject();
        placementExpressionObject.setDpTC(targetingComponent);
        placementExpressionObject.setFanTC(targetingComponent);
        placementExpressionObject.setMapTC(targetingComponent);
        placementExpressionObject.setMwTC(targetingComponent);
        placementExpressionObject.setmComTEx("test");
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.getOne(Mockito.anyLong())).thenReturn(targetingComponent);
        Boolean response = mobileTargetingUtil.isMobilePlacementSpecificExpression("65");
        assertNotNull(response);
        assertTrue(response);
    }

    @Test
    public void testIsMobilePlacementSpecificExpressionNull() throws Exception {
        Boolean response = mobileTargetingUtil.isMobilePlacementSpecificExpression(null);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.MobileTargetingUtil#isMobileCommonExpression(String)}.
     */
    @Test
    public void testIsMobileCommonExpression() throws Exception {
        TargetingComponent targetingComponent = new TargetingComponent("2",7L,100L);
        PlacementExpressionObject placementExpressionObject = new PlacementExpressionObject();
        placementExpressionObject.setDpTC(targetingComponent);
        placementExpressionObject.setFanTC(targetingComponent);
        placementExpressionObject.setMapTC(targetingComponent);
        placementExpressionObject.setMwTC(targetingComponent);
        placementExpressionObject.setmComTEx("test");
        Optional<TargetingComponent> optional = Optional.of(targetingComponent);
        Mockito.when(targettingComponentRepository.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(tcRepository.getOne(Mockito.anyLong())).thenReturn(targetingComponent);
        Boolean response = mobileTargetingUtil.isMobileCommonExpression("65");
        assertNotNull(response);
        assertTrue(response);
    }
}
