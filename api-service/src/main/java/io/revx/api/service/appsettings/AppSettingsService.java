package io.revx.api.service.appsettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.mysql.entity.AppSettingsEntity;
import io.revx.api.mysql.entity.AppSettingsPropertyEntity;
import io.revx.api.mysql.repo.AppSettingsPropertyRepository;
import io.revx.api.mysql.repo.AppSettingsRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.model.AppSettingsPropertyDTO;
import io.revx.core.model.LogoModel;
import io.revx.core.response.ApiResponseObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppSettingsService {

    private static final Logger logger = LogManager.getLogger(AppSettingsService.class);

    private static final List<AppSettingsKey> keyTypes = Stream.
            of(AppSettingsKey.LOGO_LINK, AppSettingsKey.FALLBACK_IMG_LINK,
                    AppSettingsKey.OVERLAY_IMG_LINK, AppSettingsKey.DEFAULT_LOGO).collect(Collectors.toList());

    private final ValidationService validationService;

    private final LoginUserDetailsService userDetailsService;

    private final AppSettingsRepository repository;

    private final AppSettingsPropertyRepository propertyRepository;

    private final ModelConverterService converterService;

    private final AppSettingsUtil settingsUtil;

    private final EntityESService esService;

    public AppSettingsService(ValidationService validationService, LoginUserDetailsService userDetailsService,
                              AppSettingsRepository repository, AppSettingsPropertyRepository propertyRepository,
                              ModelConverterService converterService, AppSettingsUtil settingsUtil, EntityESService esService) {
        this.validationService = validationService;
        this.userDetailsService = userDetailsService;
        this.repository = repository;
        this.propertyRepository = propertyRepository;
        this.converterService = converterService;
        this.settingsUtil = settingsUtil;
        this.esService = esService;
    }

    /**
     *  Return the appSettings for the queried Licensee or Advertiser.
     *  currently only return the active settings
     *
     * @param appSettings - Incoming DTO for creating new settings
     * @return - ApiResponse object containing response or error
     * @throws ValidationException - thrown when advertiser is not valid
     */
    @Transactional
    public ApiResponseObject<List<AppSettingsDTO>>
            createSettings(List<AppSettingsDTO> appSettings) throws ValidationException, JsonProcessingException {

        Set<Long> advertiserIds = new HashSet<>();
        appSettings.forEach(c -> advertiserIds.add(c.getAdvertiserId()));
        for (Long advertiserId : advertiserIds) {
            validationService.isValidAdvertiserId(advertiserId);
        }
        List<AppSettingsEntity> appSettingsEntities = new ArrayList<>();
        for (AppSettingsDTO appSettingsDTO : appSettings) {
            AppSettingsEntity entity = converterService.populateAppSettingEntity(appSettingsDTO);
            if (entity != null && keyTypes.contains(appSettingsDTO.getSettingsKey())) {
                String imageUrl = settingsUtil
                        .copyImageAndGenerateLink(entity.getValue(),entity.getAdvertiserId(),entity.getKey());
                entity.setValue(imageUrl);
            }
            if (entity != null) {
                AppSettingsEntity settingsEntity = repository.save(entity);
                logger.debug("Updated AppSettings Entity : {}",settingsEntity);
                saveProperties(settingsEntity, appSettingsDTO.getAppSettingsProperties());
                saveToElasticSearch(settingsEntity);
                appSettingsEntities.add(settingsEntity);
            }
        }
        List<AppSettingsDTO> appSettingsDTOList = populateAppSettingsDTO(appSettingsEntities);
        ApiResponseObject<List<AppSettingsDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(appSettingsDTOList);
        return responseObject;
    }

    private void saveToElasticSearch(AppSettingsEntity settingsEntity) throws JsonProcessingException {
        if (settingsEntity.getKey().equals(AppSettingsKey.LOGO_LINK)) {
            LogoModel logoModel = converterService.populateLogoModel(settingsEntity);
            esService.save(logoModel, TablesEntity.ADVERTISER_LOGO);
        }
    }

    /**
     * After saving the parent setting entity the properties associated with this setting entity
     * are also saved to db , The relation between Settings Entity and properties is 1:n
     *
     * @param entity - Settings for which the properties are saved for
     * @param appSettingsProperties - A settings item can be associated with any number of properties
     */
    private void saveProperties(AppSettingsEntity entity, List<AppSettingsPropertyDTO> appSettingsProperties) {
        List<AppSettingsPropertyEntity> propertyEntities = new ArrayList<>();

        for (AppSettingsPropertyDTO settingsPropertyDTO : appSettingsProperties) {
            AppSettingsPropertyEntity propertyEntity =
                    converterService.populatePropertyEntity(settingsPropertyDTO,entity);
            propertyEntities.add(propertyEntity);
        }
        if (!propertyEntities.isEmpty()) {
            propertyEntities = propertyRepository.saveAll(propertyEntities);
            logger.debug("Updated AppSettings Property Entity : {} for App Settings id : {}",
                    propertyEntities, entity.getId());
            entity.setAppSettingsPropertyEntities(propertyEntities);
        }
    }

    /**
     * Return the appSettings for the queried Licensee or Advertiser or Key.
     * currently only return the active settings
     *
     * @param settingsKeys - Like LOGO_LINK , OVERLAY_IMG_LINK etc
     * @param advertiserId (optional)
     * @return - Returns a list of AppSettings
     * @throws ValidationException - validation is done at advertiser level if it come under
     * the selected licensee or not.
     */
    public ApiResponseObject<List<AppSettingsDTO>> getSettings(List<AppSettingsKey> settingsKeys, Long advertiserId)
            throws ValidationException {

        Long licenseeId = userDetailsService.getLicenseeId();
        if (advertiserId != null) {
            validationService.isValidAdvertiserId(advertiserId);
        }
        List<AppSettingsEntity> appSettingsEntities;
        if (advertiserId != null && settingsKeys != null) {
            appSettingsEntities = repository.findByAdvertiserIdAndKeyInAndActive(advertiserId, settingsKeys,true);
        } else if (settingsKeys != null) {
            appSettingsEntities = repository.findByLicenseeIdAndKeyInAndActive(licenseeId, settingsKeys, true);
        } else if (advertiserId != null) {
            appSettingsEntities = repository.findByAdvertiserIdAndActive(advertiserId, true);
        } else {
            appSettingsEntities = repository.findByLicenseeIdAndActive(licenseeId, true);
        }
        List<AppSettingsDTO> appSettingsList = populateAppSettingsDTO(appSettingsEntities);
        ApiResponseObject<List<AppSettingsDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(appSettingsList);
        return responseObject;
    }

    /**
     * Converts the List of AppSettingsEntity to List of AppSettingsDTO
     *
     * @param entities List of AppSettings fetched from DB
     * @return AppSettings DTO
     */
    private List<AppSettingsDTO> populateAppSettingsDTO(List<AppSettingsEntity> entities) {
        List<AppSettingsDTO> appSettingsList = new ArrayList<>();
        entities.forEach(c ->
                appSettingsList.add(converterService.populateAppSettingsFromEntity(c)));
        return appSettingsList;
    }

    /**
     * Update the AppSettings entities along with the associated properties (The payload
     * contains only the properties that needs to be updated i.e delta)
     *
     * @param appSettings - Input payload for which updated needs to be done
     * @return - List of Settings DTO populated with the updated values
     * @throws ValidationException - Validation before updating entity if it is present or ot
     */
    @Transactional
    public ApiResponseObject<List<AppSettingsDTO>>
            updateSettings( List<AppSettingsDTO> appSettings) throws ValidationException {
        Set<Long> advertiserIds = new HashSet<>();

        appSettings.forEach(c-> advertiserIds.add(c.getAdvertiserId()));
        validationService.isValidAdvertiserId(advertiserIds);

        List<AppSettingsDTO> settingsDTOList = new ArrayList<>();
        for (AppSettingsDTO settingsDTO : appSettings) {
            Optional<AppSettingsEntity> entity = repository.findById(settingsDTO.getId());
            if (entity.isPresent()) {
                AppSettingsEntity settingsEntity = entity.get();
                updatePropertyValues(settingsDTO);
                updateAppSettingsEntity(settingsEntity, settingsDTO);
                AppSettingsEntity responseEntity = repository.save(settingsEntity);
                AppSettingsDTO appSettingsDTO = converterService.populateAppSettingsFromEntity(responseEntity);
                settingsDTOList.add(appSettingsDTO);
            } else {
                logger.error("Unable to update AppSettings for id : {} as is invalid",settingsDTO.getId());
                throw new ValidationException("AppSettings id is not valid");
            }
        }
        ApiResponseObject<List<AppSettingsDTO>> response = new ApiResponseObject<>();
        response.setRespObject(settingsDTOList);
        return response;
    }

    /**
     * The respective properties are updated first for a AppSettingsEntity
     *
     * @param settingsDTO - Input payload individual entity
     * @throws ValidationException AppSettingsProperty Id is validated if present or not before
     * updating
     */
    private void updatePropertyValues(AppSettingsDTO settingsDTO) throws ValidationException{
        List<AppSettingsPropertyDTO> propertyDTOS = settingsDTO.getAppSettingsProperties();
        if (propertyDTOS != null) {
            for (AppSettingsPropertyDTO propertyDTO : propertyDTOS) {
                Optional<AppSettingsPropertyEntity> propertyEntity =
                        propertyRepository.findById(propertyDTO.getId());
                if (propertyEntity.isPresent()) {
                    converterService.updateAppSettingsPropertyEntity(propertyDTO, propertyEntity.get());
                    propertyRepository.save(propertyEntity.get());
                } else {
                    logger.error("Unable to update AppSettings Property for id : {} as is invalid"
                            ,settingsDTO.getId());
                    throw new ValidationException("AppSettings Property id is not valid");
                }
            }
        }
    }

    private void updateAppSettingsEntity(AppSettingsEntity settingsEntity, AppSettingsDTO settingsDTO) {
        settingsEntity.setKey(settingsDTO.getSettingsKey());
        if (keyTypes.contains(settingsDTO.getSettingsKey()) &&
                !settingsEntity.getValue().equals(settingsDTO.getSettingsValue())) {
            String imageUrl = settingsUtil
                    .copyImageAndGenerateLink(settingsDTO.getSettingsValue(),settingsEntity.getAdvertiserId(),
                            settingsEntity.getKey());
            settingsEntity.setValue(imageUrl);
        } else {
            settingsEntity.setValue(settingsDTO.getSettingsValue());
        }
        settingsEntity.setActive(settingsDTO.getActive());
    }
}