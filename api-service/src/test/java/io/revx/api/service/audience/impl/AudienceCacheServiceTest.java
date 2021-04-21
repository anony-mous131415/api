package io.revx.api.service.audience.impl;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
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
public class AudienceCacheServiceTest extends BaseTestService {
    @Mock
    EntityESService entityESService;

    @Mock
    CacheService cacheService;

    @Mock
    LoginUserDetailsService loginUserDetailsService;

    @Mock
    ApplicationProperties applicationProperties;

    @Mock
    ModelConverterService modelConverterService;

    @Mock
    SegmentsRepository segmentsRepository;

    @InjectMocks
    AudienceCacheService audienceCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        audienceCacheService.cacheService = cacheService;
        audienceCacheService.entityESService = entityESService;
        audienceCacheService.applicationProperties = applicationProperties;
        audienceCacheService.loginUserDetailsService = loginUserDetailsService;
        audienceCacheService.modelConverterService = modelConverterService;
        audienceCacheService.segmentsRepository = segmentsRepository;
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceCacheService#fetchAudience(java.lang.Long, java.lang.Boolean)}.
     */
    @Test
    public void testFetchAudience() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegments());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(segmentsRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.createSegments());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(list);
        Segments response = audienceCacheService.fetchAudience(3425L,true);
        assertNotNull(response);
        assertEquals("Honda",response.getName());
        Segments resp = audienceCacheService.fetchAudience(3425L,false);
        assertNotNull(resp);
        assertEquals("Honda",resp.getName());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceCacheService#fetchAllAudience(Long, int, int, String, SearchRequest, Boolean)}.
     */
    @Test
    public void testFetchAllAudienceNull() throws Exception{
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        SearchRequest request = new SearchRequest();
        request.setFilters(list);
        exceptionRule.expect(Exception.class);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(3452L);
        Mockito.when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(MockDataGenerator.getElasticSearchTerm());
        audienceCacheService.fetchAllAudience(33L,0,0,"2342",request,true);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.AudienceCacheService#getCacheKey()}.
     */
    @Test
    public void testGetCacheKey() throws Exception{
        List<Segments> list = new ArrayList<>();
        list.add(MockDataGenerator.createSegments());
        Mockito.when(segmentsRepository.findByLicenseeId(Mockito.anyLong())).thenReturn(list);
        audienceCacheService.saveToCache();
        audienceCacheService.saveToCache(list);
        audienceCacheService.remove();
        String response = audienceCacheService.getCacheKey();
        assertNotNull(response);
    }
}
