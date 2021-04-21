package io.revx.auth.service;

import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.entity.LifeTimeAuthenticationEntity;
import io.revx.auth.repository.LifeTimeTokenRepository;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ValidationException;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ResponseMessage;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class LifeTimeAuthTokenServiceTest extends BaseTestService {

    @Mock
    public SecurityConstants securityConstants;

    @Mock
    LifeTimeTokenRepository lifeTimeTokenRepository;

    @Mock
    AuthValidaterServiceImpl authValidaterServiceImpl;

    @InjectMocks
    LifeTimeAuthTokenService lifeTimeAuthTokenService;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setup();
        MockitoAnnotations.initMocks(this);
        lifeTimeAuthTokenService.securityConstants = securityConstants;
        lifeTimeAuthTokenService.lifeTimeTokenRepository = lifeTimeTokenRepository;
        lifeTimeAuthTokenService.authValidaterServiceImpl = authValidaterServiceImpl;
    }

    @Test
    public void testGenerateLifeTimeAuthToken() throws Exception {
        UserInfo ui = TestDataGenerator.getObject(UserInfo.class);
        String authenticationHeaderValue = "FSHDBBDwe87wejsdhhhjhadFggs";

        Mockito.when(authValidaterServiceImpl.validateToken(authenticationHeaderValue)).thenReturn(ui);
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken(lifeTimeAuth);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setActive(true);
        lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthenticationEntity.setUserId(21971L);

        Mockito.when(lifeTimeTokenRepository.save(Mockito.any(LifeTimeAuthenticationEntity.class))).thenReturn(lifeTimeAuthenticationEntity);
        Mockito.when(securityConstants.getSIGNING_KEY()).thenReturn("jhasjh45sdySDs");

        LifeTimeAuthenticationEntity resp = lifeTimeAuthTokenService.generateLifeTimeAuthToken(authenticationHeaderValue);
        assertNotNull(resp);
        assertEquals(resp.getUserId(), 21971L);
    }

    @Test
    public void testGenerateLifeTimeAuthTokenValidateTokenFailed() throws Exception {
        UserInfo ui = TestDataGenerator.getObject(UserInfo.class);
        String authenticationHeaderValue = null;

        Mockito.when(authValidaterServiceImpl.validateToken(authenticationHeaderValue)).thenThrow(new Exception("invalid token"));
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken(lifeTimeAuth);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setActive(true);
        lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthenticationEntity.setUserId(21971L);

        Mockito.when(lifeTimeTokenRepository.save(Mockito.any(LifeTimeAuthenticationEntity.class))).thenReturn(lifeTimeAuthenticationEntity);
        Mockito.when(securityConstants.getSIGNING_KEY()).thenReturn("jhasjh45sdySDs");
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("invalid token");
        LifeTimeAuthenticationEntity resp = lifeTimeAuthTokenService.generateLifeTimeAuthToken(authenticationHeaderValue);
    }

    @Test
    public void testGetLifeTimeAuthToken() throws Exception {
        UserInfo ui = TestDataGenerator.getObject(UserInfo.class);
        String authenticationHeaderValue = "FSHDBBDwe87wejsdhhhjhadFggs";

        Mockito.when(authValidaterServiceImpl.validateToken(authenticationHeaderValue)).thenReturn(ui);
        List<LifeTimeAuthenticationEntity> tokenEntityList = TestDataGenerator.getListOfObject(10, LifeTimeAuthenticationEntity.class);
        Mockito.when(lifeTimeTokenRepository.findAllByLicenseeId(Mockito.anyLong())).thenReturn(tokenEntityList);

        ApiListResponse<LifeTimeAuthenticationEntity> resp = lifeTimeAuthTokenService.getLifeTimeAuthToken(authenticationHeaderValue);
        assertNotNull(resp);
        assertNotNull(resp.getData());
        assertEquals(resp.getData().size(), 10);
    }

    @Test
    public void testGetLifeTimeAuthTokenFailed() throws Exception {
        String authenticationHeaderValue = "FSHDBBDwe87wejsdhhhjhadFggs";

        Mockito.when(authValidaterServiceImpl.validateToken(authenticationHeaderValue)).thenThrow(new Exception("invalid token"));
        List<LifeTimeAuthenticationEntity> tokenEntityList = TestDataGenerator.getListOfObject(10, LifeTimeAuthenticationEntity.class);
        Mockito.when(lifeTimeTokenRepository.findAllByLicenseeId(Mockito.anyLong())).thenReturn(tokenEntityList);

        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("invalid token");
        ApiListResponse<LifeTimeAuthenticationEntity> resp = lifeTimeAuthTokenService.getLifeTimeAuthToken(authenticationHeaderValue);
    }

    @Test
    public void testDeleteLifeTimeAuthToken() throws Exception {
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken(lifeTimeAuth);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setActive(true);
        lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthenticationEntity.setUserId(21971L);
        lifeTimeAuthenticationEntity.setId(12L);

        Mockito.when(authValidaterServiceImpl.validateToken(Mockito.anyString())).thenReturn(TestDataGenerator.getObject(UserInfo.class));
        Mockito.when(lifeTimeTokenRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(lifeTimeTokenRepository.deActivate(Mockito.anyLong())).thenReturn(1);
        Mockito.when(lifeTimeTokenRepository.updateModifiedOn(Mockito.anyLong(), Mockito.anyLong())).thenReturn(1);

        ResponseMessage response = lifeTimeAuthTokenService.deleteLifeTimeAuthToken(12L, lifeTimeAuth);
        assertNotNull(response);
        assertEquals(response.getMessage(), Constants.MSG_SUCCESS);
    }

    @Test
    public void testDeleteLifeTimeAuthTokenAlreadyDeleted() throws Exception {
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken(lifeTimeAuth);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setActive(false);
        lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthenticationEntity.setUserId(21971L);
        lifeTimeAuthenticationEntity.setId(12L);

        Mockito.when(authValidaterServiceImpl.validateToken(Mockito.anyString())).thenReturn(TestDataGenerator.getObject(UserInfo.class));
        Mockito.when(lifeTimeTokenRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.of(lifeTimeAuthenticationEntity));
        Mockito.when(lifeTimeTokenRepository.deActivate(Mockito.anyLong())).thenReturn(1);
        Mockito.when(lifeTimeTokenRepository.updateModifiedOn(Mockito.anyLong(), Mockito.anyLong())).thenReturn(1);

        ResponseMessage response = lifeTimeAuthTokenService.deleteLifeTimeAuthToken(12L, lifeTimeAuth);
        assertNotNull(response);
        assertEquals(response.getMessage(), Constants.MSG_ID_ALREADY_INACTIVE);
        assertEquals(response.getCode(), Constants.ID_ALREADY_INACTIVE);
    }

    @Test
    public void testDeleteLifeTimeAuthTokenFailed() throws Exception {
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        Mockito.when(authValidaterServiceImpl.validateToken(Mockito.anyString())).thenReturn(TestDataGenerator.getObject(UserInfo.class));
        Mockito.when(lifeTimeTokenRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(lifeTimeTokenRepository.deActivate(Mockito.anyLong())).thenReturn(1);
        Mockito.when(lifeTimeTokenRepository.updateModifiedOn(Mockito.anyLong(), Mockito.anyLong())).thenReturn(1);

        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("INVALID_PARAMETER_IN_REQUEST");
        ResponseMessage response = lifeTimeAuthTokenService.deleteLifeTimeAuthToken(12L, lifeTimeAuth);
    }

    @Test
    public void testIsActiveLifeTimeToken() throws Exception {
        LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
        String lifeTimeAuth="AF45ASIj56DDHdrW09knLLMOP";
        lifeTimeAuthenticationEntity.setLifeTimeAuthToken(lifeTimeAuth);
        lifeTimeAuthenticationEntity.setLicenseeId(219L);
        lifeTimeAuthenticationEntity.setActive(true);
        lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
        lifeTimeAuthenticationEntity.setUserId(21971L);
        lifeTimeAuthenticationEntity.setId(12L);

        Mockito.when(lifeTimeTokenRepository.findByLifeTimeAuthToken(Mockito.anyString())).thenReturn(java.util.Optional.of(lifeTimeAuthenticationEntity));
        Boolean response = lifeTimeAuthTokenService.isActiveLifeTimeToken(lifeTimeAuth);
        assertNotNull(response);
        assertEquals(true, response.booleanValue());
    }

}