package io.revx.api.service.creative.template.theme;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.creative.CreativeTemplateThemeEntity;
import io.revx.api.mysql.repo.creative.CreativeTemplateThemeRepo;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.TemplateThemeDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TemplateThemeServiceTest extends BaseTestService {

    @InjectMocks
    private TemplateThemeService themeService;

    @Mock
    private ValidationService validationService;

    @Mock
    private CreativeTemplateThemeRepo themeRepo;

    @Mock
    private CreativeUtil creativeUtil;

    @Mock
    private LoginUserDetailsService userDetailsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        creativeUtil = new CreativeUtil();
        themeService = new TemplateThemeService(themeRepo, creativeUtil, userDetailsService);
        themeService.setValidationService(validationService);
    }

    @Test
    public void testGetTemplatesByAdvertiserId() throws ValidationException {
        Mockito.when(themeRepo.findByAdvertiserId(Mockito.anyLong()))
                .thenReturn(MockDataGenerator.generateTemplateThemesList());
        ApiResponseObject<List<TemplateThemeDTO>> responseObject =
                themeService.getThemesForAdvertiser(1234L);
        assertNotNull(responseObject);
        assertEquals(2, responseObject.getRespObject().size());
    }

    @Test
    public void testGetTemplatesById() throws ValidationException {
        CreativeTemplateThemeEntity entity = MockDataGenerator.getTemplateThemeEntity(true,
                "theme_ny_name" ,"style_json_3",12L,3456L, 1234L
                ,16756098L,16504456L, 1989L);
        Mockito.when(themeRepo.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entity));
        ApiResponseObject<TemplateThemeDTO> responseObject =
                themeService.getThemeById(3L);
        assertNotNull(responseObject);
        assertEquals(12L,responseObject.getRespObject().getId().longValue());
    }

    @Test(expected = ValidationException.class)
    public void testGetTemplatesByWithNoValidEntry() throws ValidationException {
        Mockito.when(themeRepo.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        themeService.getThemeById(3L);
    }

    @Test
    public void testCreateTemplateTheme() throws ValidationException {
        mockSecurityContext("Govardhan",false,false);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("NewTheme1",
                        "StyleJson1", 3456L,null, null, null);
        CreativeTemplateThemeEntity entity = MockDataGenerator.getTemplateThemeEntity(true,
                "NewTheme1" ,"StyleJson1",12L,3456L, 1234L
                ,System.currentTimeMillis()/1000,null, null);
        Mockito.when(themeRepo.save(Mockito.any(CreativeTemplateThemeEntity.class)))
                .thenReturn(entity);
        Mockito.when(userDetailsService.getUserInfo()).thenReturn(userInfo);
        ApiResponseObject<TemplateThemeDTO> responseObject = themeService.createTemplateTheme(themeDTO);
        assertNotNull(responseObject);
        assertEquals(12,responseObject.getRespObject().getId().longValue());
    }

    @Test(expected = ValidationException.class)
    public void testCreateTemplateThemeWithId() throws ValidationException {
        themeService.setValidationService(new ValidationService());
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("NewTheme1",
                        "StyleJson1", 3456L,12L,null,null);
        themeService.createTemplateTheme(themeDTO);
    }

    @Test(expected = ValidationException.class)
    public void testCreateTemplateThemeWithNoName() throws ValidationException {
        themeService.setValidationService(new ValidationService());
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("","StyleJson1", 3456L,null, null, null);
        themeService.createTemplateTheme(themeDTO);
    }

    @Test(expected = ValidationException.class)
    public void testCreateTemplateThemeWithNoVariables() throws ValidationException {
        themeService.setValidationService(new ValidationService());
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("NewTheme1","", 3456L,
                        null, null, null);
        themeService.createTemplateTheme(themeDTO);
    }

    @Test
    public void testUpdateTemplateTheme() throws ValidationException {
        CreativeTemplateThemeEntity themeEntity = MockDataGenerator.getTemplateThemeEntity(true,
                "theme_ny_name" ,"style_json_3",12L,3456L, 1234L
                ,16756098L,16504456L, 1989L);
        Mockito.when(themeRepo.findById(Mockito.any())).thenReturn(Optional.of(themeEntity));
        Mockito.when(themeRepo.save(Mockito.any(CreativeTemplateThemeEntity.class))).thenReturn(themeEntity);
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("NewTheme1","",
                        3456L,null,1234L,16789567L);
        ApiResponseObject<TemplateThemeDTO> responseObject = themeService.updateTemplateTheme(themeDTO);
        assertNotNull(responseObject);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateTemplateThemeWithInvalidId() throws ValidationException {
        CreativeTemplateThemeEntity themeEntity = MockDataGenerator.getTemplateThemeEntity(true,
                "theme_ny_name" ,"style_json_3",12L,3456L, 1234L
                ,16756098L,16504456L, 1989L);
        Mockito.when(themeRepo.findById(Mockito.any())).thenReturn(Optional.empty());
        TemplateThemeDTO themeDTO = MockDataGenerator
                .generateTemplateThemeDTO("NewTheme1","",
                        3456L,null,1234L,16789567L);
        themeService.updateTemplateTheme(themeDTO);
    }
}
