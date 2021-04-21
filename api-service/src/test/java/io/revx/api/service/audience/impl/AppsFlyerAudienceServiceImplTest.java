package io.revx.api.service.audience.impl;

import io.revx.api.audience.pojo.AppsFlyerAudienceCreateDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceSyncDto;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.mysql.entity.LifeTimeAuthenticationEntity;
import io.revx.api.mysql.repo.LifeTimeTokenRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.security.ApiAuthorizationFilter;
import io.revx.api.service.audience.AudienceUtils;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.core.model.audience.PixelDataScheduleDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@RunWith(SpringJUnit4ClassRunner.class)
public class AppsFlyerAudienceServiceImplTest extends BaseTestService {

    @Mock
    LifeTimeTokenRepository lifeTimeTokenRepository;

    @Mock
    DataPixelServiceImpl dataPixelServiceImpl;

    @Mock
    AudienceUtils audienceUtils;

    @Mock
    SegmentsRepository segmentsRepository;

    @Mock
    AudienceServiceImpl audienceService;

    @Mock
    AudienceCacheService audienceCacheService;

    @Mock
    CustomESRepositoryImpl elastic;

    @Mock
    ApplicationProperties properties;

    @Mock
    ApiAuthorizationFilter apiAuthorizationFilter;

    @Mock
    CrmServiceImpl crmService;

    @InjectMocks
    @Spy
    AppsFlyerAudienceServiceImpl appsFlyerAudienceService;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        appsFlyerAudienceService.lifeTimeTokenRepository = lifeTimeTokenRepository;
        appsFlyerAudienceService.dataPixelServiceImpl = dataPixelServiceImpl;
        appsFlyerAudienceService.audienceUtils = audienceUtils;
        appsFlyerAudienceService.segmentsRepository = segmentsRepository;
        appsFlyerAudienceService.audienceService = audienceService;
        appsFlyerAudienceService.audienceCacheService = audienceCacheService;
        appsFlyerAudienceService.elastic = elastic;
        appsFlyerAudienceService.properties = properties;
        appsFlyerAudienceService.apiAuthorizationFilter = apiAuthorizationFilter;
        appsFlyerAudienceService.crmService = crmService;
    }

    @Test
    public void testCreateAppsFlyerAudience() throws Exception {
        AppsFlyerAudienceCreateDto appsFlyerAudienceCreateDto = new AppsFlyerAudienceCreateDto();
        appsFlyerAudienceCreateDto.setName("TestAudience");
        appsFlyerAudienceCreateDto.setPlatform("Windows");
        appsFlyerAudienceCreateDto.setApi_key("eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNp8kMFKw0AQhl9F5hxK0pbE5GTUCoWoEAuCbpA1mda1aTbsblJLydW7T-lrOJtYPVi8DMs__8z_ze5BN88QQYFY8_Wo5mbLzxS2byMhwQGdyxo19dPbZPaU3pMkuIHI872xF4STICBBJ2KJC7HBuDEvC7nGCiKjGnTg1Qia_fx4Z83lqT-hOotd-56GVK_OXbtP2_17Bo1GNS8YRGN3HDoMOG2TShiBmsRHdmBgkDmDu-IbpBY7Ss-AXBpLzA0Wicix0mjdlJQ3SmGV7y5kMczPb9LeznMjWisteamRBNHzeBbnJ-xuK1arHYPODhQtKiOIpWek3eV3Un-J6_wTZujHHmT16zyeHnhT_2_8SVzXhJANDIfzrnk9XEjIlsfOZl0H3RcAAAD__w.lTmLTTsmyvMTRqNWoeJOwyc3UC58LmumASkSwrWcT2w");

        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = getLifeTimeAuthTokenTestData(true);
        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(apiAuthorizationFilter.getAuthentication(Mockito.any(UserInfo.class))).thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        Mockito.when(dataPixelServiceImpl.createAdvertiserToPixel(Mockito.any(Advertiser.class), Mockito.any(DataSourceType.class))).thenReturn(4176L);
        Segments segmentsEntity = TestDataGenerator.getObject(Segments.class);
        Mockito.when(audienceUtils.getEntityFromDto(Mockito.any(AudienceDTO.class))).thenReturn(segmentsEntity);
        Mockito.when(segmentsRepository.save(Mockito.any(Segments.class))).thenReturn(segmentsEntity);
        Mockito.doNothing().when(audienceService).createRuleExpression(Mockito.any(AudienceDTO.class), Mockito.any(Segments.class), Mockito.anyLong());
        Mockito.doNothing().when(audienceUtils).populateAudienceDTO(Mockito.any(Segments.class), Mockito.any(AudienceDTO.class));
        Mockito.when(elastic.save(Mockito.any(BaseModel.class), Mockito.any(TablesEntity.class))).thenReturn("");
        AudienceESDTO audienceESDTO = TestDataGenerator.getObject(AudienceESDTO.class);
        Mockito.doReturn(audienceESDTO).when(appsFlyerAudienceService).getESDTO(Mockito.any(AudienceDTO.class));
        Mockito.doNothing().when(audienceCacheService).remove();
        Long containerId = appsFlyerAudienceService.createAppsFlyerAudience(appsFlyerAudienceCreateDto);
        assertEquals(segmentsEntity.getId(), containerId);
    }

    @Test
    public void testCreateAppsFlyerAudienceValidationFailed() throws Exception {
        AppsFlyerAudienceCreateDto appsFlyerAudienceCreateDto = new AppsFlyerAudienceCreateDto();
        appsFlyerAudienceCreateDto.setName("TestAudience");
        appsFlyerAudienceCreateDto.setPlatform("Windows");
        appsFlyerAudienceCreateDto.setApi_key("sdjkfuiwesfiujkb982498k.jqbefliukjabefkwe98wxssfr");

        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = getLifeTimeAuthTokenTestData(true);
        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(apiAuthorizationFilter.getAuthentication(Mockito.any(UserInfo.class))).thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        Mockito.when(dataPixelServiceImpl.createAdvertiserToPixel(Mockito.any(Advertiser.class), Mockito.any(DataSourceType.class))).thenReturn(4176L);
        Segments segmentsEntity = TestDataGenerator.getObject(Segments.class);
        Mockito.when(audienceUtils.getEntityFromDto(Mockito.any(AudienceDTO.class))).thenReturn(segmentsEntity);
        Mockito.when(segmentsRepository.save(Mockito.any(Segments.class))).thenReturn(segmentsEntity);
        Mockito.doNothing().when(audienceService).createRuleExpression(Mockito.any(AudienceDTO.class), Mockito.any(Segments.class), Mockito.anyLong());
        Mockito.doNothing().when(audienceUtils).populateAudienceDTO(Mockito.any(Segments.class), Mockito.any(AudienceDTO.class));
        Mockito.when(elastic.save(Mockito.any(BaseModel.class), Mockito.any(TablesEntity.class))).thenReturn("");
        AudienceESDTO audienceESDTO = TestDataGenerator.getObject(AudienceESDTO.class);
        Mockito.doReturn(audienceESDTO).when(appsFlyerAudienceService).getESDTO(Mockito.any(AudienceDTO.class));
        Mockito.doNothing().when(audienceCacheService).remove();
        exceptionRule.expect(ValidationException.class);
        Long containerId = appsFlyerAudienceService.createAppsFlyerAudience(appsFlyerAudienceCreateDto);
    }

    LifeTimeAuthenticationEntity getLifeTimeAuthTokenTestData(Boolean isActive){
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        lifeTimeAuthenticationEntity.setId(1L);
        lifeTimeAuthenticationEntity.setActive(isActive);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setUserId(12L);
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken("eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNp8kMFKw0AQhl9F5hxK0pbE5GTUCoWoEAuCbpA1mda1aTbsblJLydW7T-lrOJtYPVi8DMs__8z_ze5BN88QQYFY8_Wo5mbLzxS2byMhwQGdyxo19dPbZPaU3pMkuIHI872xF4STICBBJ2KJC7HBuDEvC7nGCiKjGnTg1Qia_fx4Z83lqT-hOotd-56GVK_OXbtP2_17Bo1GNS8YRGN3HDoMOG2TShiBmsRHdmBgkDmDu-IbpBY7Ss-AXBpLzA0Wicix0mjdlJQ3SmGV7y5kMczPb9LeznMjWisteamRBNHzeBbnJ-xuK1arHYPODhQtKiOIpWek3eV3Un-J6_wTZujHHmT16zyeHnhT_2_8SVzXhJANDIfzrnk9XEjIlsfOZl0H3RcAAAD__w.lTmLTTsmyvMTRqNWoeJOwyc3UC58LmumASkSwrWcT2w");
        return lifeTimeAuthenticationEntity;
    }

    AudienceDTO getAudienceDTOTestData(){
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(TestDataGenerator.getObject(Advertiser.class));
        audienceDTO.setId(2146L);
        audienceDTO.setName("Test Audience");
        audienceDTO.setDataSourceType(2);
        audienceDTO.setUserDataType(2);
        audienceDTO.setLicensee(TestDataGenerator.getObject(BaseModel.class));
        audienceDTO.setActive(false);
        audienceDTO.setPixelId(414L);
        audienceDTO.setPixelDataSchedule(TestDataGenerator.getObject(PixelDataScheduleDTO.class));

        return audienceDTO;
    }


    @Test
    public void testSyncAppsFlyerAudience() throws Exception {
        AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto = new AppsFlyerAudienceSyncDto();
        appsFlyerAudienceSyncDto.setApi_key("eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNp8kMFKw0AQhl9F5hxK0pbE5GTUCoWoEAuCbpA1mda1aTbsblJLydW7T-lrOJtYPVi8DMs__8z_ze5BN88QQYFY8_Wo5mbLzxS2byMhwQGdyxo19dPbZPaU3pMkuIHI872xF4STICBBJ2KJC7HBuDEvC7nGCiKjGnTg1Qia_fx4Z83lqT-hOotd-56GVK_OXbtP2_17Bo1GNS8YRGN3HDoMOG2TShiBmsRHdmBgkDmDu-IbpBY7Ss-AXBpLzA0Wicix0mjdlJQ3SmGV7y5kMczPb9LeznMjWisteamRBNHzeBbnJ-xuK1arHYPODhQtKiOIpWek3eV3Un-J6_wTZujHHmT16zyeHnhT_2_8SVzXhJANDIfzrnk9XEjIlsfOZl0H3RcAAAD__w.lTmLTTsmyvMTRqNWoeJOwyc3UC58LmumASkSwrWcT2w");
        appsFlyerAudienceSyncDto.setContainer_id(2146L);
        appsFlyerAudienceSyncDto.setUrl("https://audiencespull.appsflyer.com/{{PARTNER_PULL_KEY}}/78czv5dq1zgxz4nh.csv");
        appsFlyerAudienceSyncDto.setUrl_hashed("");

        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = getLifeTimeAuthTokenTestData(true);
        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(apiAuthorizationFilter.getAuthentication(Mockito.any(UserInfo.class))).thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        AudienceDTO audienceDTO = getAudienceDTOTestData();
        audienceDTO.getAdvertiser().setId(7146L);
        ApiResponseObject apiResponseObject = new ApiResponseObject();
        apiResponseObject.setRespObject(audienceDTO);
        Mockito.when(audienceService.getAudience(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(apiResponseObject);
        Mockito.when(crmService.getSyncCoordinatorByPixelId(Mockito.anyLong())).thenReturn(null);
        Mockito.doNothing().when(audienceService).createCRMAudienceDetails(Mockito.any(AudienceDTO.class), Mockito.anyBoolean());
        Segments segment = TestDataGenerator.getObject(Segments.class);
        Mockito.when(properties.getPartnerPullKey_Key()).thenReturn("{{PARTNER_PULL_KEY}}");
        Mockito.when(properties.getPartnerPullKey_Value()).thenReturn("config");
        Mockito.when(segmentsRepository.findByIdAndLicenseeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(segment);
        Mockito.when(segmentsRepository.save(segment)).thenReturn(segment);
        appsFlyerAudienceService.syncAppsFlyerAudience(appsFlyerAudienceSyncDto);
        Mockito.verify(audienceService, times(1)).createCRMAudienceDetails(audienceDTO, false);
        Mockito.verify(segmentsRepository, times(1)).save(segment);
    }

    @Test
    public void testSyncAppsFlyerAudienceValidationFailed() throws Exception {
        AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto = new AppsFlyerAudienceSyncDto();
        appsFlyerAudienceSyncDto.setApi_key("sdjkfuiwesfiujkb982498k.jqbefliukjabefkwe98wxssfr");
        appsFlyerAudienceSyncDto.setContainer_id(2146L);
        appsFlyerAudienceSyncDto.setUrl("https://audiencespull.appsflyer.com/{{PARTNER_PULL_KEY}}/78czv5dq1zgxz4nh.csv");
        appsFlyerAudienceSyncDto.setUrl_hashed("");

        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = getLifeTimeAuthTokenTestData(true);
        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(apiAuthorizationFilter.getAuthentication(Mockito.any(UserInfo.class))).thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        AudienceDTO audienceDTO = getAudienceDTOTestData();
        audienceDTO.getAdvertiser().setId(7146L);
        ApiResponseObject apiResponseObject = new ApiResponseObject();
        apiResponseObject.setRespObject(audienceDTO);
        Mockito.when(audienceService.getAudience(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(apiResponseObject);
        Mockito.when(crmService.getSyncCoordinatorByPixelId(Mockito.anyLong())).thenReturn(null);
        Mockito.doNothing().when(audienceService).createCRMAudienceDetails(Mockito.any(AudienceDTO.class), Mockito.anyBoolean());
        Segments segment = TestDataGenerator.getObject(Segments.class);
        Mockito.when(properties.getPartnerPullKey_Key()).thenReturn("{{PARTNER_PULL_KEY}}");
        Mockito.when(properties.getPartnerPullKey_Value()).thenReturn("config");
        Mockito.when(segmentsRepository.findByIdAndLicenseeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(segment);
        Mockito.when(segmentsRepository.save(segment)).thenReturn(segment);
        exceptionRule.expect(ValidationException.class);
        appsFlyerAudienceService.syncAppsFlyerAudience(appsFlyerAudienceSyncDto);
    }

    @Test
    public void testSyncAppsFlyerAudienceSyncCoordinateNotNull() throws Exception {
        AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto = new AppsFlyerAudienceSyncDto();
        appsFlyerAudienceSyncDto.setApi_key("eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNp8kMFKw0AQhl9F5hxK0pbE5GTUCoWoEAuCbpA1mda1aTbsblJLydW7T-lrOJtYPVi8DMs__8z_ze5BN88QQYFY8_Wo5mbLzxS2byMhwQGdyxo19dPbZPaU3pMkuIHI872xF4STICBBJ2KJC7HBuDEvC7nGCiKjGnTg1Qia_fx4Z83lqT-hOotd-56GVK_OXbtP2_17Bo1GNS8YRGN3HDoMOG2TShiBmsRHdmBgkDmDu-IbpBY7Ss-AXBpLzA0Wicix0mjdlJQ3SmGV7y5kMczPb9LeznMjWisteamRBNHzeBbnJ-xuK1arHYPODhQtKiOIpWek3eV3Un-J6_wTZujHHmT16zyeHnhT_2_8SVzXhJANDIfzrnk9XEjIlsfOZl0H3RcAAAD__w.lTmLTTsmyvMTRqNWoeJOwyc3UC58LmumASkSwrWcT2w");
        appsFlyerAudienceSyncDto.setContainer_id(2146L);
        appsFlyerAudienceSyncDto.setUrl("https://audiencespull.appsflyer.com/{{PARTNER_PULL_KEY}}/78czv5dq1zgxz4nh.csv");
        appsFlyerAudienceSyncDto.setUrl_hashed("");

        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = getLifeTimeAuthTokenTestData(true);
        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(apiAuthorizationFilter.getAuthentication(Mockito.any(UserInfo.class))).thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        AudienceDTO audienceDTO = getAudienceDTOTestData();
        audienceDTO.getAdvertiser().setId(7146L);
        ApiResponseObject apiResponseObject = new ApiResponseObject();
        apiResponseObject.setRespObject(audienceDTO);
        Mockito.when(audienceService.getAudience(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(apiResponseObject);
        Mockito.when(crmService.getSyncCoordinatorByPixelId(Mockito.anyLong())).thenReturn(TestDataGenerator.getObject(ServerSyncCoordinatorDTO.class));
        Mockito.doNothing().when(audienceService).createCRMAudienceDetails(Mockito.any(AudienceDTO.class), Mockito.anyBoolean());
        Segments segment = TestDataGenerator.getObject(Segments.class);
        Mockito.when(properties.getPartnerPullKey_Key()).thenReturn("{{PARTNER_PULL_KEY}}");
        Mockito.when(properties.getPartnerPullKey_Value()).thenReturn("config");
        Mockito.when(segmentsRepository.findByIdAndLicenseeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(segment);
        Mockito.when(segmentsRepository.save(segment)).thenReturn(segment);
        appsFlyerAudienceService.syncAppsFlyerAudience(appsFlyerAudienceSyncDto);
        Mockito.verify(audienceService, times(1)).createCRMAudienceDetails(audienceDTO, true);
        Mockito.verify(crmService,times(1)).forceSyncAction(Mockito.anyLong());
    }
}