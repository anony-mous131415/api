package io.revx.api.service.appsettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.AppSettingsEntity;
import io.revx.api.mysql.entity.AppSettingsPropertyEntity;
import io.revx.api.mysql.repo.AppSettingsPropertyRepository;
import io.revx.api.mysql.repo.AppSettingsRepository;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class AppSettingsServiceTest extends BaseTestService {

    @InjectMocks
    private AppSettingsService appSettingsService;

    @Mock
    private ModelConverterService modelConverterService;

    @Mock
    private ValidationService validationService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private AppSettingsRepository settingsRepository;

    @Mock
    private AppSettingsPropertyRepository propertyRepository;

    @Mock
    private AppSettingsUtil settingsUtil;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private EntityESService esService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        appSettingsService = new AppSettingsService(validationService,loginUserDetailsService,settingsRepository,
                propertyRepository,modelConverterService,settingsUtil, esService);
    }

    @Test
    public void testGetAppSettingsWithKeyAndAdvertiserId() throws ValidationException {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(settingsRepository.findByAdvertiserIdAndKeyInAndActive
                (Mockito.anyLong(),Mockito.any(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.getAppSettingEntityList());
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.getSettings(Collections.singletonList(AppSettingsKey.LOGO_LINK),7146L);
        assertNotNull(settingsDTO);
        assertEquals(7146L,settingsDTO.getRespObject().get(0).getAdvertiserId());
        assertEquals(1,settingsDTO.getRespObject().size());
    }

    @Test
    public void testGetAppSettingsWithKeyOnly() throws ValidationException {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(settingsRepository.findByLicenseeIdAndKeyInAndActive
                (Mockito.anyLong(),Mockito.any(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.getAppSettingEntityList());
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.getSettings(Collections.singletonList(AppSettingsKey.LOGO_LINK),null);
        assertNotNull(settingsDTO);
        assertEquals(7146L,settingsDTO.getRespObject().get(0).getAdvertiserId());
        assertEquals(1,settingsDTO.getRespObject().size());
    }

    @Test
    public void testGetAppSettingsWithAdvertiserIdOnly() throws ValidationException {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(settingsRepository.findByAdvertiserIdAndActive
                (Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.getAppSettingEntityList());
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.getSettings(null,7146L);
        assertNotNull(settingsDTO);
        assertEquals(7146L,settingsDTO.getRespObject().get(0).getAdvertiserId());
        assertEquals(1,settingsDTO.getRespObject().size());
    }

    @Test
    public void testGetAppSettingsWithLicenseeIdOnly() throws ValidationException {
        Mockito.when(loginUserDetailsService.getLicenseeId()).thenReturn(33L);
        Mockito.when(settingsRepository.findByLicenseeIdAndActive
                (Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(MockDataGenerator.getAppSettingEntityList());
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.getSettings(null,null);
        assertNotNull(settingsDTO);
        assertEquals(7146L,settingsDTO.getRespObject().get(0).getAdvertiserId());
        assertEquals(1,settingsDTO.getRespObject().size());
    }

    @Test
    public void testCreateAppSettingsWithLogoType() throws ValidationException, JsonProcessingException {
        Mockito.when(modelConverterService.populateAppSettingEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsEntity());
        Mockito.when(settingsUtil.copyImageAndGenerateLink(Mockito.anyString(),Mockito.anyLong(),Mockito.any()))
                .thenReturn("http://origin.atomex.net/atomex-ui/static/adv-logos/7016/test.png");
        Mockito.when(settingsRepository.save(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsEntity());
        Mockito.when(modelConverterService.populatePropertyEntity(Mockito.any(),Mockito.any()))
                .thenReturn(MockDataGenerator.generatePropertyEntityWithOutIds());
        Mockito.when(propertyRepository.saveAll(Mockito.anyCollection()))
                .thenReturn(MockDataGenerator.getPropertyEntityListWithIds());
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any()))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.createSettings(MockDataGenerator.generateSettingsDTOListWithOutIds());
        assertNotNull(settingsDTO);
        assertEquals(1,settingsDTO.getRespObject().size());
        assertEquals(1,settingsDTO.getRespObject().get(0).getAppSettingsProperties().size());
    }

    @Test
    public void testUpdateAppSettings() throws ValidationException {
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any(AppSettingsEntity.class)))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        Mockito.doReturn(Optional.of(MockDataGenerator.generateAppSettingsEntity()))
                .when(settingsRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(MockDataGenerator.generatePropertyEntity()))
                .when(propertyRepository).findById(Mockito.anyLong());
        Mockito.when(settingsRepository.save(Mockito.any(AppSettingsEntity.class)))
                .thenReturn(MockDataGenerator.generateAppSettingsEntity());
        Mockito.when(propertyRepository.save(Mockito.any(AppSettingsPropertyEntity.class)))
                .thenReturn(MockDataGenerator.generatePropertyEntity());
        ApiResponseObject<List<AppSettingsDTO>> settingsDTO =
                appSettingsService.updateSettings(MockDataGenerator.generateSettingsDTOList());
        assertNull(settingsDTO.getError());
        assertNotNull(settingsDTO.getRespObject());
        assertEquals(1,settingsDTO.getRespObject().size());
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAppSettingsWithOutSettingsId() throws ValidationException {
        appSettingsService.updateSettings(MockDataGenerator.generateSettingsDTOList());
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAppSettingsWithOutPropertyId() throws ValidationException {
        Mockito.when(modelConverterService.populateAppSettingsFromEntity(Mockito.any(AppSettingsEntity.class)))
                .thenReturn(MockDataGenerator.generateAppSettingsDTO());
        Mockito.doReturn(Optional.of(MockDataGenerator.generateAppSettingsEntity()))
                .when(settingsRepository).findById(Mockito.any());
        appSettingsService.updateSettings(MockDataGenerator.generateSettingsDTOList());
    }
}
