package io.revx.api.service.creative;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ValidationService;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.creative.CreativeType;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.UserInfo;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.service.ModelConverterService;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CreativeServiceTest extends BaseTestService {

    @Mock
    CreativeMockUpUtil creativeMockUpUtil;

    @Mock
    LoginUserDetailsService userDetailsService;

    @Mock
    ValidationService validationService;

    @Mock
    EntityESService esService;

    @Mock
    ApplicationProperties properties;

    @Mock
    CreativeValidationService creativeValidationService;

    @Mock
    CreativeUtil creativeUtil;

    @Mock
    CreativeCacheService cache;

    @Mock
    ModelConverterService modelConverter;

    @InjectMocks
    CreativeService creativeService;

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
        creativeService = new CreativeService();
        creativeMockUpUtil = new CreativeMockUpUtil();
        creativeValidationService = new CreativeValidationService();
        creativeService.validator = creativeValidationService;
        creativeService.validationService = validationService;
        creativeService.mockUpUtil = creativeMockUpUtil;
        creativeValidationService.validator = validationService;
        creativeMockUpUtil.user = userDetailsService;
        creativeMockUpUtil.elasticSearch = esService;
        creativeMockUpUtil.properties = properties;
        creativeMockUpUtil.util = creativeUtil;
        creativeService.cache = cache;
        creativeService.modelConverter=modelConverter;
    }

    @Test
    public void testHtmlMockups() throws ValidationException {
        mockSecurityContext("Govardhan",false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(userDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(esService.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(new BaseModel(7156,"test_advertiser"));
        Mockito.when(properties.getTemporaryCreativeDirectoryPath())
                .thenReturn("/atom/origin/cr_temp/");
        /*Mockito.when(properties.getCreativeDirectoryPath()).thenReturn("/atom/origin/");
        Mockito.when(properties.getCreativeUrlPrependTemp()).thenReturn("http://origin.atomex.net/");*/
        Mockito.when(creativeUtil.generateUniqueId()).thenReturn("555dc4");
        /*Mockito.when(creativeUtil.replaceClickNoEncodingString(Mockito.anyString(),Mockito.anyString()))
                .thenReturn("html");*/
        ApiListResponse<CreativeDTO> creativeDTOList = creativeService
                .htmlMockups(MockDataGenerator.generateHtmlMockup());
        assertNotNull(creativeDTOList);
        assertEquals(1,creativeDTOList.getTotalNoOfRecords());
        assertNotNull(creativeDTOList.getData());
        assertEquals("HTML",creativeDTOList.getData().get(0).getContent());
    }

    @Test
    public void testHtmlDCOMockups() throws ValidationException {
        mockSecurityContext("Govardhan",false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(userDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(esService.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(new BaseModel(7156,"test_advertiser"));
        Mockito.when(properties.getTemporaryCreativeDirectoryPath())
                .thenReturn("/atom/origin/cr_temp/");
        /*Mockito.when(properties.getCreativeDirectoryPath()).thenReturn("/atom/origin/");
        Mockito.when(properties.getCreativeUrlPrependTemp()).thenReturn("http://origin.atomex.net/");*/
        Mockito.when(creativeUtil.generateUniqueId()).thenReturn("555dc4");
        /*Mockito.when(creativeUtil.replaceClickNoEncodingString(Mockito.anyString(),Mockito.anyString()))
                .thenReturn("html");*/
        /*Mockito.when(creativeUtil.constructMacroStringFromDcoDatabase(Mockito.any(),Mockito.any(),Mockito.anyString()))
                .thenReturn("html");*/
        /*Mockito.when(creativeUtil.replaceDynamicMacroWithContent(Mockito.anyString(),Mockito.anyString()))
                .thenReturn("html");*/
        ApiListResponse<CreativeDTO> creativeDTOList = creativeService
                .htmlMockups(MockDataGenerator.generateHtmlDcoMockup());
        assertNotNull(creativeDTOList);
        assertEquals(1,creativeDTOList.getTotalNoOfRecords());
        assertNotNull(creativeDTOList.getData());
        assertEquals("HTML",creativeDTOList.getData().get(0).getContent());
    }

    @Test
    public void testHtmlMockupsWithEmptyContent() throws ValidationException {
        mockSecurityContext("Govardhan",false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(userDetailsService.getUserInfo()).thenReturn(ui);
        Mockito.when(esService.searchById(Mockito.any(),Mockito.anyLong()))
                .thenReturn(new BaseModel(7156,"test_advertiser"));
        Mockito.when(properties.getTemporaryCreativeDirectoryPath())
                .thenReturn("/atom/origin/cr_temp/");
        CreativeHtmlMockupDTO mockupDTO = MockDataGenerator.generateHtmlMockup();
        mockupDTO.getCreativeHtmlFiles().get(0).setHtmlContent("");
        ApiListResponse<CreativeDTO> creativeDTOList = creativeService
                .htmlMockups(mockupDTO);
        assertNotNull(creativeDTOList);
        assertEquals(1,creativeDTOList.getTotalNoOfRecords());
        assertNotNull(creativeDTOList.getData().get(0).getErrorMsg());
    }

    @Test
    public void testHtmlMockupsWithOtherTypes() throws ValidationException {
        mockSecurityContext("Govardhan",false, false);
        CreativeHtmlMockupDTO mockupDTO = MockDataGenerator.generateHtmlMockup();
        mockupDTO.getCreativeHtmlFiles().get(0).setType(CreativeType.html);
        ApiListResponse<CreativeDTO> creativeDTOList = creativeService
                .htmlMockups(mockupDTO);
        assertNotNull(creativeDTOList);
        assertEquals(0,creativeDTOList.getTotalNoOfRecords());
    }

    @Test
    public void TestSearchCompactCreatives() throws ValidationException {
        SearchRequest searchRequest = new SearchRequest();

        List<DashboardFilters> dashboardFiltersList = new ArrayList<>();
        DashboardFilters filter1 = new DashboardFilters("advertiserId", "7146");
        DashboardFilters filter2 = new DashboardFilters("status", "active");

        dashboardFiltersList.add(filter1);
        dashboardFiltersList.add(filter2);
        searchRequest.setFilters(dashboardFiltersList);

        List<CreativeCompactDTO> list = new ArrayList<>();
        list.addAll(TestDataGenerator.getListOfObject(20, CreativeCompactDTO.class));
        ApiListResponse<CreativeCompactDTO> apiListResponse = new ApiListResponse<>();
        Mockito.when(cache.fetchCompactCreatives(searchRequest, "id", false, false)).thenReturn(list);
        apiListResponse.setData(list);

        Mockito.when(modelConverter.getSubList(list, 1, 5)).thenReturn(list.subList(0, 5));

        ApiListResponse<CreativeCompactDTO> resp = creativeService.searchCompactCreatives(searchRequest, 1, 5, "id", false, false);

        assertNotNull(resp);
        assertThat(resp.getData().size()).isEqualTo(5);
    }

    @Test(expected = ValidationException.class)
    public void TestSearchCompactCreativesValidation() throws ValidationException {
        SearchRequest searchRequest = new SearchRequest();

        List<CreativeCompactDTO> list = new ArrayList<>();
        list.addAll(TestDataGenerator.getListOfObject(20, CreativeCompactDTO.class));
        ApiListResponse<CreativeCompactDTO> apiListResponse = new ApiListResponse<>();
        //Mockito.when(cache.fetchCompactCreatives(searchRequest, "id", false, false)).thenReturn(list);
        apiListResponse.setData(list);

        //Mockito.when(modelConverter.getSubList(list, 1, 5)).thenReturn(list.subList(0, 5));

        ApiListResponse<CreativeCompactDTO> resp = creativeService.searchCompactCreatives(searchRequest, 1, 5, "id", false, false);
    }

}
