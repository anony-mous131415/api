package io.revx.api.service.audience.impl;

import com.nimbusds.oauth2.sdk.id.Audience;
import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.Status;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.entity.RuleComponent;
import io.revx.api.mysql.amtdb.entity.SegmentPixelMap;
import io.revx.api.mysql.amtdb.entity.SegmentType;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.RuleComponentRepository;
import io.revx.api.mysql.amtdb.repo.SegmentPixelMapRepository;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.mysql.crmdb.entity.ServerFetchConfigEntity;
import io.revx.api.mysql.crmdb.repo.ServerFetchConfigRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserSegmentMappingRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.audience.AudienceUtils;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.DurationUnit;
import io.revx.core.exception.ApiException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.*;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AudienceServiceImplTest extends BaseTestService {

    @Mock
    private AudienceCacheService audienceCacheService;

    @Mock
    private SegmentPixelMapRepository segmentPixelMapRepository;

    @Mock
    private AdvertiserRepository advertiserRepository;

    @Mock
    private AudienceUtils audienceUtils;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private ServerFetchConfigRepository serverFetchConfigRepository;

    @Mock
    private SegmentsRepository segmentsRepository;

    @Mock
    private DataPixelServiceImpl dataPixelService;

    @Mock
    private AdvertiserSegmentMappingRepository advertiserSegmentMappingRepository;

    @Mock
    private CustomESRepositoryImpl elastic;

    @Mock
    private CrmServiceImpl crmService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private RuleComponentRepository ruleComponentRepository;

    @InjectMocks
    private AudienceServiceImpl audienceService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        audienceUtils = new AudienceUtils();
        audienceService.audienceUtils = audienceUtils;
        audienceService.elastic = elastic;
        audienceUtils.setElasticSearch(elasticSearch);
        audienceUtils.setLoginUserDetailsService(loginUserDetailsService);
        audienceUtils.setDataPixelService(dataPixelService);
        audienceUtils.setSegmentPixelMapRepository(segmentPixelMapRepository);
        audienceUtils.setRuleComponentRepository(ruleComponentRepository);
        audienceUtils.setApplicationProperties(applicationProperties);
        audienceUtils.setCrmService(crmService);
        audienceService.audienceCacheService = audienceCacheService;
        audienceService.advertiserRepository = advertiserRepository;
        audienceService.segmentPixelMapRepository = segmentPixelMapRepository;
        audienceService.dataPixelServiceImpl = dataPixelService;
        audienceService.crmServiceImpl = crmService;
        audienceService.segmentsRepository = segmentsRepository;
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#createAudience(io.revx.core.model.audience.AudienceDTO)}.
     */
    @Test
    public void testCreateAudience() throws Exception{
        List<RuleComponent> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponent());
        List<SegmentPixelMap> map = new ArrayList<>();
        map.add(MockDataGenerator.createSegmentPixelMap());
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Honda");
        advertiser.setId(375L);
        advertiser.setCreatedBy(20191001095943L);
        advertiser.setCreationTime(20191001095943L);
        advertiser.setActive(true);
        advertiser.setLicensee(MockDataGenerator.createBaseModel());
        advertiser.setLicenseeId(1L);
        advertiser.setCurrency(MockDataGenerator.createBaseModel());
        advertiser.setTimeZoneId(372L);
        when(crmService.getPixelDataFileByPixelId(Mockito.anyLong())).thenReturn(MockDataGenerator.getPixelDataFileDTO());
        when(dataPixelService.getDataPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.getDataPixelDTO());
        when(ruleComponentRepository.findBySegmentPixelExpressionId(Mockito.any())).thenReturn(list);
        when(segmentPixelMapRepository.findBySegmentId(Mockito.any())).thenReturn(map);
        when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        when(dataPixelService.getAdvertiserToPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.advertiserToPixelEntity());
        when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2314L);
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(advertiser);
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(MockDataGenerator.createAudienceDTO());
        assertNotNull(response);
        assertEquals("Clicker Audience",response.getRespObject().getDescription());
    }

    @Test
    public void testCreateAudienceDMP() throws Exception{
        PixelDataScheduleDTO dto = new PixelDataScheduleDTO();
        dto.setId(33L);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setDuration(1L);
        audienceDTO.setPixelId(2028L);
        audienceDTO.setId(375L);
        audienceDTO.setName(" Honda "+ " Clickers " + "R1");
        audienceDTO.setDescription("Clicker Audience");
        audienceDTO.setCreationTime(System.currentTimeMillis() / 1000);
        audienceDTO.setCreatedBy(MockDataGenerator.createStatusTimeModel().getCreatedBy());
        audienceDTO.setLicensee(MockDataGenerator.createBaseModel());
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setPixelDataSchedule(dto);
        audienceDTO.setSegmentType(SegmentType.CLICKER.id);
        audienceDTO.setActive(Boolean.TRUE);
        audienceDTO.setRuleExpression(MockDataGenerator.getRuleDTO());
        audienceDTO.setDurationUnit(DurationUnit.DAY);
        audienceDTO.setUserDataType(UserDataType.MOBILE_APP.id);
        audienceDTO.setDataSourceType(DataSourceType.AUDIENCE_FEED.id);
        List<RuleComponent> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponent());
        List<SegmentPixelMap> map = new ArrayList<>();
        map.add(MockDataGenerator.createSegmentPixelMap());
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Honda");
        advertiser.setId(375L);
        advertiser.setCreatedBy(20191001095943L);
        advertiser.setCreationTime(20191001095943L);
        advertiser.setActive(true);
        advertiser.setLicensee(MockDataGenerator.createBaseModel());
        advertiser.setLicenseeId(1L);
        advertiser.setCurrency(MockDataGenerator.createBaseModel());
        advertiser.setTimeZoneId(372L);
        when(crmService.getPixelDataFileByPixelId(Mockito.anyLong())).thenReturn(MockDataGenerator.getPixelDataFileDTO());
        when(dataPixelService.getDataPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.getDataPixelDTO());
        when(ruleComponentRepository.findBySegmentPixelExpressionId(Mockito.any())).thenReturn(list);
        when(segmentPixelMapRepository.findBySegmentId(Mockito.any())).thenReturn(map);
        when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        when(dataPixelService.getAdvertiserToPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.advertiserToPixelEntity());
        when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2314L);
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(advertiser);
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
        assertNotNull(response);
        assertEquals("Clicker Audience",response.getRespObject().getDescription());
    }

    @Test
    public void testCreateAudienceFile() throws Exception{
        PixelDataScheduleDTO dto = new PixelDataScheduleDTO();
        dto.setId(33L);
        PixelDataFileDTO pixelDataFileDTO = new PixelDataFileDTO();
        pixelDataFileDTO.setPixelId(3425L);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setDuration(1L);
        audienceDTO.setPixelId(2028L);
        audienceDTO.setId(375L);
        audienceDTO.setName(" Honda "+ " Clickers " + "R1");
        audienceDTO.setDescription("Clicker Audience");
        audienceDTO.setCreationTime(System.currentTimeMillis() / 1000);
        audienceDTO.setCreatedBy(MockDataGenerator.createStatusTimeModel().getCreatedBy());
        audienceDTO.setLicensee(MockDataGenerator.createBaseModel());
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setPixelDataSchedule(dto);
        audienceDTO.setSegmentType(SegmentType.CLICKER.id);
        audienceDTO.setActive(Boolean.TRUE);
        audienceDTO.setRuleExpression(MockDataGenerator.getRuleDTO());
        audienceDTO.setDurationUnit(DurationUnit.DAY);
        audienceDTO.setUserDataType(UserDataType.MOBILE_APP.id);
        audienceDTO.setPixelDataFile(pixelDataFileDTO);
        audienceDTO.setDataSourceType(DataSourceType.FILE_UPLOAD.id);
        List<RuleComponent> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponent());
        List<SegmentPixelMap> map = new ArrayList<>();
        map.add(MockDataGenerator.createSegmentPixelMap());
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Honda");
        advertiser.setId(375L);
        advertiser.setCreatedBy(20191001095943L);
        advertiser.setCreationTime(20191001095943L);
        advertiser.setActive(true);
        advertiser.setLicensee(MockDataGenerator.createBaseModel());
        advertiser.setLicenseeId(1L);
        advertiser.setCurrency(MockDataGenerator.createBaseModel());
        advertiser.setTimeZoneId(372L);
        when(crmService.getPixelDataFileByPixelId(Mockito.anyLong())).thenReturn(MockDataGenerator.getPixelDataFileDTO());
        when(dataPixelService.getDataPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.getDataPixelDTO());
        when(ruleComponentRepository.findBySegmentPixelExpressionId(Mockito.any())).thenReturn(list);
        when(segmentPixelMapRepository.findBySegmentId(Mockito.any())).thenReturn(map);
        when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        when(dataPixelService.getAdvertiserToPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.advertiserToPixelEntity());
        when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2314L);
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(advertiser);
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
        assertNotNull(response);
        assertEquals("Clicker Audience",response.getRespObject().getDescription());
    }

    @Test
    public void testCreateAudienceNotValidAdvertiser() throws Exception{
        exceptionRule.expect(ApiException.class);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(MockDataGenerator.createAudienceDTO());
    }

    @Test
    public void testCreateAudienceNotValidAdvertiserUpdateFalse() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDurationNull() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDurationZero() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(0L);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDurationUnitNull() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(null);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidSegmentType() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(5);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDataSourceType() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(3);
        audienceDTO.setDataSourceType(null);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDataRuleExpression() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(null);
        audienceDTO.setDataSourceType(DataSourceType.PIXEL_LOG.id);
        audienceDTO.setRuleExpression(null);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidDataRuleExpressions() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(null);
        audienceDTO.setDataSourceType(DataSourceType.AUDIENCE_FEED.id);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidFileUpload() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(null);
        audienceDTO.setDataSourceType(DataSourceType.FILE_UPLOAD.id);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceNotValidSegmentTypes() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setSegmentType(SegmentType.DMP.id);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    @Test
    public void testCreateAudienceWithNull() throws Exception{
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(null);
    }

    @Test
    public void testCreateAudienceWithAdvertiserNull() throws Exception{
        exceptionRule.expect(ApiException.class);
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(null);
        ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audienceDTO);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#updateAudience(io.revx.core.model.audience.AudienceDTO)}.
     */
    @Test
    public void testUpdateAudience() throws Exception{
        List<RuleComponent> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponent());
        List<SegmentPixelMap> map = new ArrayList<>();
        map.add(MockDataGenerator.createSegmentPixelMap());
        DataPixelDTO dataPixelDTO = new DataPixelDTO();
        dataPixelDTO.setSourceType(DataSourceType.PIXEL_LOG);
        dataPixelDTO.setDescription("test");
        dataPixelDTO.setName("SafariTest");
        dataPixelDTO.setUuCount(2L);
        dataPixelDTO.setPiCount(31415926535898L);
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Honda");
        advertiser.setId(375L);
        advertiser.setCreatedBy(20191001095943L);
        advertiser.setCreationTime(20191001095943L);
        advertiser.setActive(true);
        advertiser.setLicensee(MockDataGenerator.createBaseModel());
        advertiser.setLicenseeId(1L);
        advertiser.setCurrency(MockDataGenerator.createBaseModel());
        advertiser.setTimeZoneId(372L);
        when(crmService.getPixelDataFileByPixelId(Mockito.anyLong())).thenReturn(MockDataGenerator.getPixelDataFileDTO());
        when(dataPixelService.getDataPixel(Mockito.anyLong())).thenReturn(dataPixelDTO);
        when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        when(ruleComponentRepository.findBySegmentPixelExpressionId(Mockito.any())).thenReturn(list);
        when(segmentPixelMapRepository.findBySegmentId(Mockito.any())).thenReturn(map);
        when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
        when(dataPixelService.getAdvertiserToPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.advertiserToPixelEntity());
        when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2314L);
        when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(advertiser);
        ApiResponseObject<AudienceDTO> response = audienceService.updateAudience(MockDataGenerator.createAudienceDTO());
        assertNotNull(response);
        assertEquals("Clicker Audience",response.getRespObject().getDescription());
    }

    @Test
    public void testUpdateAudienceWithBadRequest() throws  Exception {
        Mockito.when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2324L);
        exceptionRule.expect(ApiException.class);
        audienceService.updateAudience(MockDataGenerator.createAudienceDTO());
    }

    @Test
    public void testUpdateAudienceWithNull() throws  Exception {
        Mockito.when(dataPixelService.createAdvertiserToPixel(Mockito.any(),Mockito.any())).thenReturn(2324L);
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        List<SegmentPixelMap> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        exceptionRule.expect(ApiException.class);
        audienceService.updateAudience(MockDataGenerator.createAudienceDTO());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#activate(String)}.
     */
    @Test
    public void testActivate() throws Exception {
        ApiResponseObject<Map<Integer, ResponseMessage>> response = audienceService.activate("1234");
        assertNotNull(response);
    }

    @Test
    public void testActivateWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        audienceService.activate(null);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#deactivate(String)}.
     */
    @Test
    public void testDeactivate() throws Exception {
        ApiResponseObject<Map<Integer, ResponseMessage>> response = audienceService.deactivate("1234");
        assertNotNull(response);
    }

    @Test
    public void testDeactivateWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        audienceService.deactivate(null);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#syncRemoteAudience(Long)}.
     */
    @Test
    public void testSyncRemoteAudienceSuccess() throws Exception {
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        List<SegmentPixelMap> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(crmService.forceSyncAction(Mockito.anyLong())).thenReturn(CrmStatus.SUCCESS);
        ApiResponseObject<BaseModel> response = audienceService.syncRemoteAudience(1234L);
        assertEquals("SUCCESS",response.getRespObject().getName());
    }

    @Test
    public void testSyncRemoteAudienceRunning() throws Exception {
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        List<SegmentPixelMap> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(crmService.forceSyncAction(Mockito.anyLong())).thenReturn(CrmStatus.RUNNING);
        ApiResponseObject<BaseModel> response = audienceService.syncRemoteAudience(1234L);
        assertEquals("RUNNING",response.getRespObject().getName());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#checkConnection(PixelRemoteConfigDTO)}.
     */
    @Test
    public void testCheckConnection() throws Exception {
        PixelRemoteConfigDTO pixel = new PixelRemoteConfigDTO();
        pixel.setPassword("password");
        ApiResponseObject<BaseModel> response = audienceService.checkConnection(pixel);
        assertNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#updateStatus(List, Status)}.
     */
    @Test
    public void testUpdateStatus() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(234L);
        idList.add(345L);
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        Mockito.when(segmentsRepository.updateStatus(Mockito.any(),Mockito.anyLong())).thenReturn(2353);
        Map<Integer, ResponseMessage> response = audienceService.updateStatus(idList,Status.ACTIVE);
        assertEquals("id already active",response.get(345).getMessage());
    }

    @Test
    public void testUpdateStatusSegmentType() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(234L);
        idList.add(345L);
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        Mockito.when(segmentsRepository.updateStatus(Mockito.any(),Mockito.anyLong())).thenReturn(2353);
        Map<Integer, ResponseMessage> response = audienceService.updateStatus(idList,Status.INACTIVE);
        assertEquals("success",response.get(345).getMessage());
    }

    @Test
    public void testUpdateStatusSegmentTypeEntityESService() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(234L);
        idList.add(345L);
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        Mockito.when(elastic.findDetailById(Mockito.anyString(),Mockito.anyString(),Mockito.any()))
                .thenReturn(MockDataGenerator.createAudienceESDTO());
        Mockito.when(segmentsRepository.updateStatus(Mockito.any(),Mockito.anyLong())).thenReturn(2353);
        Map<Integer, ResponseMessage> response = audienceService.updateStatus(idList,Status.INACTIVE);
        assertEquals("success",response.get(345).getMessage());
    }

    @Test
    public void testUpdateStatusSegmentTypeNegative() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(234L);
        idList.add(345L);
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        Mockito.when(segmentsRepository.updateStatus(Mockito.any(),Mockito.anyLong())).thenReturn(-22);
        Map<Integer, ResponseMessage> response = audienceService.updateStatus(idList,Status.INACTIVE);
        assertEquals("Internal DB Error",response.get(345).getMessage());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#getAudience(Long, Boolean)}.
     */
    @Test
    public void testGetAudience() throws Exception {
        List<RuleComponent> ruleComponents = new ArrayList<>();
        ruleComponents.add(MockDataGenerator.getRuleComponent());
        List<SegmentPixelMap> list =new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(audienceCacheService.fetchAudience(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createSegments());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(dataPixelService.getAdvertiserToPixel(Mockito.anyLong())).thenReturn(MockDataGenerator
                .advertiserToPixelEntity());
        Mockito.when(dataPixelService.getDataPixel(Mockito.anyLong())).thenReturn(MockDataGenerator.getDataPixelDTO());
        Mockito.when(ruleComponentRepository.findBySegmentPixelExpressionId(Mockito.anyLong()))
                .thenReturn(ruleComponents);
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createBaseModel());
        ApiResponseObject<AudienceDTO> response = audienceService.getAudience(3432L,false);
        assertNotNull(response);
    }

    @Test
    public void testGetAudienceWithInvalidId() throws Exception {
        exceptionRule.expect(ApiException.class);
        audienceService.getAudience(4536L,true);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#getAllAudience(Long, int, int, String, SearchRequest, Boolean)}.
     */
    @Test
    public void testGetAllAudience() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        List<AudienceESDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.createAudienceESDTO());
        List<List<AudienceESDTO>> lists = new ArrayList<>();
        lists.add(list);
        ApiListResponse<List<AudienceESDTO>> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(lists);
        Mockito.when(audienceCacheService.fetchAllAudience(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),
                Mockito.anyString(),Mockito.any(),Mockito.anyBoolean())).thenReturn(apiListResponse);
        ApiListResponse<List<AudienceESDTO>> response = audienceService.getAllAudience(4352L,0,3,"1",searchRequest,true);
        assertEquals("Honda",response.getData().get(0).get(0).getName());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#getAcces(Long)}.
     */
    @Test
    public void testGetAcces() throws Exception {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3452L);
        Mockito.when(advertiserRepository.findByIdAndLicenseeId(Mockito.any(),Mockito.any())).thenReturn(MockDataGenerator.createAdvertiserEntity());
        ApiResponseObject<AudienceAccessDTO> response = audienceService.getAcces(2345L);
        assertEquals(true,response.getRespObject().getIsDmpAccess());
    }

    @Test
    public void testGetAccesWithNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3452L);
        Mockito.when(advertiserRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(null);
        audienceService.getAcces(2345L);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#getDmpAudience(Long, Integer, Integer, Integer)}.
     */
    @Test
    public void testGetDmpAudience() throws Exception {
        ApiResponseObject<DmpAudienceDTO> response = audienceService.getDmpAudience(4354L,null,null,34);
        assertNotNull(response);
        ApiResponseObject<DmpAudienceDTO> resp = audienceService.getDmpAudience(4357L,null,null,34);
        assertNotNull(resp);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#getSyncedDmpAudience(Long)}.
     */
    @Test
    public void testGetSyncedDmpAudience() throws Exception {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3456L);
        List<Long> list = new ArrayList<>();
        list.add(345L);
        list.add(453L);
        Mockito.when(advertiserSegmentMappingRepository.findSegmentIdByAdvertiserIdAndLicenseeId(Mockito.anyLong()
                ,Mockito.anyLong())).thenReturn(list);
        List<Segments> audiences = new ArrayList<>();
        audiences.add(MockDataGenerator.createSegments());
        Mockito.when(segmentsRepository.findAllById(Mockito.any())).thenReturn(audiences);
        ApiResponseObject<List<AudienceDTO>> response = audienceService.getSyncedDmpAudience(5234L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceServiceImpl#syncPlatformAudience(PlatformAudienceDTO)}.
     */
    @Test
    public void testSyncPlatformAudienceFalse() throws Exception {
        PlatformAudienceDTO platformAudienceDTO = new PlatformAudienceDTO();
        List<SegmentPixelMap> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(segmentsRepository.findByRemoteSegmentId(Mockito.anyString())).thenReturn(MockDataGenerator.createSegments());
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        Boolean response = audienceService.syncPlatformAudience(platformAudienceDTO);
        assertFalse(response);
    }

    @Test
    public void testSyncPlatformAudienceTrue() throws Exception {
        ServerFetchConfigEntity entity = new ServerFetchConfigEntity();
        entity.setId(33L);
        PlatformAudienceDTO platformAudienceDTO = new PlatformAudienceDTO();
        platformAudienceDTO.setUrl("http://www.komli.com");
        platformAudienceDTO.setContainer_id("3414");
        List<SegmentPixelMap> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegmentPixelMap());
        Mockito.when(applicationProperties.getS3SegmentBucketUrlPath()).thenReturn("http://www.komli.com");
        Mockito.when(serverFetchConfigRepository.findByPixelId(Mockito.anyLong())).thenReturn(entity);
        Mockito.when(segmentsRepository.findByRemoteSegmentId(Mockito.anyString())).thenReturn(MockDataGenerator.createSegments());
        Mockito.when(segmentPixelMapRepository.findBySegmentId(Mockito.anyLong())).thenReturn(list);
        Boolean response = audienceService.syncPlatformAudience(platformAudienceDTO);
        assertTrue(response);
    }
}
