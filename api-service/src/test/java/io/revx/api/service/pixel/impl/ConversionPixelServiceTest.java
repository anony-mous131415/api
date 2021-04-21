package io.revx.api.service.pixel.impl;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.pixel.ConversionPixelRepository;
import io.revx.api.service.EsDataProvider;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.campaign.PixelCacheService;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.SlicexData;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.pixel.Tag;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ConversionPixelServiceTest extends BaseTestService {
    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private CustomESRepositoryImpl elastic;

    @Mock
    private ValidationService validator;

    @Mock
    private ConversionPixelRepository pixelRepo;

    @Mock
    private EsDataProvider esdata;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private  PixelCacheService pixelCache;

    @Mock
    private AdvertiserLineItemPixelRepository alipRepo;

    @Mock
    private ApplicationProperties properties;

    @InjectMocks
    private ConversionPixelService pixelService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pixelService.modelConverter = modelConverter;
        pixelService.properties = properties;
        pixelService.alipRepo = alipRepo;
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#create (io.revx.core.model.pixel)}.
     */
    @Test
    public void testCreate() throws Exception {
        Mockito.when(modelConverter.convertPixelDTOToEntity(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any()))
                .thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        ApiResponseObject<Pixel> response = pixelService.create(MockDataGenerator.createPixel());
        assertNotNull(response);
        assertEquals("TestPixel", response.getRespObject().getName());
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#update (io.revx.core.model.pixel)}.
     */
    @Test
    public void testUpdate() throws Exception {
        Optional<ConversionPixelEntity> optional = Optional.of(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(pixelRepo.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(modelConverter.convertPixelDTOToEntity(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any())).thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        ApiResponseObject<Pixel> response = pixelService.update(MockDataGenerator.createPixel());
        assertNotNull(response);
        assertEquals("TestPixel", response.getRespObject().getName());
    }

    @Test
    public void testUpdateWithPixelNull() throws Exception {
        Mockito.when(modelConverter.convertPixelDTOToEntity(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any())).thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        exceptionRule.expect(ApiException.class);
        pixelService.update(MockDataGenerator.createPixel());
        }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#getbyId(java.lang.Long)}.
     */
    @Test
    public void testGetById() throws Exception {
        Optional<ConversionPixelEntity> optional = Optional
                .of(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(pixelRepo.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any())).thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        ApiResponseObject<Pixel> response = pixelService.getbyId(33L);
        assertNotNull(response);
        assertEquals("TestPixel", response.getRespObject().getName());
    }

    @Test
    public void testGetByIdWithPixelNull() throws Exception {
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any())).thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        exceptionRule.expect(ApiException.class);
        pixelService.getbyId(33L);
    }

    @Test
    public void testGetByIdNull() throws Exception {
        Optional<ConversionPixelEntity> optional = Optional
                .of(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(pixelRepo.findById(Mockito.any())).thenReturn(optional);
        Mockito.when(modelConverter.convertPixelToDTO(Mockito.any()))
                .thenReturn(MockDataGenerator.createPixel());
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        exceptionRule.expect(ApiException.class);
        ApiResponseObject<Pixel> response = pixelService.getbyId(null);
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#getTrackingCode(java.lang.Long)}.
     */
    @Test
    public void testGetTrackingCode() throws Exception {
        String url = "trk.atomex.test/cgi-bin/tracker.fcgi";
        String url1 = "<!-- RevX Remarketing Conversion Pixel -->\n\n<!-- Pass values through the query params in url below\n tid = ID OF THE TRANSACTION\n tamt = AMOUNT OF THE TRANSACTION\n -->\n<img src=\"//{0}?px={1}&ty=1&tid=&tamt=\" />";
        String url2 = "<!-- RevX Remarketing Conversion Pixel -->\n\n<!-- Pass values through the query params in url below\n tid = ID OF THE TRANSACTION\n tamt = AMOUNT OF THE TRANSACTION\n -->\n<script type=\"text/javascript\" src=\"//{0}?px={1}&ty=1&tid=&tamt=\"></script>";
        String url3 = "http://{0}?px={1}&android_id=&os=&advertising_id=&ty=1&tid=&tamt=";
        Mockito.when(properties.getTrackerAppurl()).thenReturn(url);
        Mockito.when(properties.getImageTrackerCodeTemplate()).thenReturn(url1);
        Mockito.when(properties.getJsTrackerCodeTemplate()).thenReturn(url2);
        Mockito.when(properties.getAppTrackerCodeTemplate()).thenReturn(url3);
        ApiResponseObject<Tag> response = pixelService.getTrackingCode(33L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#searchPixels(SearchRequest, Integer, Integer, String, boolean, Long)}
     */
    @Test
    public void testSearchPixels() throws Exception {
        SearchRequest request = new SearchRequest();
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        request.setFilters(list);
        AdvertiserLineItemPixelEntity advertiserLineItemPixelEntity = new AdvertiserLineItemPixelEntity();
        advertiserLineItemPixelEntity.setPixelId(33L);
        advertiserLineItemPixelEntity.setStrategyId(333L);
        List<AdvertiserLineItemPixelEntity> alip = new ArrayList<>();
        alip.add(advertiserLineItemPixelEntity);
        SearchRequest search = new SearchRequest();
        search.setFilters(list);
        List<BaseEntity> pixels = new ArrayList<>();
        pixels.add(MockDataGenerator.createPixel());
        Mockito.when(pixelCache.fetchPixelAsync(Mockito.any(),Mockito.anyInt(),Mockito.any(),Mockito.anyString()
                ,Mockito.anyBoolean(),Mockito.anyLong())).thenReturn(pixels);
        Mockito.when(alipRepo.findAllByStrategyId(Mockito.any())).thenReturn(alip);
        ApiListResponse<Pixel> response = pixelService.searchPixels(request,33,33,"1",true,
                3832L);
        assertNotNull(response);
    }

    @Test
    public void testSearchPixelsNull() throws Exception {
        AdvertiserLineItemPixelEntity advertiserLineItemPixelEntity = new AdvertiserLineItemPixelEntity();
        advertiserLineItemPixelEntity.setPixelId(33L);
        advertiserLineItemPixelEntity.setStrategyId(333L);
        List<AdvertiserLineItemPixelEntity> alip = new ArrayList<>();
        alip.add(advertiserLineItemPixelEntity);
        DashboardFilters dash = new DashboardFilters();
        dash.setValue("123");
        dash.setColumn("1");
        List<DashboardFilters> list = new ArrayList<>();
        list.add(dash);
        SearchRequest search = new SearchRequest();
        search.setFilters(list);
        Mockito.when(alipRepo.findAllByStrategyId(Mockito.any())).thenReturn(alip);
        ApiListResponse<Pixel> response = pixelService.searchPixels(null,33,33,"1",true,
                3832L);
        assertNotNull(response);
    }

    @Test
    public void testSearchPixelsIfFilterNotPresent() throws Exception {
        AdvertiserLineItemPixelEntity advertiserLineItemPixelEntity = new AdvertiserLineItemPixelEntity();
        advertiserLineItemPixelEntity.setPixelId(33L);
        advertiserLineItemPixelEntity.setStrategyId(333L);
        List<AdvertiserLineItemPixelEntity> alip = new ArrayList<>();
        alip.add(advertiserLineItemPixelEntity);
        List<DashboardFilters> list = new ArrayList<>();
        SearchRequest search = new SearchRequest();
        search.setFilters(null);
        Mockito.when(alipRepo.findAllByStrategyId(Mockito.any())).thenReturn(alip);
        ApiListResponse<Pixel> response = pixelService.searchPixels(search,33,33,"1",true,
                3832L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#activate(java.lang.String)}
     */
    @Test
    public void testActivate() throws Exception {
        List<ConversionPixelEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(pixelRepo.findByIdIn(Mockito.any())).thenReturn(list);
        ApiResponseObject<Map<Long, ResponseMessage>> response = pixelService.activate("3832");
        assertNotNull(response);
        assertEquals(true,response.getRespObject().containsKey(8771L));
    }

    @Test
    public void testInactivateId() throws Exception {
        ConversionPixelEntity conversionPixelEntity = new ConversionPixelEntity();
        conversionPixelEntity.setName("Honda");
        List<ConversionPixelEntity> list = new ArrayList<>();
        list.add(conversionPixelEntity);
        Mockito.when(pixelRepo.findByIdIn(Mockito.any())).thenReturn(list);
        ApiResponseObject<Map<Long, ResponseMessage>> response = pixelService.deactivate("3832");
        assertNotNull(response);
        assertEquals(false,response.getRespObject().containsKey(132L));
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#deactivate(java.lang.String)}
     */
    @Test
    public void testDeactivate() throws Exception {
        List<ConversionPixelEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createConversionPixelEntity());
        Mockito.when(pixelRepo.findByIdIn(Mockito.any())).thenReturn(list);
        ApiResponseObject<Map<Long, ResponseMessage>> response = pixelService.deactivate("3832");
        assertNotNull(response);
        assertEquals(true,response.getRespObject().containsKey(8771L));
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.ConversionPixelService#getESHourlyDataForPixel(Long)}
     */
    @Test
    public void testGetESHourlyDataForPixel() throws Exception{
        List<? extends SlicexData> response = pixelService.getESHourlyDataForPixel(3832L);
        assertNotNull(response);
        assertEquals(0,response.size());
    }
}