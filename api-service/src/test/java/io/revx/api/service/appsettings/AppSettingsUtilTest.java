package io.revx.api.service.appsettings;

import io.revx.api.common.BaseTestService;
import io.revx.api.config.ApplicationProperties;
import io.revx.core.enums.AppSettingsKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class AppSettingsUtilTest extends BaseTestService {

    @InjectMocks
    private AppSettingsUtil settingsUtil;

    /**
     * Please check the following before running these tests
     * 1.Copy a PNG image file to location /atom/origin/cr_temp and rename it as API_Call.PNG
     * 2. Needs to modify the temporary file path if needed (currently considered the
     * application.properties files value for the directories)
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ApplicationProperties properties = new ApplicationProperties();
        properties.setCreativeUrlPrependTemp("http://origin.atomex.net/");
        properties.setCreativeDirectoryPath("/atom/origin/");
        settingsUtil = new AppSettingsUtil(properties);
    }

    @Test
    public void testCopyImageAndGenerateLogoLink() {
        String temporaryImageLink = "http://origin.atomex.net/cr_temp/API_Call.png";
        String finalImageUrl = settingsUtil
                .copyImageAndGenerateLink(temporaryImageLink, 7777L, AppSettingsKey.LOGO_LINK);
        assertNotNull(finalImageUrl);
        assertNotEquals(temporaryImageLink,finalImageUrl);
        assertEquals("http://origin.atomex.net/atomex-ui/static/adv-logos/7777/API",
                Arrays.stream(finalImageUrl.split("_")).findFirst().get());
        String imagePath = finalImageUrl.replace("http://origin.atomex.net","/atom/origin");
        assertTrue(Files.exists(Paths.get(imagePath)));
    }

    @Test
    public void testCopyImageAndGenerateDefaultLink() {
        String temporaryImageLink = "http://origin.atomex.net/cr_temp/API_Call.png";
        String finalImageUrl = settingsUtil
                .copyImageAndGenerateLink(temporaryImageLink, null, AppSettingsKey.DEFAULT_LOGO);
        assertNotNull(finalImageUrl);
        assertNotEquals(temporaryImageLink,finalImageUrl);
        assertEquals("http://origin.atomex.net/atomex-ui/static/adv-default-logos/API",
                Arrays.stream(finalImageUrl.split("_")).findFirst().get());
        String imagePath = finalImageUrl.replace("http://origin.atomex.net","/atom/origin");
        assertTrue(Files.exists(Paths.get(imagePath)));
    }

    @Test
    public void testCopyImageAndGenerateFallbackImageLink() {
        String temporaryImageLink = "http://origin.atomex.net/cr_temp/API_Call.png";
        String finalImageUrl = settingsUtil
                .copyImageAndGenerateLink(temporaryImageLink, 7777L, AppSettingsKey.FALLBACK_IMG_LINK);
        assertNotNull(finalImageUrl);
        assertNotEquals(temporaryImageLink,finalImageUrl);
        assertEquals("http://origin.atomex.net/atomex-ui/static/adv-fallbacks/7777/API",
                Arrays.stream(finalImageUrl.split("_")).findFirst().get());
        String imagePath = finalImageUrl.replace("http://origin.atomex.net","/atom/origin");
        assertTrue(Files.exists(Paths.get(imagePath)));
    }

    @Test
    public void testCopyImageAndGenerateOverlayImageLink() {
        String temporaryImageLink = "http://origin.atomex.net/cr_temp/API_Call.png";
        String finalImageUrl = settingsUtil
                .copyImageAndGenerateLink(temporaryImageLink, 7777L, AppSettingsKey.OVERLAY_IMG_LINK);
        assertNotNull(finalImageUrl);
        assertNotEquals(temporaryImageLink,finalImageUrl);
        assertEquals("http://origin.atomex.net/atomex-ui/static/adv-overlays/7777/API",
                Arrays.stream(finalImageUrl.split("_")).findFirst().get());
        String imagePath = finalImageUrl.replace("http://origin.atomex.net","/atom/origin");
        assertTrue(Files.exists(Paths.get(imagePath)));
    }

    @Test
    public void testCopyImageAndGenerateLinkException() {
        String temporaryImageLink = "http://origin.atomex.net/API_Call.png";
        String finalImageUrl = settingsUtil
                .copyImageAndGenerateLink(temporaryImageLink, 7777L, AppSettingsKey.OVERLAY_IMG_LINK);
        assertNotNull(finalImageUrl);
        assertNotEquals(temporaryImageLink,finalImageUrl);
        assertEquals("http://origin.atomex.net/atomex-ui/static/adv-overlays/7777/API",
                Arrays.stream(finalImageUrl.split("_")).findFirst().get());
        String imagePath = finalImageUrl.replace("http://origin.atomex.net","/atom/origin");
        assertTrue(Files.exists(Paths.get(imagePath)));
    }

}
