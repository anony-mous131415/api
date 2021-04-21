package io.revx.api.service.audience.impl;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.amtdb.entity.RuleComponent;
import io.revx.api.mysql.amtdb.entity.SegmentPixelMap;
import io.revx.api.mysql.amtdb.entity.SegmentType;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.RuleComponentRepository;
import io.revx.api.mysql.amtdb.repo.SegmentPixelMapRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserSegmentMappingEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.api.service.audience.AudienceUtils;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.DurationUnit;
import io.revx.core.enums.Operator;
import io.revx.core.exception.ApiException;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.RuleComponentDTO;
import io.revx.core.model.audience.RuleDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AudienceUtilsTest extends BaseTestService {
    @Mock
    private DataPixelServiceImpl dataPixelService;

    @Mock
    private EntityESService elasticSearch;

    @Mock
    private CrmServiceImpl crmService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private RuleComponentRepository ruleComponentRepository;

    @Mock
    private SegmentPixelMapRepository segmentPixelMapRepository;

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private AudienceUtils audienceUtils;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        audienceUtils.setElasticSearch(elasticSearch);
        audienceUtils.setCrmService(crmService);
        audienceUtils.setApplicationProperties(applicationProperties);
        audienceUtils.setDataPixelService(dataPixelService);
        audienceUtils.setRuleComponentRepository(ruleComponentRepository);
        audienceUtils.setSegmentPixelMapRepository(segmentPixelMapRepository);
        audienceUtils.setLoginUserDetailsService(loginUserDetailsService);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#validateDTO(io.revx.core.model.audience.AudienceDTO, boolean)}.
     */
    @Test
    public void testValidateDTO() throws Exception{
        Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator
                .createAdvertiser());
        Mockito.when(loginUserDetailsService.isValidAdvertiser(Mockito.any())).thenReturn(true);
        Boolean response = audienceUtils.validateDTO(MockDataGenerator.createAudienceDTO(),true);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#getEntityFromDto(io.revx.core.model.audience.AudienceDTO)}.
     */
    @Test
    public void testGetEntityFromDto() throws Exception{
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setRemoteSegmentId("Test");
        audienceDTO.setSegmentType(SegmentType.DMP.id);
        Segments response = audienceUtils.getEntityFromDto(audienceDTO);
        assertNotNull(response);
    }

    @Test
    public void testGetEntityFromDtoSegmentType() throws Exception{
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setRemoteSegmentId("Test");
        audienceDTO.setSegmentType(SegmentType.PLATFORM.id);
        Segments response = audienceUtils.getEntityFromDto(audienceDTO);
        assertNotNull(response);
    }

    @Test
    public void testGetEntityFromDtoSegmentTypes() throws Exception{
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setRemoteSegmentId("Test");
        audienceDTO.setSegmentType(SegmentType.HASH_BUCKET.id);
        Segments response = audienceUtils.getEntityFromDto(audienceDTO);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#getMapEntityFromDto(io.revx.core.model.audience.AudienceDTO, java.lang.Long)}.
     */
    @Test
    public void testGetMapEntityFromDto() throws Exception{
        RuleDTO ruleDTO = MockDataGenerator.getRuleDTO();
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setRemoteSegmentId("Test");
        audienceDTO.setSegmentType(SegmentType.HASH_BUCKET.id);
        audienceDTO.setRuleExpression(ruleDTO);
        SegmentPixelMap response = audienceUtils.getMapEntityFromDto(audienceDTO,33L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#getEntityFromDto(io.revx.core.model.audience.AudienceDTO, java.lang.Long)}.
     */
    @Test
    public void testGetEntityFromDTO() throws Exception{
        RuleDTO ruleDTO = MockDataGenerator.getRuleDTO();
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
        audienceDTO.setName("test");
        audienceDTO.setDataSourceType(6);
        audienceDTO.setDuration(10L);
        audienceDTO.setDurationUnit(DurationUnit.MINUTE);
        audienceDTO.setRemoteSegmentId("Test");
        audienceDTO.setSegmentType(SegmentType.HASH_BUCKET.id);
        audienceDTO.setRuleExpression(ruleDTO);
        List<RuleComponent> response = audienceUtils.getEntityFromDto(audienceDTO,33L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#convertRules(io.revx.core.model.audience.RuleDTO, java.util.List)}.
     */
    @Test
    public void testConvertRules() throws Exception{
        List<RuleDTO> dtos = new ArrayList<>();
        AudienceDTO audienceDTO = new AudienceDTO();
        audienceDTO.setPixelId(3365L);
        audienceDTO.setDataSourceType(DataSourceType.AUDIENCE_FEED.id);
        dtos.add(MockDataGenerator.getRuleDTO());
        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.setSimpleExpr(false);
        ruleDTO.setNegate(true);
        ruleDTO.setOperator(Operator.AND);
        ruleDTO.setRuleExpressionList(dtos);
        List<RuleComponentDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponentDTO());
        audienceUtils.populateCrmDetails(audienceDTO);
        String response = audienceUtils.convertRules(ruleDTO,list);
        assertNotNull(response);
    }

    @Test
    public void testConvertRulesOperatorNull() throws Exception{
        List<RuleDTO> dtos = new ArrayList<>();
        dtos.add(MockDataGenerator.getRuleDTO());
        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.setSimpleExpr(false);
        ruleDTO.setNegate(true);
        ruleDTO.setRuleExpressionList(dtos);
        List<RuleComponentDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleComponentDTO());
        String response = audienceUtils.convertRules(ruleDTO,list);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.AudienceUtils#getEntity(Long, Long, String)}.
     */
    @Test
    public void testGetEntity() throws Exception{
        AdvertiserSegmentMappingEntity response = audienceUtils.getEntity(33L,34L,"Test");
        assertNotNull(response);
    }


}
