package io.revx.api.service.campaign;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.Licensee;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CampaignCacheServiceTest extends BaseTestService {
    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ModelConverterService modelConverterService;

    @Mock
    private EntityESService elasticService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
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
        campaignCacheService.loginUserDetailsService = loginUserDetailsService;
        campaignCacheService.cacheService = cacheService;
        campaignCacheService.campaignRepository = campaignRepository;
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignCacheService#findByIdAndLicenseeId(Long, Boolean)}
     */
    @Test
    public void testFindByIdAndLicenseeId() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCampaignEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(list);
        Mockito.when(campaignRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        CampaignEntity response = campaignCacheService.findByIdAndLicenseeId(2732L,false);
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getName());
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignCacheService#fetchCampaign(Long, Boolean)}
     */
    @Test
    public void testFetchCampaign() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCampaignEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(list);
        Mockito.when(campaignRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        CampaignEntity response = campaignCacheService.fetchCampaign(2732L,false);
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getName());
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CampaignCacheService#findByIdAndLicenseeIdAndAdvertiserIdIn(Long, Boolean)}
     */
    @Test
    public void testFindByIdAndLicenseeIdAndAdvertiserIdIn() throws Exception{
        mockSecurityContext("akhilesh", false, false);
        List<Long> list = new ArrayList<>();
        List<BaseEntity> campaignEntityList = new ArrayList<>();
        List<CampaignEntity> campaignEntities = new ArrayList<>();
        campaignEntities.add(MockDataGenerator.createCampaignEntity());
        campaignEntityList.add(MockDataGenerator.createCampaignEntity());
        list.add(2354L);
        campaignCacheService.saveToCache();
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(loginUserDetailsService.getAdvertisers()).thenReturn(list);
        Mockito.when(campaignRepository.findByLicenseeId(Mockito.anyLong())).thenReturn(campaignEntities);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(campaignEntityList);
        Mockito.when(campaignRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(Mockito.anyLong(),Mockito.anyLong()
        ,Mockito.anyList())).thenReturn(MockDataGenerator.createCampaignEntity());
        CampaignEntity response = campaignCacheService.findByIdAndLicenseeIdAndAdvertiserIdIn(3452L,true);
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getName());
    }

    @Test
    public void testFindByIdAndLicenseeIdAndAdvertiserId() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCampaignEntity());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(list);
        Mockito.when(campaignRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createCampaignEntity());
        CampaignEntity response = campaignCacheService.findByIdAndLicenseeIdAndAdvertiserIdIn(2732L,false);
        assertNotNull(response);
        assertEquals("SafariTestIO",response.getName());
    }
}
