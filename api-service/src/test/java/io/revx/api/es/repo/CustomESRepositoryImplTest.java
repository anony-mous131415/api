/**
 * 
 */
package io.revx.api.es.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.revx.api.common.MockDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.BaseModelWithModifiedTime;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.ElasticResponse;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomESRepositoryImplTest {
  @Mock
  private ElasticsearchTemplate elasticsearchTemplate;

  @InjectMocks
  private CustomESRepositoryImpl customESRepositoryImpl;
  
  @Mock
  private Client client;

  private static Logger logger = LogManager.getLogger(CustomESRepositoryImplTest.class);

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    customESRepositoryImpl.elasticsearchTemplate = elasticsearchTemplate;
  }

  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#findDetailById(java.lang.String, java.lang.String, java.lang.Class)}.
   */
  @Test
  public void testFindDetailById() throws Exception {
    long id = 12345;
    Advertiser adv = TestDataGenerator.getObject(Advertiser.class);
    adv.setId(id);
    adv.setName("Advertiser Name " + id);
    // adv.setCurrencyCode("INR");
    adv.setLicenseeId(33);
    List<Advertiser> list = new ArrayList<>();
    list.add(adv);
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    Advertiser adv1 =
        customESRepositoryImpl.findDetailById("test", String.valueOf(id), Advertiser.class);
    assertNotNull(adv1);
  }

  @Test
  public void testFindDetailByIdNoIds() throws Exception {
    List<AdvertiserPojo> list = new ArrayList<>();
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    AdvertiserPojo adv1 =
        customESRepositoryImpl.findDetailById("test", String.valueOf(1234), AdvertiserPojo.class);
    assertNull(adv1);
  }

  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#findById(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testFindById() throws Exception {
    List<BaseModelWithModifiedTime> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      BaseModelWithModifiedTime obj = TestDataGenerator.getObject(BaseModelWithModifiedTime.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setModifiedTime(System.currentTimeMillis() / 1000);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    List<BaseModelWithModifiedTime> timeBasedModel =
        customESRepositoryImpl.findById("test", "12345");
    assertNotNull(timeBasedModel);
    assertThat(docs).isEqualTo(timeBasedModel.size());

  }

  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#searchByNameLike(int, int, java.lang.String, io.revx.api.es.entity.ElasticSearchTerm)}.
   */
  @Test
  public void testSearchByNameLikeIntIntStringElasticSearchTerm() throws Exception {
    List<StatusBaseObject> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      StatusBaseObject obj = TestDataGenerator.getObject(StatusBaseObject.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setActive(true);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    ElasticSearchTerm searchTerm = new ElasticSearchTerm();
    searchTerm.setLicenseeId(123L);
    searchTerm.setAdvertisers(123L);
    ElasticResponse eResp = customESRepositoryImpl.searchByNameLike("city", searchTerm, "CityMaster");
    assertNotNull(eResp);
    assertThat(docs).isEqualTo(eResp.getData().size());
  }

  @Test
  public void testSearchByNameLikeElasticSearchTerm() throws Exception {
    List<StatusBaseObject> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      StatusBaseObject obj = TestDataGenerator.getObject(StatusBaseObject.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setActive(true);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    ElasticSearchTerm searchTerm = new ElasticSearchTerm();
    searchTerm.setLicenseeId(123l);
    searchTerm.setAdvertisers(123l);
    ElasticResponse eResp = customESRepositoryImpl.searchByNameLike(0, 10, "city", searchTerm);
    assertNotNull(eResp);
    assertThat(docs).isEqualTo(eResp.getData().size());
  }


  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#getAllStatusBasedEntity(java.lang.String)}.
   */
  @Test
  public void testGetAllStatusBasedEntityString() throws Exception {
    List<StatusBaseObject> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      StatusBaseObject obj = TestDataGenerator.getObject(StatusBaseObject.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setActive(true);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    ElasticSearchTerm searchTerm = new ElasticSearchTerm();
    searchTerm.setLicenseeId(123l);
    searchTerm.setAdvertisers(123l);
    searchTerm.setSerachInIdOrName("1236");
    List<StatusBaseObject> eResp = customESRepositoryImpl.getAllStatusBasedEntity("test");
    assertNotNull(eResp);
    assertThat(docs).isEqualTo(eResp.size());
  }


  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#searchAsList(java.lang.String, io.revx.api.es.entity.ElasticSearchTerm, java.lang.Class)}.
   */
  @Test
  public void testSearchAsList() throws Exception {
    List<StatusBaseObject> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      StatusBaseObject obj = TestDataGenerator.getObject(StatusBaseObject.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setActive(true);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    ElasticSearchTerm searchTerm = new ElasticSearchTerm();
    searchTerm.setLicenseeId(123l);
    searchTerm.setAdvertisers(123l);
    searchTerm.setSerachInIdOrName("test123");
    List<StatusBaseObject> eResp =
        customESRepositoryImpl.searchAsList("city", searchTerm, StatusBaseObject.class);
    assertNotNull(eResp);
    assertThat(docs).isEqualTo(eResp.size());
  }


  /**
   * Test method for
   * {@link io.revx.api.es.repo.CustomESRepositoryImpl#search(java.lang.String, io.revx.api.es.entity.ElasticSearchTerm, java.lang.Class)}.
   */
  @Test
  public void testSearch() throws Exception {
    List<Advertiser> list = new ArrayList<>();
    int docs = 10;
    for (int i = 0; i < docs; i++) {
      Advertiser obj = TestDataGenerator.getObject(Advertiser.class);
      obj.setId((long) (1000 + i));
      obj.setName("bmwmt - " + (1000 + i));
      obj.setActive(true);
      list.add(obj);
    }
    when(elasticsearchTemplate.query(Mockito.any(SearchQuery.class), Mockito.any()))
        .thenReturn(TestDataGenerator.getElasticSearchResponse(list));
    ElasticSearchTerm searchTerm = new ElasticSearchTerm();
    searchTerm.setLicenseeId(123L);
    searchTerm.setAdvertisers(123L);
    searchTerm.setCampaigns(1234L);
    searchTerm.setStrategies(234L);
    Map<Long, ?> idModelMap =
        customESRepositoryImpl.search("city", searchTerm, Advertiser.class);
    assertNotNull(idModelMap);
    assertThat(docs).isEqualTo(idModelMap.size());
  }


}
