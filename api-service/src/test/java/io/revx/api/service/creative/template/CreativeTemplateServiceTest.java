package io.revx.api.service.creative.template;

import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.repo.creative.CreativeTemplateRepo;
import io.revx.api.service.ValidationService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeTemplateDTO;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CreativeTemplateServiceTest {

    @InjectMocks
    private CreativeTemplateService templateService;

    @Mock
    private CreativeTemplateRepo templateRepo;

    @Mock
    private CreativeUtil creativeUtil;

    @Mock
    private ValidationService validationService;

    @Mock
    private ApplicationProperties properties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        creativeUtil = new CreativeUtil();
        templateService = new CreativeTemplateService(templateRepo,creativeUtil,validationService,properties);
    }

    @Test
    public void testGetTemplatesWithNoSlots() {
        Mockito.when(templateRepo.findAllByActiveTrueAndDynamicAndSizeIn(Mockito.anyBoolean(), Mockito.anyList()
                , Mockito.any())).thenReturn(new PageImpl<>(MockDataGenerator.generateCreativeTemplateEntities()));
        ApiListResponse<CreativeTemplateDTO> responseObject =
                templateService.getTemplates(null,false,1,10, "320x480",1234L);
        assertNotNull(responseObject);
        assertEquals(3,responseObject.getTotalNoOfRecords());
        assertEquals("template_1",responseObject.getData().get(0).getTemplateName());
    }

    @Test
    public void testGetTemplatesWithSlots() {
        Mockito.when(templateRepo.findAllByActiveTrueAndSlotsLessThanEqualAndDynamicAndSizeIn(Mockito.anyInt()
                ,Mockito.anyBoolean(),Mockito.anyList(),Mockito.any()))
                .thenReturn(new PageImpl<>(MockDataGenerator.generateCreativeTemplateEntities()));
        ApiListResponse<CreativeTemplateDTO> responseObject =
                templateService.getTemplates(4,true,1,10, "320x480", 1234L);
        assertNotNull(responseObject);
        assertEquals(3,responseObject.getTotalNoOfRecords());
        assertEquals("template_1",responseObject.getData().get(0).getTemplateName());
    }

    @Test
    public void testSaveProductImages() throws ValidationException {
        Mockito.when(properties.getCreativeUrlPrependTemp()).thenReturn("http://origin.atomex.net/");
        Mockito.when(properties.getCreativeDirectoryPath()).thenReturn("/atom/origin/");
        Mockito.when(properties.getCreativesDirectory()).thenReturn("creatives/");
        ApiResponseObject<List<CreativeFiles>> responseObject =
                templateService.saveProductImages(MockDataGenerator.generateMockupDTO());
        assertNotNull(responseObject);
        File copiedFile = new File("/atom/origin/creatives/8146/83x83/nmitlogo.png");
        assertTrue(copiedFile.exists());
    }

}
