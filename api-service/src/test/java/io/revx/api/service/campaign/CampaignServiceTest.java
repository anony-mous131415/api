package io.revx.api.service.campaign;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.repo.campaign.AdvertiserIOPixelRepository;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ValidationService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.UserInfo;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CampaignServiceTest extends BaseTestService {
    @Mock
    private ValidationService validationService;

    @Mock
    private CampaignUtils campaignUtils;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private AdvertiserIOPixelRepository ioPixelRepository;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private CustomESRepositoryImpl elastic;

    @Mock
    private CampaignCacheService campaignCacheService;

    @InjectMocks
    private CampaignService campaignService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        campaignUtils = new CampaignUtils();
        campaignService.campaignUtils = campaignUtils;
        campaignUtils.loginUserDetailsService = loginUserDetailsService;
        campaignUtils.elasticSearch = elasticSearch;
        campaignService.campaignRepository = campaignRepository;
        campaignService.ioPixelRepository = ioPixelRepository;
        campaignService.campaignCacheService = campaignCacheService;
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#create(CampaignDTO)}
     */
    @Test
    public void testCreate() throws Exception {
        CampaignEntity campaignEntity = MockDataGenerator.createCampaignEntity();
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchById(TablesEntity.CURRENCY, campaignEntity.getCurrencyId()))
                .thenReturn(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchById(TablesEntity.COUNTRY, campaignEntity.getRegionId()))
                .thenReturn(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchById(TablesEntity.LICENSEE, campaignEntity.getLicenseeId()))
                .thenReturn(MockDataGenerator.createBaseModel());
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.getCurrencyModel());
        ApiResponseObject<CampaignDTO> response = campaignService
                .create(MockDataGenerator.createCampaignDTO());
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getRespObject().getName());
    }

    @Test
    public void testCreateWithId() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.getCurrencyModel());
        ApiResponseObject<CampaignDTO> response = campaignService
                .create(MockDataGenerator.createCampaignId());
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getRespObject().getName());
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#update(CampaignDTO)}
     */
    @Test
    public void testUpdate() throws Exception {
        exceptionRule.expect(Exception.class);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Optional<CampaignEntity> optional = Optional.of(MockDataGenerator.createCampaignEntity());
        Mockito.when(campaignRepository.findById(Mockito.any())).thenReturn(optional);
        campaignService.update(MockDataGenerator.createCampaignDTO());
    }

    @Test
    public void testUpdateWithId() throws Exception {
        exceptionRule.expect(Exception.class);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Optional<CampaignEntity> optional = Optional.of(MockDataGenerator.createCampaignEntity());
        Mockito.when(campaignRepository.findById(Mockito.any())).thenReturn(optional);
        campaignService.update(MockDataGenerator.createCampaignId());
    }

    @Test
    public void testUpdateByInvalidID() throws Exception {
        exceptionRule.expect(ApiException.class);
        campaignService.update(MockDataGenerator.createCampaignDTO());
        }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#getById(Long,Boolean)}
     */
    @Test
    public void testGetById() throws Exception {
        exceptionRule.expect(Exception.class);
        Optional<AdvertiserIOPixel> optional = Optional.of(MockDataGenerator.createAdvertiserIOPixel());
        Mockito.when(campaignCacheService.fetchCampaign(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        Mockito.when(ioPixelRepository.findById(Mockito.anyLong())).thenReturn(optional);
        campaignService.getbyId(6429L,true);
    }

    @Test
    public void testGetByIdNull() throws Exception {
        Optional<AdvertiserIOPixel> optional = Optional.of(MockDataGenerator.createAdvertiserIOPixel());
        Mockito.when(ioPixelRepository.findById(Mockito.anyLong())).thenReturn(optional);
        exceptionRule.expect(ApiException.class);
        campaignService.getbyId(8932L,true);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#activate(String)}
     */
    @Test
    public void testActivate() throws Exception {
        ApiResponseObject<Map<Integer, ResponseMessage>> response = campaignService.activate("234");
        assertNotNull(response);
        assertEquals(1,response.getRespObject().size());
    }

    @Test
    public void testActivateWithNullId() throws Exception {
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<Map<Integer, ResponseMessage>> response = campaignService.activate("");
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#deactivate(String)}
     */
    @Test
    public void testDeactivate() throws Exception {
        ApiResponseObject<Map<Integer, ResponseMessage>> response = campaignService.deactivate("234");
        assertNotNull(response);
        assertEquals(1,response.getRespObject().size());
    }

    @Test
    public void testDeactivateWithNullId() throws Exception {
        exceptionRule.expect(ApiException.class);
        campaignService.deactivate("");
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#getbyAdvertiserId(Long, Integer, Integer, String, String, Boolean)}
     */
    @Test
    public void testGetByAdvertiserId() throws Exception {
        List<CampaignEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCampaignEntity());
        exceptionRule.expect(Exception.class);
        Mockito.when(elasticSearch.searchAll(Mockito.any(),Mockito.any(ElasticSearchTerm.class))).thenReturn(null);
        Mockito.when(campaignCacheService.fetchAllCampaign(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),
                Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean())).thenReturn(list);
        Optional<AdvertiserIOPixel> optional = Optional.of(MockDataGenerator.createAdvertiserIOPixel());
        Mockito.when(ioPixelRepository.findById(Mockito.anyLong())).thenReturn(optional);
        campaignService.getbyAdvertiserId(6422L,2,3,"1","10",true);
    }

    @Test
    public void testGetByAdvertiserIdNull() throws Exception {
        List<CampaignEntity> list = new ArrayList<>();
        Mockito.when(campaignCacheService.fetchAllCampaign(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),
                Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean())).thenReturn(null);
        Optional<AdvertiserIOPixel> optional = Optional.of(MockDataGenerator.createAdvertiserIOPixel());
        Mockito.when(ioPixelRepository.findById(Mockito.anyLong())).thenReturn(optional);
        exceptionRule.expect(ApiException.class);
        campaignService.getbyAdvertiserId(6422L,2,3,"0","10",false);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignService#updateStatus(List<Long>,Boolean)}
     */
    @Test
    public void testUpdateStatus() throws Exception {
        List<Long> list = new ArrayList<>();
        list.add(235L);
        Mockito.when(campaignCacheService.fetchCampaign(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        Map<Integer, ResponseMessage> response = campaignService.updateStatus(list,true);
        assertEquals(1,response.size());
    }

    @Test
    public void testUpdateRowStatus() throws Exception {
        List<Long> list = new ArrayList<>();
        list.add(235L);
        list.add(0L);
        CampaignESDTO campaign = new CampaignESDTO();
        campaign.setAdvertiserId(6L);
        campaign.setId(235L);
        Mockito.when(campaignCacheService.fetchCampaign(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        Mockito.when(campaignRepository.updateStatus(Mockito.any(),Mockito.anyLong())).thenReturn(1);
        Mockito.when(elastic.findDetailById(Mockito.any(),Mockito.anyString(),Mockito.any()))
                .thenReturn(campaign);
        Map<Integer, ResponseMessage> response = campaignService.updateStatus(list,false);
        assertEquals(2,response.size());
    }

    @Test
    public void testUpdateStatusWithNull() throws Exception {
        List<Long> list = new ArrayList<>();
        list.add(235L);
        Mockito.when(campaignCacheService.fetchCampaign(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createCampaign());
        Map<Integer, ResponseMessage> response = campaignService.updateStatus(list,true);
        assertEquals(1,response.size());
    }
}
