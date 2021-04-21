package io.revx.api.service.catalog;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoRepository;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.campaign.CatalogCacheService;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
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
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class CatalogServiceTest extends BaseTestService {
    @Mock
    private CatalogCacheService catalogCache;

    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private CatalogUtil catalogUtil;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private FeedInfoRepository feedInfoRepo;

    @Mock
    private ValidationService validator;

    @InjectMocks
    private CatalogService catalogService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        catalogService.feedInfoRepo = feedInfoRepo;
        catalogService.catalogUtil = catalogUtil;
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogService#getMacros(Long, Integer, SearchRequest, Integer, String, boolean)}.
     */
    @Test
    public void testGetMacros() throws Exception {
        ApiListResponse<Macro> response = catalogService.getMacros(3832L,33,null,3,"1",true);
        assertEquals(0,response.getTotalNoOfRecords());
    }

    @Test
    public void testGetMacrosIfAdvertiserIdNull() throws Exception {
        BaseEntity baseEntity = new Macro();
        List<BaseEntity> list = new ArrayList<>();
        list.add(baseEntity);
        Mockito.when(catalogCache.fetchMacros(Mockito.anyLong(),Mockito.any(),Mockito.anyString(),Mockito.anyBoolean()))
                .thenReturn(list);
        ApiListResponse<Macro> response = catalogService.getMacros(0L,33,null,3,"1",true);
        assertEquals(1,response.getTotalNoOfRecords());
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogService#getFeeds(Long, Integer, SearchRequest, Integer, String, boolean)}.
     */
    @Test
    public void testGetFeeds() throws Exception {
        ApiListResponse<CatalogFeed> response = catalogService.getFeeds(3832L,33,null,3,"1",true);
        assertEquals(0,response.getTotalNoOfRecords());

    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogService#getVariableMappings(Long, Integer, Integer, SearchRequest, String, boolean)}.
     */
    @Test
    public void testGetVariableMappings() throws Exception {
        Optional<FeedInfoEntity> optional = Optional
                .of(MockDataGenerator.createFeedInfoEntity());
        Mockito.when(feedInfoRepo.findById(Mockito.anyLong())).thenReturn(optional);
        ApiListResponse<VariablesMappingDTO> response = catalogService.getVariableMappings(3832L,33,3,null,"1",true);
        assertEquals(0,response.getTotalNoOfRecords());
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogService#getAllFeedsByAdvertiserId(Long)}.
     */
    @Test
    public void testGetAllFeedsByAdvertiserId() throws Exception {
        List<FeedInfoEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createFeedInfoEntity());
        Mockito.when(feedInfoRepo.findAllByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        List<CatalogFeed> response = catalogService.getAllFeedsByAdvertiserId(3832L);
        assertEquals(1,response.size());
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogService#getbyId(Long)}.
     */
    @Test
    public void testGetById() throws Exception{
        Optional<FeedInfoEntity> optional = Optional
                .of(MockDataGenerator.createFeedInfoEntity());
        Mockito.when(feedInfoRepo.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(modelConverter.convertFeedEntityToFeedDTO(Mockito.any()))
                .thenReturn(MockDataGenerator.createCatalogFeed());
        Mockito.when(catalogUtil.getSuccessRateForFeedInfoStats(Mockito.anyLong(),Mockito.anyLong())).
                thenReturn(33L);
        ApiResponseObject<CatalogFeed> response = catalogService.getbyId(3832L);
        assertEquals("Honda",response.getRespObject().getName());
    }
}
