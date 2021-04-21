package io.revx.api.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.QueryType;
import io.revx.api.enums.SlicexEntity;
import io.revx.api.service.campaign.CampaignUtils;
import io.revx.core.model.Licensee;
import io.revx.core.model.SlicexData;
import io.revx.core.model.SlicexGridData;
import io.revx.core.model.requests.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SlicexServiceTest extends BaseTestService {
    @Mock
    private EsDataProvider eSDataProvider;

    @Mock
    private LoginUserDetailsService loginService;

    @Mock
    private EsRestClient esRestClient;

    @Mock
    private CSVReaderWriterService csvReaderWriterService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private Environment env;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SlicexService slicexService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eSDataProvider = new EsDataProvider();
        esRestClient = new EsRestClient();
        slicexService.eSDataProvider = eSDataProvider;
        eSDataProvider.loginService = loginService;
        eSDataProvider.esRestClient = esRestClient;
        esRestClient.env = env;
        esRestClient.restTemplate = restTemplate;
        slicexService.loginService = loginService;
    }

    /**
     *  Test method for {@Link io.revx.api.service.SlicexService#getSlicexChartData(SlicexRequest)}
     */
    @Test
    public void testGetSlicexChartData() throws Exception{
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        SlicexRequest slicexRequest = new SlicexRequest();
        slicexRequest.setCompareToDuration(MockDataGenerator.getDashboardRequest().getDuration());
        SlicexChartResponse response = slicexService.getSlicexChartData(slicexRequest);
        assertNotNull(response);
        assertEquals(45612,response.getCompareEndTimestamp().longValue());
    }

    /**
     *  Test method for {@Link io.revx.api.service.SlicexService#getSlicexGridData(SlicexRequest, String, SlicexEntity)}
     */
    @Test
    public void testGetSlicexGridData() throws Exception{
        Duration duration = new Duration();
        duration.setEndTimeStamp(1614124800L);
        duration.setStartTimeStamp(1614038400L);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        SlicexRequest request = new SlicexRequest();
        request.setDuration(duration);
        request.setGroupBy("daily");
        request.setCompareToDuration(null);
        SlicexListResponse response = slicexService.getSlicexGridData(request,null,SlicexEntity.licensee);
        assertNotNull(response);
    }

    @Test
    public void testGetSlicexGridDataSortData() throws Exception{
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        SlicexRequest request = new SlicexRequest();
        request.setFilters(list);
        SlicexListResponse response = slicexService.getSlicexGridData(request,null,SlicexEntity.aggregator);
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.SlicexService#getSlicexGridDataForExport(SlicexRequest, String, SlicexEntity)}
     */
    @Test
    public void testGetSlicexGridDataForExport() throws Exception{
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginService.isSuperAdminUser()).thenReturn(true);
        Mockito.when(loginService.getAdvertiserCurrencyId()).thenReturn("INR");
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        SlicexRequest request = new SlicexRequest();
        request.setFilters(list);
        request.setDuration(MockDataGenerator.getDashboardRequest().getDuration());
        FileDownloadResponse response = slicexService.getSlicexGridDataForExport(request,"cost",SlicexEntity.licensee);
        assertNotNull(response);
    }

    @Test
    public void testGetSlicexGridDataForExports() throws Exception{
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilter());
        Set<Long> set = new HashSet<>();
        set.add(76L);
        Map<Long,Set<Long>> map = new HashMap<>();
        map.put(76L,set);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginService.isSuperAdminUser()).thenReturn(true);
        Mockito.when(loginService.getAdvertiserCurrencyId()).thenReturn("INR");
        Mockito.when(loginService.getAllLicenseeCount()).thenReturn(2);
        Mockito.when(loginService.getAllAdvLicenseeMap()).thenReturn(map);
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        SlicexRequest request = new SlicexRequest();
        request.setFilters(list);
        request.setDuration(MockDataGenerator.getDashboardRequest().getDuration());
        FileDownloadResponse response = slicexService.getSlicexGridDataForExport(request,"cost",SlicexEntity.licensee);
        assertNotNull(response);
    }

    @Test
    public void testGetSlicexGridDataForExportStrategy() throws Exception{
        List<DashboardFilters> list = new ArrayList<>();
        DashboardFilters filters = new DashboardFilters();
        filters.setOperator("AND");
        filters.setValue("76");
        filters.setColumn("strategy");
        list.add(filters);
        Set<Long> set = new HashSet<>();
        set.add(76L);
        Map<Long,Set<Long>> map = new HashMap<>();
        map.put(76L,set);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(loginService.isSuperAdminUser()).thenReturn(true);
        Mockito.when(loginService.getAdvertiserCurrencyId()).thenReturn("INR");
        Mockito.when(loginService.getAllLicenseeCount()).thenReturn(2);
        Mockito.when(loginService.getAllAdvLicenseeMap()).thenReturn(map);
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        SlicexRequest request = new SlicexRequest();
        request.setFilters(list);
        request.setDuration(MockDataGenerator.getDashboardRequest().getDuration());
        FileDownloadResponse response = slicexService.getSlicexGridDataForExport(request,"cost",SlicexEntity.licensee);
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.SlicexService#getExportCSVFileName(SlicexEntity, Duration)}
     */
    @Test
    public void testGetExportCSVFileName() throws Exception{
        Duration duration = new Duration();
        duration.setEndTimeStamp(34L);
        duration.setStartTimeStamp(4L);
        String response = slicexService.getExportCSVFileName(SlicexEntity.licensee,duration);
        assertNotNull(response);
    }
}
