package io.revx.api.service.creative.template.theme;

import io.revx.api.mysql.entity.creative.CreativeTemplateThemeEntity;
import io.revx.api.mysql.repo.creative.CreativeTemplateThemeRepo;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.TemplateThemeDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TemplateThemeService {

    private static final Logger logger = LogManager.getLogger(TemplateThemeService.class);

    private ValidationService validationService;
    private final CreativeTemplateThemeRepo themeRepo;
    private final CreativeUtil creativeUtil;
    private final LoginUserDetailsService userDetailsService;

    public TemplateThemeService( CreativeTemplateThemeRepo themeRepo,
            CreativeUtil creativeUtil, LoginUserDetailsService userDetailsService) {
        this.themeRepo = themeRepo;
        this.creativeUtil = creativeUtil;
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Fetching the existing template themes from the db for a given advertiser id
     *
     * @param advertiserId - advertiser id for which template themes need to be fetched
     * @return - List of template themes
     * @throws ValidationException thrown id the advertiser id is not valid
     */
    public ApiResponseObject<List<TemplateThemeDTO>> getThemesForAdvertiser(Long advertiserId) throws ValidationException {
        validationService.isValidAdvertiserId(advertiserId);
        List<CreativeTemplateThemeEntity> themeEntities = themeRepo.findByAdvertiserId(advertiserId);
        logger.debug("Theme entities for advertiser id : {} are {}", advertiserId, themeEntities);
        List<TemplateThemeDTO> themeDTOS = creativeUtil.populateTemplateThemeDTOs(themeEntities);
        ApiResponseObject<List<TemplateThemeDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTOS);
        logger.debug("List of ThemeDTO's for the advertiser id : {} are : {}", advertiserId, themeDTOS);
        return responseObject;
    }

    /**
     * Fetching template theme for a given id from the DB
     *
     * @param id - Id for which the theme data needs to be retrieved
     * @return - Template theme corresponding to the id
     * @throws ValidationException thrown if the id is not valid
     */
    public ApiResponseObject<TemplateThemeDTO> getThemeById(Long id) throws ValidationException {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        ApiResponseObject<TemplateThemeDTO> responseObject;

        Optional<CreativeTemplateThemeEntity> themeEntity = themeRepo.findById(id);
        if (!themeEntity.isPresent()) {
            throw new ValidationException("Template Theme id is not valid");
        }
        logger.debug("Theme entity for id : {} is {}", id, themeEntity.get());
        creativeUtil.populateThemeFromEntity(themeEntity.get(), themeDTO);
        responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);
        logger.debug("ThemeDTO for the id : {} is : {}", id, themeDTO);
        return responseObject;
    }

    /**
     * Creating a new template theme for the advertiser based on incoming payload
     * 1. validating the payload
     * 2. generating the entity from the payload and populated other fields
     * 3. saving to DB and responding with the data saved in DB
     *
     * @param themeDTO - incoming payload for creating template theme
     * @return - Theme DTO with data saved in DB
     * @throws ValidationException thrown if payload is not valid.
     */
    @Transactional
    public ApiResponseObject<TemplateThemeDTO> createTemplateTheme(TemplateThemeDTO themeDTO) throws ValidationException {
        CreativeTemplateThemeEntity themeEntity = generateThemeEntity(themeDTO);
        CreativeTemplateThemeEntity createdEntity = themeRepo.save(themeEntity);
        logger.debug("Theme entity saved in DB is : {} ", createdEntity);
        creativeUtil.populateThemeFromEntity(createdEntity, themeDTO);
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);
        logger.debug("ThemeDTO populated from entity is {} ", themeDTO);
        return responseObject;
    }

    /**
     * Updating a existing template theme with new values in payload
     * 1. checking if theme is present with the id and validating the payload
     * 2. update the entity based on payload
     * 3. saving to DB and responding with the data saved in DB
     *
     * @param themeDTO - incoming payload for creating template theme
     * @return - Theme DTO with data saved in DB
     * @throws ValidationException thrown if payload is not valid.
     */
    @Transactional
    public ApiResponseObject<TemplateThemeDTO> updateTemplateTheme(TemplateThemeDTO themeDTO) throws ValidationException {

        Optional<CreativeTemplateThemeEntity> themeEntity = themeRepo.findById(themeDTO.getId());
        if (!themeEntity.isPresent()) {
            throw new ValidationException("Template Theme is not valid");
        }
        logger.debug("Theme entity for the id : {} is {}", themeDTO.getId(), themeEntity.get());
        updateThemeEntity(themeEntity.get(), themeDTO);
        CreativeTemplateThemeEntity newThemeEntity = themeRepo.save(themeEntity.get());
        creativeUtil.populateThemeFromEntity(newThemeEntity, themeDTO);
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);
        logger.debug("ThemeDTO populated from entity is {} ", themeDTO);
        return responseObject;
    }

    /**
     * The incoming payload contains the values that needs to be updated, the values are
     * validated and the existing entity is populated correspondingly
     *
     * @param themeEntity - existing DB entity present in DB
     * @param themeDTO - the incoming payload with the changes
     * @throws ValidationException thrown if incoming payload does not contain valid fields
     */
    private void updateThemeEntity(CreativeTemplateThemeEntity themeEntity,
            TemplateThemeDTO themeDTO) throws ValidationException {
        validateThemeDTO(themeDTO, true);
        long createdTime = System.currentTimeMillis()/1000;
        UserInfo userInfo = userDetailsService.getUserInfo();
        Long userId = null;
        if (userInfo != null) {
            userId = userInfo.getUserId();
        }

        if (themeDTO.getThemeName() != null) {
            themeEntity.setThemeName(themeDTO.getThemeName());
        }

        if (themeDTO.getIsActive() != null) {
            themeEntity.setActive(themeDTO.getIsActive());
        }

        if (themeDTO.getStyleJson() != null) {
            themeEntity.setStyleJson(themeDTO.getStyleJson());
        }

        themeEntity.setModifiedOn(createdTime);
        themeEntity.setModifiedBy(userId);
        logger.debug("Updated theme entity is : {} ", themeEntity);
    }

    /**
     * New Theme entity is created from th payload and values like user id , time based values
     * are populated and that entity is returned
     *
     * @param themeDTO - incoming payload
     * @return - New entity populated from payload and other values
     * @throws ValidationException - thrown if incoming payload does not contain valid fields
     */
    private CreativeTemplateThemeEntity generateThemeEntity(TemplateThemeDTO themeDTO) throws ValidationException {
        validateThemeDTO(themeDTO, false);
        CreativeTemplateThemeEntity entity = new CreativeTemplateThemeEntity();
        long createdTime = System.currentTimeMillis()/1000;
        UserInfo userInfo = userDetailsService.getUserInfo();
        Long userId = null;
        if (userInfo != null) {
            userId = userInfo.getUserId();
        }
        entity.setAdvertiserId(themeDTO.getAdvertiserId());
        entity.setActive(true);
        entity.setThemeName(themeDTO.getThemeName());
        entity.setCreatedBy(userId);
        entity.setCreatedOn(createdTime);
        entity.setStyleJson(themeDTO.getStyleJson());
        logger.debug("Generated theme entity is : {} ", entity);

        return entity;
    }

    /**
     * Incoming payload should contain a valid name, style_json and advertiserId
     * and id should not be populated already when creating a new theme
     *
     * @param themeDTO - Incoming payload for updating/creation
     * @param isUpdate - flag to check validity of some fields based on updating/creation
     * @throws ValidationException thrown if found invalid values
     */
    private void validateThemeDTO(TemplateThemeDTO themeDTO, boolean isUpdate) throws ValidationException {
        if (!isUpdate && themeDTO.getId() != null) {
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
                    "Id value should be null when creating a new theme");
        }
        validationService.isBlank(themeDTO.getThemeName(), "Template theme name");
        validationService.isBlank(themeDTO.getStyleJson(), "Template style json data");
        validationService.isValidAdvertiserId(themeDTO.getAdvertiserId());
    }

}
