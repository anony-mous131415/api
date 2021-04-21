/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.revx.api.common.MockDataGenerator;
import io.revx.core.model.*;
import io.revx.core.model.requests.SearchRequest;
import org.elasticsearch.common.recycler.Recycler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.pojo.TablesEntity;
import io.revx.core.aop.LogMetrics;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.MenuCrubResponse;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EntityESServiceTest extends BaseTestService {
  @Mock
  private CustomESRepositoryImpl customESRepositoryImpl;

  @Mock
  private LoginUserDetailsService loginUserDetailsService;

  @InjectMocks
  private EntityESService entityESService;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    entityESService.customESRepositoryImpl = customESRepositoryImpl;
  }


  /**
   * Test method for {@link io.revx.api.service.EntityESService#getMenuCrubResponse()}.
   */
  @Test
  @LogMetrics(name = "myJunit Test")
  public void testGetMenuCrubResponse() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      when(
          customESRepositoryImpl.searchAsList(entity.getElasticIndex(), es, StatusBaseObject.class))
              .thenReturn(TestDataGenerator.getListOfObject(10, entity.getElasticIndex(),
                  StatusBaseObject.class));
    }
    List<MenuCrubResponse> mRb = entityESService.getMenuCrubResponse();
    assertNotNull(mRb);
    assertThat(mRb.size()).isEqualTo(7);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.EntityESService#getDictionaryData(io.revx.api.pojo.TablesEntity, java.lang.Integer, java.lang.Integer)}.
   */
  @Test
  public void testGetDictionaryData() throws Exception {
    int pageNumber = 0;
    int pageSize = 10;
    List<DashboardFilters> filters = new ArrayList<>();
    filters.add(MockDataGenerator.getDashBoardFilters());
    String sort = "i";
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setFilters(filters);
    ElasticSearchTerm es = new ElasticSearchTerm();
    ElasticSearchTerm est = new ElasticSearchTerm();
    es.setFilters("licensee","76");
    es.setFilters("refactor","true");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    when(customESRepositoryImpl.searchByNameLike(pageNumber, pageSize, TablesEntity.ADVERTISER.getElasticIndex(), est
            , null, sort, Sort.Direction.ASC)).thenReturn(MockDataGenerator.getElasticResponse());
    DictionaryResponse dRb =
        entityESService.getDictionaryData(TablesEntity.ADVERTISER, 1, 10, searchRequest, "id");
    assertNotNull(dRb);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.EntityESService#searchByName(io.revx.api.pojo.TablesEntity, java.lang.String, java.util.List)}.
   */
  @Test
  public void testSearchByNameMenuAdvertise() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
          TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(entity.getElasticIndex(), es, "test"))
          .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
    }
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    // filters.add(new DashboardFilters("advertiserId", "1234"));
    MenuCrubResponse mcr = entityESService.searchByName(TablesEntity.ADVERTISER, "test", filters);
    assertNotNull(mcr);
    assertNotNull(mcr.getMenuList());
    assertThat(mcr.getMenuName()).isEqualTo(TablesEntity.ADVERTISER.getElasticIndex());

  }

  @Test
  public void testSearchByNameMenuSuccess() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
          TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(entity.getElasticIndex(), es, "test"))
          .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
    }
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("advertiserId", "1234"));
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    MenuCrubResponse mcr = entityESService.searchByName(TablesEntity.ADVERTISER, "test", filters);
    assertNotNull(mcr);
    assertNotNull(mcr.getMenuList());
    assertThat(mcr.getMenuName()).isEqualTo(TablesEntity.ADVERTISER.getElasticIndex());

  }

  /**
   * Test method for
   * {@link io.revx.api.service.EntityESService#searchById(io.revx.api.pojo.TablesEntity, long)}.
   */
  @Test
  public void testSearchById() throws Exception {
    for (TablesEntity entity : TablesEntity.values()) {
      BaseModelWithModifiedTime obj =
          new BaseModelWithModifiedTime(123L, "Test Name " + entity.getElasticIndex());
      List<BaseModelWithModifiedTime> listData = new ArrayList<BaseModelWithModifiedTime>();
      listData.add(obj);
      when(customESRepositoryImpl.findById(entity.getElasticIndex(), "123")).thenReturn(listData);
    }
    BaseModel mcr = entityESService.searchById(TablesEntity.ADVERTISER, 123L);
    assertNotNull(mcr);
    assertThat(mcr.getId()).isEqualTo(123);
  }

  @Test
  public void testSearchByIdNotFound() throws Exception {
    for (TablesEntity entity : TablesEntity.values()) {
      BaseModelWithModifiedTime obj =
          new BaseModelWithModifiedTime(123L, "Test Name " + entity.getElasticIndex());
      List<BaseModelWithModifiedTime> listData = new ArrayList<BaseModelWithModifiedTime>();
      listData.add(obj);
      when(customESRepositoryImpl.findById(entity.getElasticIndex(), "123")).thenReturn(listData);
    }
    BaseModel mcr = entityESService.searchById(TablesEntity.ADVERTISER, 12366L);
    assertNull(mcr);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.EntityESService#searchDetailById(io.revx.api.pojo.TablesEntity, long)}.
   */
  @Test
  public void testSearchDetailById() throws Exception {
    Licensee li = new Licensee(123, "LI-123");
    AdvertiserPojo adv = new AdvertiserPojo(234, "Adv-234");
    adv.setLicenseeId(li.getId());
    Campaign camp = new Campaign(345L, "Camp-345");
    camp.setAdvertiserId(adv.getId());
    Strategy strt = new Strategy(5678, "Stra-5678");
    strt.setCampaignId(camp.getId());
    when(customESRepositoryImpl.findDetailById(TablesEntity.LICENSEE.getElasticIndex(),
        String.valueOf(li.getId()), Licensee.class)).thenReturn(li);
    when(customESRepositoryImpl.findDetailById(TablesEntity.ADVERTISER.getElasticIndex(),
        String.valueOf(adv.getId()), AdvertiserPojo.class)).thenReturn(adv);
    when(customESRepositoryImpl.findDetailById(TablesEntity.CAMPAIGN.getElasticIndex(),
        String.valueOf(camp.getId()), Campaign.class)).thenReturn(camp);
    when(customESRepositoryImpl.findDetailById(TablesEntity.STRATEGY.getElasticIndex(),
        String.valueOf(strt.getId()), Strategy.class)).thenReturn(strt);
    ParentBasedObject mcr = entityESService.searchDetailById(TablesEntity.STRATEGY, strt.getId());
    assertNotNull(mcr);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.EntityESService#search(io.revx.api.pojo.TablesEntity, io.revx.api.es.entity.ElasticSearchTerm)}.
   */
  @Test
  public void testSearch() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    TablesEntity entity = TablesEntity.ADVERTISER;
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    Map<Long, Advertiser> idModelMap =
        TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), es, Advertiser.class))
        .thenReturn(idModelMap);
    Map<Long, ?> mRb = entityESService.search(TablesEntity.ADVERTISER, es);
    assertNotNull(mRb);
    assertThat(mRb).hasSize(10);
  }

}
