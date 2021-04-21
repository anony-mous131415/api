package io.revx.api.service.creative.template;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.creative.CreativeTemplateEntity;
import io.revx.api.mysql.entity.creative.TemplateMetaData;
import io.revx.api.mysql.repo.creative.CreativeTemplateRepo;
import io.revx.api.service.ValidationService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeDetails;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativeTemplateDTO;
import io.revx.core.model.creative.CreativeTemplatesMetadataDTO;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreativeTemplateService {

    private static final Logger logger = LogManager.getLogger(CreativeTemplateService.class);

    private final CreativeTemplateRepo templateRepo;
    private final CreativeUtil creativeUtil;
    private final ValidationService validationService;
    private final ApplicationProperties properties;

    public CreativeTemplateService(CreativeTemplateRepo templateRepo, CreativeUtil creativeUtil,
            ValidationService validationService, ApplicationProperties properties) {
        this.templateRepo = templateRepo;
        this.creativeUtil = creativeUtil;
        this.validationService = validationService;
        this.properties = properties;
    }

    /**
     * Fetches creative static templates from the DB with maximum number of
     * requested slots. Return all the templates if the slots is null
     *
     * @param slots - templates with required number of slots
     * @param isDynamic - this flag is et to fetch dynamic templates
     * @param pageNumber - page number for visualization in UI
     * @param pageSize - the number of templates to show in a single page
     * @return - List of Creative static templates
     */
    public ApiListResponse<CreativeTemplateDTO> getTemplates(Integer slots, Boolean isDynamic,
            Integer pageNumber, Integer pageSize, String templateSizes, Long advertiserId) {
        Page<CreativeTemplateEntity> templateEntities;
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, Sort.by( "slots").ascending());
        List<String> sizes = templateSizes != null ? Arrays.asList(templateSizes.split(",")) : null;
        if (slots == null && sizes == null) {
            templateEntities = templateRepo.findAllByActiveTrueAndDynamic(isDynamic, pageable);
        } else if (slots != null && sizes == null){
            templateEntities = templateRepo.
                    findAllByActiveTrueAndSlotsLessThanEqualAndDynamic(slots, isDynamic, pageable);
        } else if (slots == null) {
            templateEntities = templateRepo.
                    findAllByActiveTrueAndDynamicAndSizeIn( isDynamic, sizes, pageable);
        } else {
            templateEntities = templateRepo.
                    findAllByActiveTrueAndSlotsLessThanEqualAndDynamicAndSizeIn(slots, isDynamic, sizes, pageable);
        }
        logger.debug("Active templates with at least {} slots are {}", slots, templateEntities);
        List<CreativeTemplateDTO> creativeTemplateDTOList =
                creativeUtil.populateCreativeTemplateDTO(templateEntities.getContent(), advertiserId);
        ApiListResponse<CreativeTemplateDTO> responseObject = new ApiListResponse<>();
        responseObject.setData(creativeTemplateDTOList);
        responseObject.setTotalNoOfRecords( (int) templateEntities.getTotalElements());
        logger.debug("List of active template DTOs are : {}", creativeTemplateDTOList);
        return responseObject;
    }

    /**
     * The incoming payload contains product images located in the temporary location
     * The images are moved to permanent location with directory format like
     * /advertiserId/HeightXWidth/*.png/jpeg
     *
     * @param mockupDTO - Basic details along with list of images
     * @return - List of files with permanent location
     * @throws ValidationException - thrown if advertiser is not valid
     */
    public ApiResponseObject<List<CreativeFiles>> saveProductImages(CreativeMockUpsDTO mockupDTO) throws ValidationException {
        CreativeDetails details =  mockupDTO.getBasicDetails();
        validationService.isValidAdvertiserId(details.getAdvertiserId());
        List<CreativeFiles> creativeFiles = saveImageCreatives(mockupDTO);
        ApiResponseObject<List<CreativeFiles>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(creativeFiles);
        return responseObject;
    }

    /**
     * As the remaining properties are not needed , they are populated as is and
     * preview URL is set to permanent location
     *
     * @param mockUpDTO - Incoming payload with details and preview urls
     * @return - List of creative files with permanent preview URL's
     */
    private List<CreativeFiles> saveImageCreatives(CreativeMockUpsDTO mockUpDTO) {
        List<CreativeFiles> creativeFiles = new ArrayList<>();
        Long advertiserId = mockUpDTO.getBasicDetails().getAdvertiserId();
        for (CreativeFiles file : mockUpDTO.getUploadedFiles()) {
            CreativeFiles newFile = new CreativeFiles();
            BeanUtils.copyProperties(file, newFile);
            String permanentUrl = moveImage(file, advertiserId);
            newFile.setFilePath(permanentUrl);
            creativeFiles.add(newFile);
        }
        return creativeFiles;
    }

    /**
     * The temporary location is /atom/origin/cr_temp/image and from it is moved to
     * /atom/origin/advertiserId/heightXwidth/image location
     *
     * @param file - Create file with details
     * @param advertiserId - advertiser id for which creatives are getting created
     * @return - List of files with permanent location
     */
    private String moveImage(CreativeFiles file, Long advertiserId) {

        String previewUrl = file.getFilePath();
        String previewDirPostFix;

        if (StringUtils.isNotBlank(previewUrl)) {

            String source = previewUrl.replace(properties.getCreativeUrlPrependTemp(),
                    properties.getCreativeDirectoryPath());
            previewDirPostFix = advertiserId + "/" + file.getHeight() + "x" + file.getWidth() + "/";
            String destination = properties.getCreativeDirectoryPath() + properties.getCreativesDirectory() +
                    previewDirPostFix + file.getName();

            File sourceFile = new File(source);
            File destinationFile = new File(destination);

            try {
                FileUtils.copyFile(sourceFile, destinationFile);
            } catch (IOException e) {
                logger.debug("copying file to url path location directory got an Exception {}",
                        ExceptionUtils.getStackTrace(e));
            }

            return destinationFile.getPath().replace(properties.getCreativeDirectoryPath(),
                    properties.getCreativeUrlPrependTemp());
        }
        return null;
    }

    /**
     * Query the templates tables for the slots and sixes and return the unique values as list
     *
     * @return CreativeTemplatesMetadataDTO- POJO holding slots and sizes.
     */
    public ApiResponseObject<CreativeTemplatesMetadataDTO> getTemplatesMetadata(Boolean isDynamic, Boolean isActive) {
        CreativeTemplatesMetadataDTO metadataDTO = new CreativeTemplatesMetadataDTO();
        List<TemplateMetaData> metaData = templateRepo.findAllByDynamicAndActive(isDynamic, isActive);
        Set<Integer> slots = new HashSet<>();
        Set<String> sizes = new HashSet<>();
        metaData.forEach(c -> slots.add(c.getSlots()));
        metaData.forEach(c -> sizes.add(c.getSize()));
        metadataDTO.setTemplateSizes(new ArrayList<>(sizes));
        metadataDTO.setSlots(new ArrayList<>(slots));
        ApiResponseObject<CreativeTemplatesMetadataDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(metadataDTO);
        return responseObject;
    }
}
