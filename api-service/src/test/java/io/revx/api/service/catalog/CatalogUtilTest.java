package io.revx.api.service.catalog;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity;
import io.revx.api.mysql.dco.entity.catalog.AtomCatalogVariablesEntity;
import io.revx.api.mysql.dco.entity.catalog.FeedApiStatusEntity;
import io.revx.api.mysql.dco.repo.catalog.*;
import io.revx.api.service.ModelConverterService;
import io.revx.core.enums.Operator;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CatalogUtilTest extends BaseTestService {
    @Mock
    FeedInfoRepository feedInfoRepo;

    @Mock
    AdvertiserCatalogVariableMappingRepository acvmRepo;

    @Mock
    AtomCatalogVariableRepository acvRepo;

    @Mock
    CatalogItemRepository ciRepo;

    @Mock
    FeedInfoStatsRepository fisRepo;

    @Mock
    FeedApiStatsRepository fasRepo;

    @Mock
    ModelConverterService converter;

    @InjectMocks
    CatalogUtil catalogUtil;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        catalogUtil.feedInfoRepo = feedInfoRepo;
        catalogUtil.acvmRepo = acvmRepo;
        catalogUtil.converter = converter;
        catalogUtil.ciRepo = ciRepo;
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogUtil#getMacroDTOListForAdvertiser(java.lang.Long)}.
     */
    @Test
    public void testGetMacroDTOListForAdvertiser() throws Exception {
        List<AtomCatalogVariablesEntity> variablesEntities = new ArrayList<>();
        variablesEntities.add(MockDataGenerator.getAtomCatalogVariablesEntity());
        List<Long> list = new ArrayList<>();
        list.add(5L);
        Mockito.when(feedInfoRepo.findIdByAdvertiserid(Mockito.anyLong())).thenReturn(list);
        Mockito.when(acvmRepo.findAtomVariableInFeedIds(Mockito.any())).thenReturn(list);
        Mockito.when(acvRepo.findAllByIdIn(Mockito.any())).thenReturn(variablesEntities);
        List<Macro> response = catalogUtil.getMacroDTOListForAdvertiser(2325L);
        assertNotNull(response);
        assertEquals("SafariTest",response.get(0).getName());
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogUtil#getVariableMapping(Long, Long)}.
     */
    @Test
    public void testGetVariableMapping() throws Exception {
        Optional<AtomCatalogVariablesEntity> optional = Optional.of(MockDataGenerator.getAtomCatalogVariablesEntity());
        AdvertiserCatalogVariablesMappingEntity entity = new AdvertiserCatalogVariablesMappingEntity();
        entity.setFeedId(6L);
        entity.setIsMultivalued(true);
        entity.setSelectPosition(1L);
        entity.setAtomVariable(73L);
        List<AdvertiserCatalogVariablesMappingEntity> entityList = new ArrayList<>();
        entityList.add(entity);
        Mockito.when(acvmRepo.findAllByFeedId(Mockito.anyLong())).thenReturn(entityList);
        Mockito.when(acvRepo.findById(Mockito.anyLong())).thenReturn(optional);
        Mockito.when(converter.convertAcvmToDTO(Mockito.any(),Mockito.any()))
                .thenReturn(MockDataGenerator.setVariablesMappingDTO());
        List<VariablesMappingDTO> response  = catalogUtil.getVariableMapping(342L,438L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogUtil#getSuccessRateForFeedInfoStats(Long, Long)}.
     */
    @Test
    public void testGetSuccessRateForFeedInfoStats() throws Exception{
        FeedApiStatusEntity feedApiStatusEntity = new FeedApiStatusEntity();
        feedApiStatusEntity.setFasFeedStatus(6);
        feedApiStatusEntity.setFeedId(76L);
        feedApiStatusEntity.setFasId(5L);
        feedApiStatusEntity.setFasProductInserted(45L);
        feedApiStatusEntity.setFasProductDelete(54L);
        feedApiStatusEntity.setFasProductUpdated(22L);
        feedApiStatusEntity.setFasProductTotal(99L);
        List<FeedApiStatusEntity> entityList = new ArrayList<>();
        entityList.add(feedApiStatusEntity);
        Mockito.when(fasRepo.findAllByOrderByFasCreatedTimeDesc(Mockito.anyLong())).thenReturn(entityList);
        Long response = catalogUtil.getSuccessRateForFeedInfoStats(33L,663L);
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.catalog.CatalogUtil#getSuccessRateForFeedApiStatus(java.lang.Long)}.
     */
    @Test
    public void testGetSuccessRateForFeedApiStatus() throws Exception {
        FeedApiStatusEntity feedApiStatusEntity = new FeedApiStatusEntity();
        feedApiStatusEntity.setFasFeedStatus(6);
        feedApiStatusEntity.setFeedId(76L);
        feedApiStatusEntity.setFasId(5L);
        feedApiStatusEntity.setFasProductInserted(45L);
        feedApiStatusEntity.setFasProductDelete(54L);
        feedApiStatusEntity.setFasProductUpdated(22L);
        feedApiStatusEntity.setFasProductTotal(99L);
        List<FeedApiStatusEntity> entityList = new ArrayList<>();
        entityList.add(feedApiStatusEntity);
        Mockito.when(fasRepo.findAllByOrderByFasCreatedTimeDesc(Mockito.anyLong())).thenReturn(entityList);
        Long response = catalogUtil.getSuccessRateForFeedApiStatus(76L);
        assertNotNull(response);
    }

}
