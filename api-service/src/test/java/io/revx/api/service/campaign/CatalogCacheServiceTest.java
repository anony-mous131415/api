package io.revx.api.service.campaign;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.catalog.CatalogService;
import io.revx.api.service.catalog.CatalogUtil;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.requests.SearchRequest;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CatalogCacheServiceTest extends BaseTestService {
    @Mock
    private CacheService cacheService;

    @Mock
    private CatalogService catalogService;

    @Mock
    private CatalogUtil catalogUtil;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private FeedInfoRepository fiRepo;

    @InjectMocks
    private CatalogCacheService catalogCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        catalogCacheService.cacheService = cacheService;
        catalogCacheService.catalogService = catalogService;
        catalogCacheService.catalogUtil = catalogUtil;
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CatalogCacheService#fetchFeedInfo(Long, SearchRequest, String, Boolean)}
     */
    @Test
    public void testFetchFeedInfo() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<BaseEntity> entityList = new ArrayList<>();
        entityList.add(MockDataGenerator.createCampaignEntity());
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(entityList);
        Mockito.when(catalogService.getAllFeedsByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        List<BaseEntity> response = catalogCacheService.fetchFeedInfo(2314L,searchRequest,"test",true);
        assertNotNull(response);
        assertEquals(1,response.size());
    }

    @Test
    public void testFetchFeedInfoNull() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        Mockito.when(catalogService.getAllFeedsByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        List<BaseEntity> response = catalogCacheService.fetchFeedInfo(2314L,searchRequest,"test",true);
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CatalogCacheService#fetchMacros(Long, SearchRequest, String, Boolean)}
     */
    @Test
    public void testFetchMacros() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<BaseEntity> entityList = new ArrayList<>();
        entityList.add(MockDataGenerator.createCampaignEntity());
        List<Macro> macros = new ArrayList<>();
        macros.add(MockDataGenerator.createMacro());
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(entityList);
        Mockito.when(catalogUtil.getMacroDTOListForAdvertiser(Mockito.anyLong())).thenReturn(macros);
        List<BaseEntity> response = catalogCacheService.fetchMacros(2314L,searchRequest,"test",true);
        assertNotNull(response);
        assertEquals(1,response.size());
    }

    @Test
    public void testFetchMacrosNull() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        List<Macro> macros = new ArrayList<>();
        macros.add(MockDataGenerator.createMacro());
        Mockito.when(catalogService.getAllFeedsByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(catalogUtil.getMacroDTOListForAdvertiser(Mockito.anyLong())).thenReturn(macros);
        List<BaseEntity> response = catalogCacheService.fetchMacros(2314L,searchRequest,"test",true);
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CatalogCacheService#fetchMacros(Long, SearchRequest, String, Boolean)}
     */
    @Test
    public void testFetchACVM() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<BaseEntity> entityList = new ArrayList<>();
        entityList.add(MockDataGenerator.createCampaignEntity());
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        List<VariablesMappingDTO> variablesList = new ArrayList<>();
        variablesList.add(MockDataGenerator.createVariablesMappingDTO());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(entityList);
        Mockito.when(catalogService.getAllFeedsByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(catalogUtil.getVariableMapping(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(variablesList);
        List<BaseEntity> response = catalogCacheService.fetchACVM(2314L,3459L,searchRequest,"test",true);
        assertNotNull(response);
        assertEquals(1,response.size());
    }

    @Test
    public void testFetchACVMNull() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(null);
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        List<VariablesMappingDTO> variablesList = new ArrayList<>();
        variablesList.add(MockDataGenerator.createVariablesMappingDTO());
        Mockito.when(catalogService.getAllFeedsByAdvertiserId(Mockito.anyLong())).thenReturn(list);
        Mockito.when(catalogUtil.getVariableMapping(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(variablesList);
        List<BaseEntity> response = catalogCacheService.fetchACVM(2314L,4512L,searchRequest,"test",true);
        assertNotNull(response);
    }
}
