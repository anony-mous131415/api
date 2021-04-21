package io.revx.api.service.clickdestination;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.api.service.creative.CreativeValidationService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.*;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClickDestinationServiceTest extends BaseTestService {
    @Mock
    private ClickDestinationCacheService cacheService;

    @Mock
    private EntityESService entityESService;

    @Mock
    private CreativeValidationService validator;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private ValidationService validationService;

    @Mock
    private ModelConverterService modelConverterService;

    @Mock
    private AdvertiserService advertiserService;

    @Mock
    private ClickDestinationRepository clickDestinationRepository;

    @InjectMocks
    private ClickDestinationService clickDestinationService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clickDestinationService.loginDetails = loginUserDetailsService;
        clickDestinationService.modelConverter = modelConverterService;
        clickDestinationService.cdRepo = clickDestinationRepository;
        clickDestinationService.cacheService = cacheService;
        clickDestinationService.validator = validator;
        clickDestinationService.advertiserService = advertiserService;
        clickDestinationService.esRepository = entityESService;
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationService#create(ClickDestination)}.
     */
    @Test
    public void testCreate() throws Exception {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3832L);
        Mockito.when(modelConverterService.convertToClickDestintionEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(clickDestinationRepository.save(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(modelConverterService.convertFromClickDestEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestination());
        ApiResponseObject<ClickDestination> responseObject = clickDestinationService
                .create(MockDataGenerator.createClickDestination());
        assertNotNull(responseObject);
        assertEquals("Jntuworld 468x60",responseObject.getRespObject().getName());
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationService#update(ClickDestination)}.
     */
    @Test
    public void testUpdate() throws Exception {
        Mockito.when(modelConverterService.convertToClickDestintionEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(clickDestinationRepository.save(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(modelConverterService.convertFromClickDestEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestination());
        ApiResponseObject<ClickDestination> responseObject = clickDestinationService
                .update(MockDataGenerator.createClickDestination());
        assertNotNull(responseObject);
        assertEquals("Jntuworld 468x60",responseObject.getRespObject().getName());
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationService#getById(Long)}.
     */
    @Test
    public void testGetById() throws Exception {
        Optional<ClickDestinationEntity> optional = Optional.of(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(clickDestinationRepository.findByIdAndIsRefactored(Mockito.any(),Mockito.any()))
                .thenReturn(optional);
        Mockito.when(modelConverterService.convertFromClickDestEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestination());
        ApiResponseObject<ClickDestination> apiResponseObject = clickDestinationService.getById(3832L);
        assertEquals("Jntuworld 468x60",apiResponseObject.getRespObject().getName());
    }

    @Test
    public void testGetByIdNull() throws Exception {
        Optional<ClickDestinationEntity> optional = Optional.of(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(clickDestinationRepository.findByIdAndIsRefactored(Mockito.any(),Mockito.any()))
                .thenReturn(optional);
        Mockito.when(modelConverterService.convertFromClickDestEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestination());
        exceptionRule.expect(ApiException.class);
        clickDestinationService.getById(null);
    }

    @Test
    public void testGetByNullId() throws Exception {
        Mockito.when(modelConverterService.convertFromClickDestEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createClickDestination());
        exceptionRule.expect(ApiException.class);
        clickDestinationService.getById(2437L);
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationService#getAll(SearchRequest, Integer, Integer, String, boolean, Long)}.
     */
    @Test
    public void testGetAll() throws Exception{
        DashboardFilters dashboardFilters = new DashboardFilters();
        dashboardFilters.setValue("33");

        ApiListResponse<ClickDestination> response = clickDestinationService.getAll(null,33,33
                ,"sort",true,3832L);
        assertNotNull(response);
    }

    @Test
    public void testGetAllR() throws Exception{
        DashboardFilters dashboardFilters = new DashboardFilters();
        dashboardFilters.setValue("33");
        List<DashboardFilters> list = new ArrayList<>();
        list.add(dashboardFilters);
        SearchRequest request = new SearchRequest();
        request.setFilters(null);
        exceptionRule.expect(Exception.class);
        ApiListResponse<ClickDestination> response = clickDestinationService.getAll(request,33,33
                ,"sort",true,3832L);
    }

    @Test
    public void testGetAl() throws Exception{
        DashboardFilters dashboardFilters = new DashboardFilters();
        dashboardFilters.setValue("33");
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createClickDestination());
        Mockito.when(cacheService.fetchClickDestination(Mockito.any(),Mockito.anyString(),Mockito.anyBoolean()))
        .thenReturn(list);
        ApiListResponse<ClickDestination> response = clickDestinationService.getAll(null,33,33
                ,"sort",true,3832L);
        assertNotNull(response);
        assertEquals(1,response.getTotalNoOfRecords());
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationService#getMmpParameters(Long)}.
     */
    @Test
    public void testGetMmpParameters() throws Exception {
        ClickDestinationAutomationUrls urls = new ClickDestinationAutomationUrls();
        urls.setAndroidClickUrl("http://komli.com");
        urls.setAndroidS2sUrl("http://komli.com");
        urls.setIosClickUrl("http://komli.com");
        urls.setMmpId(6L);
        urls.setFallBackUrlStatic("http://komli.com");
        urls.setIosS2sUrl("http://komli.com");
        MobileMeasurementPartner partner = new MobileMeasurementPartner();
        partner.setId(4523L);
        partner.setAndroidClickUrl("https://komli.com");
        partner.setActive(true);
        AdvertiserSettings settings = new AdvertiserSettings();
        settings.setAdvertiserId(34L);
        settings.setMmp(MockDataGenerator.createBaseModel());
        settings.setFeedKey("test");
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(settings);
        Mockito.when(advertiserService.getSettingsById(Mockito.anyLong())).thenReturn(responseObject);
        Mockito.when(entityESService.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(partner);
        Mockito.when(modelConverterService.populateClickDestinationForParameters(Mockito.any())).thenReturn(urls);
        ApiResponseObject<ClickDestinationAutomationUrls> response = clickDestinationService.getMmpParameters(2324L);
        assertNotNull(response);
        assertEquals("http://komli.com",response.getRespObject().getAndroidClickUrl());
    }

    @Test
    public void testGetMmpParameter() throws Exception {
        ClickDestinationAutomationUrls urls = new ClickDestinationAutomationUrls();
        urls.setAndroidClickUrl("http://komli.com");
        urls.setAndroidS2sUrl("http://komli.com");
        urls.setIosClickUrl("http://komli.com");
        urls.setMmpId(6L);
        urls.setFallBackUrlStatic("http://komli.com");
        urls.setIosS2sUrl("http://komli.com");
        AdvertiserSettings settings = new AdvertiserSettings();
        settings.setAdvertiserId(34L);
        settings.setMmp(MockDataGenerator.createBaseModel());
        settings.setFeedKey("test");
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(settings);
        Mockito.when(advertiserService.getSettingsById(Mockito.anyLong())).thenReturn(responseObject);
        Mockito.when(modelConverterService.populateClickDestinationForParameters(Mockito.any())).thenReturn(urls);
        ApiResponseObject<ClickDestinationAutomationUrls> response = clickDestinationService.getMmpParameters(2324L);
        assertNotNull(response);
    }

    @Test
    public void testGetMmpParameterSettings() throws Exception {
        ClickDestinationAutomationUrls urls = new ClickDestinationAutomationUrls();
        urls.setAndroidClickUrl("http://komli.com");
        urls.setAndroidS2sUrl("http://komli.com");
        urls.setIosClickUrl("http://komli.com");
        urls.setMmpId(6L);
        urls.setFallBackUrlStatic("http://komli.com");
        urls.setIosS2sUrl("http://komli.com");
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(null);
        Mockito.when(advertiserService.getSettingsById(Mockito.anyLong())).thenReturn(responseObject);
        Mockito.when(modelConverterService.populateClickDestinationForParameters(Mockito.any())).thenReturn(urls);
        ApiResponseObject<ClickDestinationAutomationUrls> response = clickDestinationService.getMmpParameters(2324L);
        assertNotNull(response);
    }
}
