package io.revx.api.service.campaign;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.pixel.ConversionPixelRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class PixelCacheServiceTest extends BaseTestService {
    @Mock
    private CacheService cacheService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private ConversionPixelService service;

    @Mock
    private ConversionPixelRepository pixelRepository;

    @InjectMocks
    private PixelCacheService pixelCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pixelCacheService.loginUserDetailsService = loginUserDetailsService;
        pixelCacheService.pixelRepository = pixelRepository;
        pixelCacheService.modelConverter = modelConverter;
        pixelCacheService.cacheService = cacheService;
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.PixelCacheService#fetchPixel(Long, Boolean)}
     */
    @Test
    public void testFetchPixel() throws Exception{
        List<Long> list = new ArrayList<>();
        list.add(4779L);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(loginUserDetailsService.getAdvertisers()).thenReturn(list);
        Mockito.when(pixelRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(Mockito.anyLong(),Mockito.anyLong()
        ,Mockito.anyList())).thenReturn(MockDataGenerator.createConversionPixelEntity());
        ConversionPixelEntity response = pixelCacheService.fetchPixel(7654L,false);
        assertNotNull(response);
        assertEquals("TestPixel",response.getName());
    }

    @Test
    public void testFetchPixelGetAdvertisers() throws Exception{
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(4511L);
        Mockito.when(pixelRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
        .thenReturn(MockDataGenerator.createConversionPixelEntity());
        ConversionPixelEntity response = pixelCacheService.fetchPixel(7654L,false);
        assertNotNull(response);
        assertEquals("TestPixel",response.getName());
    }
}
