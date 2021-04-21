package io.revx.api.service.appsettings;

import io.revx.api.config.ApplicationProperties;
import io.revx.core.enums.AppSettingsKey;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static io.revx.core.constant.Constants.DIR_SEPARATOR;
import static io.revx.core.constant.Constants.HYPHEN;

@Component
public class AppSettingsUtil {

    private static final Logger logger = LogManager.getLogger(AppSettingsUtil.class);

    private final ApplicationProperties properties;

    public AppSettingsUtil(ApplicationProperties properties) {
        this.properties = properties;
    }

    /**
     * The link is generated by the copying the file from the temp location to
     * a permnanet location based on the advertiserID and LOGO TYPE and an link
     * is generated based on the file path.
     *
     * @param previewUrl - The URL pointing to the temp location of teh file
     * @param advertiserId (optional)
     * @param settingsKey LOGO TYPE - OVERLAY_IMAGE, FALLBACK_IMAGE etc
     * @return - The url pointing to the permanent location
     */
    public String copyImageAndGenerateLink(String previewUrl, Long advertiserId, AppSettingsKey settingsKey) {

        String sourcePath = previewUrl.replace(properties.getCreativeUrlPrependTemp(),
                properties.getCreativeDirectoryPath());
        StringBuilder destinationDirectory = generateDirectoryPath(advertiserId, settingsKey);
        String permanentFilePath = destinationDirectory+generateFileName(sourcePath);

        try {
            Files.createDirectories(Paths.get(destinationDirectory.toString()));
            Files.copy(Paths.get(sourcePath),Paths.get(permanentFilePath));
        } catch (IOException e) {
            logger.debug("copying file to url path location directory got an Exception {}",
                    ExceptionUtils.getStackTrace(e));
        }

        return permanentFilePath.replace(properties.getCreativeDirectoryPath()
                ,properties.getLogoUrlPrependTemp());
    }

    /**
     * A new unique identifier is added to the uploaded file name for unqiue name value.
     * because if a file is uploaded with the same name as previous existing file it
     * should not be overridden wit the new resulting in loss of prvious file
     *
     * @param sourcePath - The temporary file location /atom/origin/cr_temp/*******
     * @return - unique file name
     */
    private String generateFileName(String sourcePath) {
        String[] filePath = sourcePath.split(DIR_SEPARATOR);
        int filePartsLength = filePath.length;
        String fileName = filePath[filePartsLength-1];
        String[] fileParts = fileName.split("\\.");
        String extension = fileParts[1];
        StringBuilder uniqueFileName = new StringBuilder(fileParts[0]).append(HYPHEN)
                .append(UUID.randomUUID().toString().split("-")[0]).append(".").append(extension);
        return uniqueFileName.toString();
    }

    /**
     * The directory path is generated based on the advertiserID and the LOGO TYPE
     *
     * @param advertiserId (optional)
     * @param settingsKey Like LOGO_LINK, DEFAULT_IMAGE_LINK
     * @return - directory path to which the logos should be saved
     */
    private StringBuilder generateDirectoryPath(Long advertiserId, AppSettingsKey settingsKey ) {
        StringBuilder destinationPath = new StringBuilder();
        destinationPath.append(properties.getCreativeDirectoryPath())
                .append("atomex-ui/static/");

        switch (settingsKey) {
            case LOGO_LINK:
                destinationPath.append("adv-logos/");
                break;
            case FALLBACK_IMG_LINK:
                destinationPath.append("adv-fallbacks/");
                break;
            case OVERLAY_IMG_LINK:
                destinationPath.append("adv-overlays/");
                break;
            case DEFAULT_LOGO:
                destinationPath.append("adv-default-logos/");
                break;
        }

        if (advertiserId != null) {
            destinationPath.append(advertiserId).append(DIR_SEPARATOR);
        }

        return destinationPath;
    }
}
