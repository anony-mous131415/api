package io.revx.api.service;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.enums.*;
import io.revx.api.pojo.SlicexFilter;
import io.revx.core.model.SlicexData;
import io.revx.core.model.requests.Duration;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class EsDataProviderTest extends BaseTestService {
    @Mock
    private Environment env;

    @Mock
    private EsRestClient esRestClient;

    @Mock
    private LoginUserDetailsService loginService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EsDataProvider eSDataProvider;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        esRestClient = new EsRestClient();
        eSDataProvider.esRestClient = esRestClient;
        eSDataProvider.loginService = loginService;
        esRestClient.restTemplate = restTemplate;
        esRestClient.env = env;
    }

    /**
     *  Test method for {@Link io.revx.api.service.EsDataProvider#fetchGraphData(QueryType, Duration, Set<SlicexFilter>, SlicexInterval)}
     */
    @Test
    public void testFetchGraphData() throws Exception{
        Duration duration = new Duration();
        duration.setStartTimeStamp(2345L);
        duration.setEndTimeStamp(5432L);
        Set<SlicexFilter> set = new HashSet<>();
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        List<? extends SlicexData> response = eSDataProvider.fetchGraphData(QueryType.slicexChart,duration,set,
                SlicexInterval.DAILY);
        assertNotNull(response);
    }

    @Test
    public void testFetchGraphDataAdminUser() throws Exception {
        Duration duration = new Duration();
        duration.setStartTimeStamp(2345L);
        duration.setEndTimeStamp(5432L);
        Set<SlicexFilter> set = new HashSet<>();
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        Mockito.when(loginService.isAdminUser()).thenReturn(true);
        Mockito.when(loginService.getAllLicenseeCount()).thenReturn(0);
        List<? extends SlicexData> response = eSDataProvider.fetchGraphData(QueryType.slicexChart, duration, set,
                SlicexInterval.DAILY);
        assertNotNull(response);
    }

    @Test
    public void testFetchGraphDataAdvertiserLogin() throws Exception {
        Duration duration = new Duration();
        duration.setStartTimeStamp(2345L);
        duration.setEndTimeStamp(5432L);
        Set<SlicexFilter> set = new HashSet<>();
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        Mockito.when(loginService.isAdvertiserLogin()).thenReturn(true);
        List<? extends SlicexData> response = eSDataProvider.fetchGraphData(QueryType.slicexChart, duration, set,
                SlicexInterval.MONTHLY);
        assertNotNull(response);
    }

    @Test
    public void testFetchGraphDataSuperAdminUser() throws Exception {
        Duration duration = new Duration();
        duration.setStartTimeStamp(2345L);
        duration.setEndTimeStamp(5432L);
        Set<SlicexFilter> set = new HashSet<>();
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        Mockito.when(loginService.isSuperAdminUser()).thenReturn(true);
        List<? extends SlicexData> response = eSDataProvider.fetchGraphData(QueryType.slicexChart, duration, set,
                SlicexInterval.HOURLY);
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.EsDataProvider#fetchGridData(QueryType, SlicexEntity, Duration, Set<SlicexFilter>, SlicexMetricEnum, SortOrder, Duration)}
     */
    @Test
    public void testFetchGridData() throws Exception{
        Set<SlicexFilter> set = new HashSet<>();
        set.add(MockDataGenerator.getSlicexFilter());
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        List<? extends SlicexData> response = eSDataProvider.fetchGridData(QueryType.slicexChart, SlicexEntity.pixel
                , MockDataGenerator.getDashboardRequest().getDuration(), set, SlicexMetricsEnum.conversions
                , SortOrder.asc,MockDataGenerator.getDashboardRequest().getDuration());
        assertNotNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.EsDataProvider#fetchHourlyDataForPixel(QueryType, SlicexEntity, Duration, Set<SlicexFilter>, SlicexMetricEnum, SortOrder, Duration)}
     */
    @Test
    public void testFetchHourlyDataForPixel() throws Exception{
        Set<SlicexFilter> set = new HashSet<>();
        set.add(MockDataGenerator.getSlicexFilter());
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        List<? extends SlicexData> response = eSDataProvider.fetchHourlyDataForPixel(QueryType.slicexChart, SlicexEntity.pixel
                , MockDataGenerator.getDashboardRequest().getDuration(), set, SlicexMetricsEnum.conversions
                , SortOrder.asc,MockDataGenerator.getDashboardRequest().getDuration());
        assertNotNull(response);
        assertEquals(response.get(0).clickInstalls,new BigDecimal("315.0"));
    }

    @Test
    public void testFetchHourlyDataForPixels() throws Exception{
        Set<SlicexFilter> set = new HashSet<>();
        set.add(MockDataGenerator.getSlicexFilters());
        Mockito.when(env.getProperty("elasticsearch.hourly.host")).thenReturn("10.125.63.198");
        Mockito.when(env.getProperty("elasticsearch.hourly.port")).thenReturn("9200");
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn("{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":86,\"successful\":86,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":751069,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"by_entity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":219,\"doc_count\":751069,\"clickConversions\":{\"value\":76.0},\"viewConversions\":{\"value\":862.0},\"revenue\":{\"value\":83414.544513952},\"cost\":{\"value\":61154.586692173},\"impInstalls\":{\"value\":2706.0},\"clicks\":{\"value\":71566.0},\"impressions\":{\"value\":1.3047289E7},\"clickInstalls\":{\"value\":315.0}}]}}}");
        List<? extends SlicexData> response = eSDataProvider.fetchHourlyDataForPixel(QueryType.slicexChart, SlicexEntity.pixel
                , MockDataGenerator.getDashboardRequest().getDuration(), set, SlicexMetricsEnum.conversions
                , SortOrder.asc,MockDataGenerator.getDashboardRequest().getDuration());
        assertNotNull(response);
        assertEquals(response.get(0).clickInstalls,new BigDecimal("315.0"));
    }

}
