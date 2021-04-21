package io.revx.api.service.pixel.impl;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;
import io.revx.api.mysql.amtdb.repo.DataPixelsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.pixel.PixelUtils;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.pixel.PixelAdvDTO;
import io.revx.core.model.requests.SearchRequest;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class DataPixelServiceImplTest extends BaseTestService {
    @Mock
    DataPixelsRepository dataPixelRepository;

    @Mock
    PixelUtils pixelUtils;

    @Mock
    LoginUserDetailsService loginUserDetailsService;

    @Mock
    AdvertiserToPixelRepository advertiserToPixelRepository;

    @InjectMocks
    DataPixelServiceImpl dataPixelService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pixelUtils = new PixelUtils();
        dataPixelService.pixelUtils = pixelUtils;
        dataPixelService.advertiserToPixelRepository = advertiserToPixelRepository;
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.DataPixelServiceImpl#createDataPixel(io.revx.core.model.pixel.DataPixelDTO)}
     */
    @Test
    public void testSearchPixels() throws Exception {
        DataPixelDTO response = dataPixelService.createDataPixel(MockDataGenerator.getDataPixelDTO());
        assertNotNull(response);
        assertEquals("FILE_UPLOAD",response.getSourceType().toString());
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.DataPixelServiceImpl#saveDataPixel(io.revx.core.model.pixel.DataPixelDTO)}
     */
    @Test
    public void testSaveDataPixels() throws Exception {
        DataPixelDTO response = dataPixelService.saveDataPixel(MockDataGenerator.getDataPixelDTO());
        assertNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.pixel.impl.DataPixelServiceImpl#modifyDataPixelStatus(java.lang.Integer, io.revx.api.enums.Status)}
     */
    @Test
    public void testModifyDataPixelStatus() throws Exception {
        DataPixelDTO response = dataPixelService.modifyDataPixelStatus(33,Status.ACTIVE);
        assertNull(response);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getAllDataPixelsForLicensee()}
     */
    @Test
    public void testGetAllDataPixelsForLicensee() throws Exception{
        List<DataPixelDTO> response = dataPixelService.getAllDataPixelsForLicensee();
        assertNull(response);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getAllDataPixels()}
     */
    @Test
    public void testGetAllDataPixels() throws Exception{
        List<PixelAdvDTO> response  = dataPixelService.getAllDataPixels();
        assertNull(response);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getAllDataPixels(int, int)}
     */
    @Test
    public void testGetAllDataPixel() throws Exception{
        List<PixelAdvDTO>  response  = dataPixelService.getAllDataPixels(3,3);
        assertNull(response);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getAllDataPixelsForLicensee(int, int)}
     */
    @Test
    public void testGetAllDataPixelsForLicense() throws Exception{
        List<DataPixelDTO> response  = dataPixelService.getAllDataPixelsForLicensee(4,3);
        assertNull(response);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getDataPixel(Long)}
     */
    @Test
    public void testGetDataPixel() throws Exception {
        DataPixelsEntity e = new DataPixelsEntity();
        e.setDescription("test");
        e.setStatus(Status.ACTIVE);
        Optional<DataPixelsEntity> optional = Optional.of(e);
        Mockito.when(dataPixelRepository.findById(Mockito.anyLong())).thenReturn(optional);
        DataPixelDTO response  = dataPixelService.getDataPixel(33L);
        assertNotNull(response);
    }

    @Test
    public void testGetDataPixelNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        dataPixelService.getDataPixel(33L);
    }

    @Test
    public void testGetDataPixelIdNull() throws Exception {
        exceptionRule.expect(ApiException.class);
        dataPixelService.getDataPixel(null);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#createAdvertiserToPixel(BaseModel, DataSourceType)}
     */
    @Test
    public void testCreateAdvertiserToPixel() throws Exception {
        Mockito.when(advertiserToPixelRepository.findByStatusAndAdvertiserId(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.advertiserToPixelEntity());
        Long response = dataPixelService.createAdvertiserToPixel(MockDataGenerator.createBaseModel()
                ,DataSourceType.PIXEL_LOG);
        assertNotNull(response);
        assertEquals(435L,response.longValue());
        Long resp = dataPixelService.createAdvertiserToPixel(MockDataGenerator.createBaseModel()
                ,DataSourceType.AUDIENCE_FEED);
        assertNull(resp);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#createAndGetAdvertiserToPixel(BaseModel, DataSourceType)}
     */
    @Test
    public void testCreateAndGetAdvertiserToPixel() throws Exception {
        Mockito.when(advertiserToPixelRepository.findByStatusAndAdvertiserId(Mockito.any(),Mockito.anyLong()))
                .thenReturn(MockDataGenerator.advertiserToPixelEntity());
        AdvertiserToPixelEntity response = dataPixelService.createAndGetAdvertiserToPixel(MockDataGenerator.createBaseModel()
                ,DataSourceType.PIXEL_LOG);
        assertNotNull(response);
        assertEquals("ACTIVE",response.getStatus().toString());
        AdvertiserToPixelEntity resp = dataPixelService.createAndGetAdvertiserToPixel(MockDataGenerator.createBaseModel()
                ,DataSourceType.AUDIENCE_FEED);
        assertNotNull(resp);
    }

    /**
     * Test method for {@link DataPixelServiceImpl#getAdvertiserToPixel(Long)}
     */
    @Test
    public void testGetAdvertiserToPixel() throws Exception {
        Mockito.when(advertiserToPixelRepository.findByPixelId(Mockito.anyLong()))
                .thenReturn(MockDataGenerator.advertiserToPixelEntity());
        AdvertiserToPixelEntity response = dataPixelService.getAdvertiserToPixel(33L);
        assertNotNull(response);
    }
}
