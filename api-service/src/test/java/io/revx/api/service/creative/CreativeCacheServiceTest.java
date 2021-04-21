package io.revx.api.service.creative;

import io.revx.api.common.TestDataGenerator;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.service.*;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.service.CacheService;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class CreativeCacheServiceTest extends TestCase {

    @Mock
    CacheService cacheService;

    @Mock
    LoginUserDetailsService loginUserDetailsService;

    @Mock
    EntityESService elasticSearch;

    @Mock
    CreativeUtil modelConverter;

    @Mock
    CreativeRepository creativeRepository;

    @InjectMocks
    CreativeCacheService creativeCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        creativeCacheService.cacheService = cacheService;
        creativeCacheService.creativeRepository = creativeRepository;
        creativeCacheService.loginUserDetailsService = loginUserDetailsService;
        creativeCacheService.elasticSearch = elasticSearch;
        creativeCacheService.modelConverter = modelConverter;
    }

    @Test
    public void testFetchCompactCreatives() {
        List<BaseEntity> list = new ArrayList<>();
        CreativeEntity creativeEntity1 = new CreativeEntity();
        creativeEntity1.setId(101L);
        CreativeEntity creativeEntity2 = new CreativeEntity();
        creativeEntity2.setId(102L);
        BaseEntity baseEntity1 = creativeEntity1;
        BaseEntity baseEntity2 = creativeEntity2;
        list.add(baseEntity1);
        list.add(baseEntity2);

        CreativeCompactDTO creative1 = new CreativeCompactDTO();
        creative1.setId(101L);
        BaseModel baseModel = new BaseModel();

        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(list);
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(219L);
        Mockito.when(creativeRepository.findAllByLicenseeIdAndIsRefactored(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(TestDataGenerator.getListOfObject(10, CreativeEntity.class));
        Mockito.when(modelConverter.populateCreativeCompactDTO(creativeEntity1)).thenReturn(creative1);
        Mockito.when(elasticSearch.searchById(Mockito.any(), Mockito.anyLong())).thenReturn(baseModel);

        SearchRequest searchRequest = new SearchRequest();
        List<DashboardFilters> dashboardFiltersList = new ArrayList<>();
        DashboardFilters filter1 = new DashboardFilters("advertiserId", "7146");
        DashboardFilters filter2 = new DashboardFilters("status", "active");

        dashboardFiltersList.add(filter1);
        dashboardFiltersList.add(filter2);
        searchRequest.setFilters(dashboardFiltersList);
        List<CreativeCompactDTO> resp = creativeCacheService.fetchCompactCreatives(searchRequest,null, false, false);

        assertNotNull(resp);
        assertThat(resp.size()).isEqualTo(2);
        assertThat(resp.get(0).getId()).isEqualTo(101L);
    }
}