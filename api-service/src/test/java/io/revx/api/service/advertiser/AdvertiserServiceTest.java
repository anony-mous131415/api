package io.revx.api.service.advertiser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import java.util.*;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;
import io.revx.api.mysql.amtdb.repo.DataPixelsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.appsettings.AppSettingsService;
import io.revx.api.service.audience.impl.AudienceServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.pixel.Tag;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.model.requests.SkadTargetPrivileges;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.UserInfo;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdvertiserServiceTest extends BaseTestService {

    @InjectMocks
    private AdvertiserService advertiserService;

    @Mock
    private EntityESService entityESService;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private AdvertiserRepository advertiserRepository;

    @Mock
    private AdvertiserToPixelRepository advToPixelRepo;

    @Mock
    private ValidationService validationService;

    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private DataPixelsRepository dataPixelsRepository;

    @Mock
    private DataPixelServiceImpl dataPixelService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private CustomESRepositoryImpl elastic;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private AudienceServiceImpl audService;

    @Mock
    private CacheService cacheService;

    @Mock
    private ASTService astService;

    @Mock
    private AdvertiserCacheService advertiserCacheService;

    @Mock
    private AppSettingsService appSettingsService;

    @Mock
    private Tag tag;

    @Mock
    private AudienceDTO audienceDTO;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        advertiserCacheService = new AdvertiserCacheService();
        astService = new ASTService();
        cacheService = new CacheService();
        astService.dataPixelService = dataPixelService;
        astService.elasticSearch = elasticSearch;
        astService.loginUserDetailsService = loginUserDetailsService;
        astService.advertiserRepo = advertiserRepository;
        astService.advertiserToPixelRepo = advToPixelRepo;
        advertiserCacheService.loginUserDetailsService = loginUserDetailsService;
        advertiserCacheService.cacheService = cacheService;
        advertiserCacheService.elasticService = elasticSearch;
        advertiserCacheService.modelConverterService = modelConverter;
        advertiserCacheService.validationService = validationService;
        advertiserCacheService.advertiserRepository = advertiserRepository;
        advertiserService.loginUserDetailsService = loginUserDetailsService;
        advertiserService.advertiserCacheService = advertiserCacheService;
        advertiserService.advertiserRepository = advertiserRepository;
        advertiserService.modelConverter = modelConverter;
        advertiserService.advToPixelRepo = advToPixelRepo;
        advertiserService.dataPixelsRepository = dataPixelsRepository;
        advertiserService.properties = properties;
        advertiserService.dataPixelService = dataPixelService;
        advertiserService.elasticSearch = elasticSearch;
        advertiserService.elastic = elastic;
        advertiserService.audService = audService;
        advertiserService.astService = astService;
        advertiserService.appSettingsService = appSettingsService;
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#create(AdvertiserPojo)}.
     */
    @Test
    public void testCreate() throws Exception{
        BaseModel baseModel = new BaseModel();
        baseModel.setId(3875L);
        baseModel.setName("Honda");
        List<AppSettingsDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> apiResponse = new ApiResponseObject<>();
        apiResponse.setRespObject(list);
        Mockito.when(modelConverter.populateAdvEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createAdvertiserEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
        Mockito.when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(123L);
        Mockito.when(elastic.save(Mockito.any(),Mockito.any())).thenReturn("Honda");
        Mockito.when(modelConverter.populateAdvertiserFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserPojo());
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(audService.createAudience(Mockito.any())).thenReturn(resp);
        Mockito.when(appSettingsService.createSettings(Mockito.any())).thenReturn(apiResponse);
        ApiResponseObject<AdvertiserPojo> apiResponseObject = advertiserService.
                create(MockDataGenerator.createAdvertiserPojo());
        assertNotNull(apiResponseObject);
        assertEquals("tribal@test.com",apiResponseObject.getRespObject().getEmail());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#getById(Long, boolean)}.
     */
    @Test
    public void testGetById() throws Exception{
        List<AppSettingsDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> api = new ApiResponseObject<>();
        api.setRespObject(list);
        Optional<AdvertiserEntity> optional = Optional.of(MockDataGenerator.createAdvertiserEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(modelConverter.populateAdvertiserFromEntity(Mockito.any())).thenReturn(MockDataGenerator.createAdvertiserPojo());
        Mockito.when(advertiserRepository.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(appSettingsService.getSettings(Mockito.any(),Mockito.anyLong()))
                .thenReturn(api);
        ApiResponseObject<AdvertiserPojo> apiResponseObject = advertiserService.getById(123L,true);
        assertNotNull(apiResponseObject);
        assertEquals("Honda",apiResponseObject.getRespObject().getName());
        assertEquals("tribal",apiResponseObject.getRespObject().getContactAddress());
        ApiResponseObject<AdvertiserPojo> apiResponse = advertiserService.getById(123L,true);
        assertNotNull(apiResponse);
        assertEquals("Honda",apiResponse.getRespObject().getName());
        assertEquals("tribal",apiResponse.getRespObject().getContactAddress());
    }

    @Test
    public void testGetByIdAdvertiserEntityNull() throws Exception{
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(modelConverter.populateAdvertiserFromEntity(Mockito.any())).thenReturn(MockDataGenerator.createAdvertiserPojo());
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<AdvertiserPojo> apiResponseObject = advertiserService.getById(123L,true);
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#getSettingsById(Long)}.
     */
    @Test
    public void testGetSettingsById() throws Exception{
        Optional<AdvertiserEntity> adv = Optional.of(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findById(Mockito.anyLong())).thenReturn(adv);
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        ApiResponseObject<AdvertiserSettings> apiResponseObject = advertiserService.getSettingsById(1323L);
        assertNotNull(apiResponseObject);
        assertEquals("Honda",apiResponseObject.getRespObject().getMmp().getName());
    }

    @Test
    public void testGetSettingsByNullId() throws Exception{
        Optional<AdvertiserEntity> adv = Optional.of(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findById(Mockito.anyLong())).thenReturn(adv);
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        exceptionRule.expect(ApiException.class);
        advertiserService.getSettingsById(null);
    }

    @Test
    public void testGetSettingsByInvalidAdvID() throws Exception{
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        exceptionRule.expect(ApiException.class);
        advertiserService.getSettingsById(2345L);

    }

    /**
     * Test method for {@link AdvertiserService#findAll()}.
     */
    @Test
    public void testGetSmartTag() throws Exception{
        AdvertiserToPixelEntity advertiserToPixelEntity = new AdvertiserToPixelEntity();
        advertiserToPixelEntity.setAdvertiserId(1232L);
        advertiserToPixelEntity.setPixelId(13L);
        advertiserToPixelEntity.setId(123L);
        DataPixelsEntity dataPixelsEntity = new DataPixelsEntity();
        dataPixelsEntity.setHash("");
        dataPixelsEntity.setName("Honda");
        String str= "/atom/origin/ast";
        Optional <DataPixelsEntity> optionalDataPixelsEntity = Optional.of(dataPixelsEntity);
        Optional<AdvertiserToPixelEntity> adv = Optional.of(advertiserToPixelEntity);
        Mockito.when(advToPixelRepo.getASTpixelByAdvId(Mockito.any(), Mockito.any())).thenReturn(adv);
        Mockito.when(dataPixelsRepository
                .findByIdAndStatus(Mockito.any(),Mockito.any())).thenReturn(optionalDataPixelsEntity);
        Mockito.when(properties.getSmartTagOriginDirectory()).thenReturn(str);
        ApiResponseObject<Tag> apiResponseObject = advertiserService.getSmartTag(123L);
        assertNotNull(apiResponseObject);
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#update(AdvertiserSettings, Long)}.
     */
    @Test
    public void testUpdateAdvertiserSettings() throws Exception {
        Optional<AdvertiserEntity> adv = Optional.of(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findById(Mockito.any())).thenReturn(adv);
        Mockito.when(advertiserRepository.save(Mockito.any())).
                thenReturn(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        ApiResponseObject<AdvertiserSettings> response = advertiserService
                .update(MockDataGenerator.createAdvertiserSettings(),123L);
        assertNotNull(response);
        assertEquals("Honda",response.getRespObject().getMmp().getName());

    }

    @Test
    public void testUpdateAdvertiserSettingsWithInvalidParameter() throws Exception {
        Mockito.when(advertiserRepository.save(Mockito.any())).
                thenReturn(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("INVALID_PARAMETER_IN_REQUEST");
        advertiserService.update(MockDataGenerator.createAdvertiserSettings(),3875L);

    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#update(AdvertiserPojo)}.
     */
    @Test
    public void testUpdate() throws Exception {
        FilterComponent filterComponent = new FilterComponent(Filter.ADVERTISER_ID,"Honda");
        Set<FilterComponent> set = new HashSet<>();
        set.add(filterComponent);
        Map<FilterType, Set<FilterComponent>> tableFilters = new HashMap<>();
        tableFilters.put(FilterType.TABLE_COLUMN, set);
        tableFilters.put(FilterType.DATA_FIELD, set);
        List<AppSettingsDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> apiResponse = new ApiResponseObject<>();
        apiResponse.setRespObject(list);
        mockSecurityContext("akhilesh", false, false);
        Optional<AdvertiserEntity> optionalAdvertiserEntity = Optional.of(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findById(Mockito.any())).thenReturn(optionalAdvertiserEntity);
        Mockito.when(advertiserRepository.save(Mockito.any())).thenReturn(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(modelConverter.populateAdvertiserFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserPojo());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createAdvertiser());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(1323L);
        Mockito.when(appSettingsService.updateSettings(Mockito.any())).thenReturn(apiResponse);
        ApiResponseObject<AdvertiserPojo> response = advertiserService.update(MockDataGenerator.createAdvertiserPojo());
        assertNotNull(response);
        assertEquals("Honda",response.getRespObject().getName());
    }

    @Test
    public void testUpdateWithInvalidParameter() throws Exception{
        Mockito.when(advertiserRepository.save(Mockito.any())).
                thenReturn(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(modelConverter.populateAdvSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiserSettings());
        exceptionRule.expect(ApiException.class);
        advertiserService.update(MockDataGenerator.createAdvertiserPojo());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#activate(String)}.
     */
    @Test
    public void testActivate() throws Exception{
        Set<Long> ids = new HashSet<>();
        ids.add(102L);
        ids.add(103L);
        Mockito.when(modelConverter.getSetOfIds(Mockito.any())).thenReturn(ids);
        List<AdvertiserEntity> advertiserEntities = new ArrayList<>();
        advertiserEntities.add(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findByIdIn(Mockito.any())).thenReturn(advertiserEntities);
        ApiResponseObject<Map<Long, ResponseMessage>> response = advertiserService.activate("102");
        assertNotNull(response);
        assertEquals(3,response.getRespObject().values().size());
    }

    @Test
    public void testActivateWhenIdNull() throws Exception{
        Set<Long> ids = new HashSet<>();
        ids.add(102L);
        ids.add(103L);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
        Mockito.when(modelConverter.getSetOfIds(Mockito.any())).thenReturn(ids);
        List<AdvertiserEntity> advertiserEntities = new ArrayList<>();
        advertiserEntities.add(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findByIdIn(Mockito.any())).thenReturn(advertiserEntities);
        Mockito.when(elastic.findDetailById(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiser());
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyInt()))
                .thenReturn(MockDataGenerator.createAdvertiser());
        ApiResponseObject<Map<Long, ResponseMessage>> response = advertiserService.activate("");
        assertNotNull(response);
        assertEquals(3,response.getRespObject().values().size());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#deactivate(String)}.
     */
    @Test
    public void testDeactivate() throws Exception{
        Set<Long> ids = new HashSet<>();
        ids.add(102L);
        Mockito.when(modelConverter.getSetOfIds(Mockito.any())).thenReturn(ids);
        List<AdvertiserEntity> advertiserEntities = new ArrayList<>();
        advertiserEntities.add(MockDataGenerator.createAdvertiserEntity());
        Mockito.when(advertiserRepository.findByIdIn(Mockito.any())).thenReturn(advertiserEntities);
        Mockito.when(elastic.findDetailById(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(MockDataGenerator.createAdvertiser());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createAdvertiser());
        ApiResponseObject<Map<Long, ResponseMessage>> response = advertiserService.deactivate("102");
        assertNotNull(response);
        assertEquals(2,response.getRespObject().values().size());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#generateAstForAdvertiser(Long)}.
     */
    @Test
    public void testGenerateAstForAdvertiser() throws Exception{
        ElasticSearchTerm es = new ElasticSearchTerm();
        es.setLicenseeId(33L);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyInt())).thenReturn(MockDataGenerator.createBaseModel());
        Mockito.when(dataPixelService.createAndGetAdvertiserToPixel(elasticSearch.searchById(TablesEntity.ADVERTISER, 33L), DataSourceType.PIXEL_LOG))
                .thenReturn(MockDataGenerator.advertiserToPixelEntity());
        ApiResponseObject<Boolean> response = advertiserService.generateAstForAdvertiser(1323L);
        assertNotNull(response);
        assertEquals("false",response.getRespObject().toString());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#generateAstForAllAdvertiser()}.
     */
    @Test
    public void testGenerateAstForAllAdvertiser() throws Exception{
        List<AdvertiserEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createAdvertiserEntity());
        List<AdvertiserToPixelEntity> pixelEntity = new ArrayList<>();
        pixelEntity.add(MockDataGenerator.advertiserToPixelEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(advertiserRepository.findAllByLicenseeId(loginUserDetailsService.getUserInfo()
                .getSelectedLicensee().getId())).thenReturn(list);
        Mockito.when(advToPixelRepo.findAllByAdvertiserId(Mockito.anyLong())).thenReturn(pixelEntity);
        Mockito.when(dataPixelService.createAndGetAdvertiserToPixel(Mockito.any(),Mockito.any()))
                .thenReturn(MockDataGenerator.advertiserToPixelEntity());
        ApiResponseObject<Boolean> response = advertiserService.generateAstForAllAdvertiser();
        assertNotNull(response);
        assertEquals(true,response.getRespObject());
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#findAll()}.
     */
    @Test
    public void testFindAll() throws Exception{
        List<AdvertiserEntity> advList = advertiserService.findAll();
        assertNotNull(advList);
        assertThat(advList.size()).isEqualTo(0);
    }

    /**
     * Test method for {@link io.revx.api.service.advertiser.AdvertiserService#getAll(SearchRequest, Integer, Integer, String)}.
     */
    @Test
    public void testGetAll() throws Exception{
        SearchRequest search = new SearchRequest();
        Integer pageNum = 10;
        Integer resultPerPage = 10;
        String sort = "Honda";
        ApiResponseObject<ApiListResponse<AdvertiserPojo>> response = advertiserService.getAll(search,pageNum,resultPerPage,sort);
        assertNull(response);
    }

    @Test
    public void testGetSkadPrivilegesForAdvertiser() throws ValidationException {
        SearchRequest searchRequest = MockDataGenerator.generateValidRequestForSKADPrivileges();
        List<BaseModel> baseModels = new ArrayList<>();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(1L);
        baseModel.setName("testCampaign");
        baseModels.add(baseModel);

        Mockito.when(entityESService.getDictionaryData(Mockito.any(TablesEntity.class), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(SearchRequest.class), Mockito.anyString())).thenReturn(new DictionaryResponse(1,baseModels));
        Mockito.when(properties.getSkadCampaignCount()).thenReturn("1");
        Mockito.when(elasticSearch.getDictionaryData(Mockito.any(),Mockito.anyInt(),Mockito.anyInt(),Mockito.any()
                ,Mockito.anyString())).thenReturn(MockDataGenerator.getDictionaryResponse());

        ApiResponseObject<SkadTargetPrivileges> result = advertiserService.getSkadTargetPrivileges(searchRequest);
        assertNotNull(result.getRespObject());
        assertFalse(result.getRespObject().isAllowed());
    }

    @Test
    public void testGetSkadPrivilegesForCampaign() throws ValidationException {
        SearchRequest searchRequest = MockDataGenerator.generateValidRequestForSKADPrivileges();
        searchRequest.getFilters().get(0).setColumn("campaignId");
        List<BaseModel> baseModels = new ArrayList<>();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(1L);
        baseModel.setName("testCampaign");
        baseModels.add(baseModel);

        Mockito.when(entityESService.getDictionaryData(Mockito.any(TablesEntity.class), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(SearchRequest.class), Mockito.anyString())).thenReturn(new DictionaryResponse(1,baseModels));
        Mockito.when(properties.getSkadStrategyCount()).thenReturn("1");
        Mockito.when(elasticSearch.getDictionaryData(Mockito.any(),Mockito.anyInt(),Mockito.anyInt(),Mockito.any()
                ,Mockito.anyString())).thenReturn(MockDataGenerator.getDictionaryResponse());

        ApiResponseObject<SkadTargetPrivileges> result = advertiserService.getSkadTargetPrivileges(searchRequest);
        assertNotNull(result.getRespObject());
        assertFalse(result.getRespObject().isAllowed());
    }

    @Test(expected = ValidationException.class)
    public void testGetSkadPrivilegesForInValidRequest() throws ValidationException {
        SearchRequest searchRequest = MockDataGenerator.generateValidRequestForSKADPrivileges();
        DashboardFilters filter = new DashboardFilters();
        filter.setValue("1234");
        filter.setColumn("strategyId");
        searchRequest.getFilters().add(filter);

        advertiserService.getSkadTargetPrivileges(searchRequest);
    }

    @Test(expected = ValidationException.class)
    public void testGetSkadPrivilegesForInValidEntity() throws ValidationException {
        SearchRequest searchRequest = MockDataGenerator.generateValidRequestForSKADPrivileges();
        searchRequest.getFilters().get(0).setColumn("strategyId");

        advertiserService.getSkadTargetPrivileges(searchRequest);
    }
}
