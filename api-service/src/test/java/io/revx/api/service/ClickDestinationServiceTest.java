package io.revx.api.service;

import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.api.service.clickdestination.ClickDestinationService;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.response.ApiResponseObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClickDestinationServiceTest {

    @Mock
    private EntityESService entityESService;

    @Mock
    private AdvertiserService advertiserService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ClickDestinationService clickDestinationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMmpParametersOnElasticFail() throws Exception {
        ClickDestination clickDestination = new ClickDestination();
        clickDestination.setAdvertiserId(1234L);
        AdvertiserSettings advertiserSettings = new AdvertiserSettings();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(6L);
        baseModel.setName("Test MMP");
        advertiserSettings.setMmp(baseModel);
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(advertiserSettings);
        when(advertiserService.getSettingsById(clickDestination.getAdvertiserId()))
                .thenReturn(responseObject);
        when(entityESService.searchPojoById(TablesEntity.MMP,1234L)).thenReturn(null);
//        ApiResponseObject<ClickDestination> dest = clickDestinationService.getMmpParameters(clickDestination);
//        ClickDestination response = dest.getRespObject();
//        Assert.assertNull(response);
//        Assert.assertNotNull(dest.getError());
    }

    @Test
    public void testMmpParametersOnAdvertiserServiceFail() throws Exception {
        ClickDestination clickDestination = new ClickDestination();
        clickDestination.setAdvertiserId(1234L);
        AdvertiserSettings advertiserSettings = new AdvertiserSettings();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(6L);
        baseModel.setName("Test MMP");
        advertiserSettings.setMmp(baseModel);
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(null);
        when(advertiserService.getSettingsById(clickDestination.getAdvertiserId()))
                .thenReturn(responseObject);
        when(entityESService.searchPojoById(TablesEntity.MMP,1234L)).thenReturn(null);
        //ApiResponseObject<ClickDestination> dest = clickDestinationService.getMmpParameters(clickDestination);
        //ClickDestination response = dest.getRespObject();
        //Assert.assertNull(response);
        //Assert.assertNotNull(dest.getError());
    }

    @Test
    public void testMmpParametersOnMmmpMissing() throws Exception {
        ClickDestination clickDestination = new ClickDestination();
        clickDestination.setAdvertiserId(1234L);
        AdvertiserSettings advertiserSettings = new AdvertiserSettings();
        advertiserSettings.setMmp(null);
        ApiResponseObject<AdvertiserSettings> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(advertiserSettings);
        when(advertiserService.getSettingsById(clickDestination.getAdvertiserId()))
                .thenReturn(responseObject);
        when(entityESService.searchPojoById(TablesEntity.MMP,1234L)).thenReturn(null);
        //ApiResponseObject<ClickDestination> dest = clickDestinationService.getMmpParameters(clickDestination);
        //ClickDestination response = dest.getRespObject();
        //Assert.assertNull(response);
        //Assert.assertNotNull(dest.getError());
    }
}
