package io.revx.api.service.audience.impl;

import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.audience.AudienceServiceMockImpl;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.PixelRemoteConfigDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import io.revx.core.service.CacheService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AudienceServiceMockImplTest extends BaseTestService {
    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private CacheService cacheService;

    @Mock
    private CustomESRepositoryImpl elastic;

    @Mock
    private EntityESService elasticSearch;

    @InjectMocks
    private AudienceServiceMockImpl audienceServiceMock;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceServiceMockImpl#createAudience(io.revx.core.model.audience.AudienceDTO)}.
     */
    @Test
    public void testCreateAudience() throws Exception{
        ApiResponseObject<AudienceDTO> response = audienceServiceMock.createAudience(MockDataGenerator
                .createAudienceDTO());
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceServiceMockImpl#getAudience(java.lang.Long)}.
     */
    @Test
    public void testGetAudience() throws Exception{
        ApiResponseObject<AudienceDTO> response = audienceServiceMock.getAudience(33L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceServiceMockImpl#updateAudience(java.lang.Long, io.revx.core.model.audience.AudienceDTO)}.
     */
    @Test
    public void testUpdateAudience() throws Exception{
        ApiResponseObject<AudienceDTO> response = audienceServiceMock.updateAudience(33L,MockDataGenerator.createAudienceDTO());
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#syncPlatformAudience()}.
     */
    @Test
    public void testSyncPlatformAudience() throws Exception{
        ApiResponseObject<BaseModel> response = audienceServiceMock.syncPlatformAudience();
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#syncRemoteAudience(java.lang.Integer)}.
     */
    @Test
    public void testSyncRemoteAudience() throws Exception{
        ApiResponseObject<BaseModel> response = audienceServiceMock.syncRemoteAudience(76);
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#checkConnection(io.revx.core.model.audience.PixelRemoteConfigDTO)}.
     */
    @Test
    public void testCheckConnection() throws Exception{
        PixelRemoteConfigDTO dto = new PixelRemoteConfigDTO();
        dto.setPassword("password");
        dto.setProtocol(6);
        dto.setUrl("hhtp://www.komli.com");
        dto.setUsername("Honda");
        ApiResponseObject<BaseModel> response = audienceServiceMock.checkConnection(dto);
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#activate(java.lang.String)}.
     */
    @Test
    public void testActivate() throws Exception{
        ApiResponseObject<Map<Integer, ResponseMessage>> response = audienceServiceMock.activate("234");
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#deactivate(java.lang.String)}.
     */
    @Test
    public void testDeactivate() throws Exception{
        ApiResponseObject<Map<Integer, ResponseMessage>> response = audienceServiceMock.deactivate("234");
        assertNotNull(response);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#getDmpAudience(Long, Integer, Integer, Integer)}.
     */
    @Test
    public void testGetDmpAudience() throws Exception{
        ApiResponseObject<DmpAudienceDTO> response = audienceServiceMock.getDmpAudience(32L,null,null,3);
        assertNotNull(response);
        ApiResponseObject<DmpAudienceDTO> responses = audienceServiceMock.getDmpAudience(33L,null,null,3);
        assertNotNull(responses);
    }

    /**
     * Test method for {@link AudienceServiceMockImpl#getAcces(java.lang.Long)}.
     */
    @Test
    public void testGetAccess() throws Exception{
        ApiResponseObject<AudienceAccessDTO> response = audienceServiceMock.getAcces(33L);
        assertNotNull(response);
        ApiResponseObject<AudienceAccessDTO> responses = audienceServiceMock.getAcces(32L);
        assertNotNull(responses);
    }
}
