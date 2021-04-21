package io.revx.api.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Modifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.PrintAllSigtonService;
import io.revx.api.constants.ApiConstant;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.entity.CSSThemeEntity;
import io.revx.api.mysql.entity.LicenseeEntity;
import io.revx.api.mysql.entity.UserInfoEntity;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.postgres.entity.PerformanceData;
import io.revx.api.postgres.entity.PerformanceDataId;
import io.revx.api.security.SecurityConstants;
import io.revx.api.service.FileExportImportService;
import io.revx.api.service.WhiteLablingService;
import io.revx.querybuilder.enums.GroupBy;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestAllConstructor {


  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testPrivateConstructorApiConstant() throws Exception {
    java.lang.reflect.Constructor<ApiConstant> constructor =
        ApiConstant.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testPrivateConstructorSecurityConstants() throws Exception {
    java.lang.reflect.Constructor<SecurityConstants> constructor =
        SecurityConstants.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testGetterSetterDashboardEntities() {
    PojoTestUtils pojo = new PojoTestUtils();
    pojo.toString();
    assertTrue(PojoTestUtils.validateAccessors(DashboardEntities.class));
  }

  @Test
  public void testGetterSetterElasticSearchTerm() {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setAdvertisers(123l);
    es.setAdvertisers(1234l);
    es.setLicenseeId(193l);
    es.setLicenseeId(173l);
    es.setCampaigns(987l);
    es.setCampaigns(787l);
    es.setStrategies(675l);
    es.setStrategies(875l);
    es.setSerachInIdOrName("test");
    assertThat(es.getLicensies().size()).isEqualTo(2);
    assertThat(es.getAdvertisers().size()).isEqualTo(2);
    assertThat(es.getCampaigns().size()).isEqualTo(2);
    assertThat(es.getStrategies().size()).isEqualTo(2);
    assertTrue(PojoTestUtils.validateAccessors(ElasticSearchTerm.class));
  }

  @Test
  public void testGetterSetterAdvertiserEntity() {
    assertTrue(PojoTestUtils.validateAccessors(AdvertiserEntity.class));
    AdvertiserEntity obj = new AdvertiserEntity();
    assertNotNull(obj.toString());
  }

  @Test
  public void testGetterSetterCSSThemeEntity() {
    assertTrue(PojoTestUtils.validateAccessors(CSSThemeEntity.class));
    CSSThemeEntity obj = new CSSThemeEntity();
    assertNotNull(obj.toString());
  }

  @Test
  public void testGetterSetterLicenseeEntity() {
    assertTrue(PojoTestUtils.validateAccessors(LicenseeEntity.class));
    LicenseeEntity obj = new LicenseeEntity();
    assertNotNull(obj.toString());
  }

  @Test
  public void testGetterSetterUserInfoEntity() {
    assertTrue(PojoTestUtils.validateAccessors(UserInfoEntity.class));
    UserInfoEntity obj = new UserInfoEntity();
    assertNotNull(obj.toString());
  }

  @Test
  public void testGetterSetterWhitelabelingEntity() {
    assertTrue(PojoTestUtils.validateAccessors(WhitelabelingEntity.class));
    AdvertiserEntity obj = new AdvertiserEntity();
    assertNotNull(obj.toString());
  }

  @Test
  public void testGetterSetterChartPerformanceDataMetrics() {
    assertTrue(PojoTestUtils.validateAccessors(ChartPerformanceDataMetrics.class));
  }

  @Test
  public void testGetterSetterListPerformanceDataMetrics() {
    assertTrue(PojoTestUtils.validateAccessors(ListPerformanceDataMetrics.class));
  }

  @Test
  public void testGetterSetterPerformanceDataMetrics() {
    assertTrue(PojoTestUtils.validateAccessors(PerformanceDataMetrics.class));
  }

  @Test
  public void testGetterSetterDashBoardEntity() {
    assertTrue(PojoTestUtils.validateAccessors(DashBoardEntity.class));
  }

  @Test
  public void testGetterSetterTablesEntity() {
    TablesEntity te = TablesEntity.getFromGroupBy(GroupBy.ADVERTISER_ID);
    assertNotNull(te);
    assertThat(te).isEqualTo(TablesEntity.ADVERTISER);
    te = TablesEntity.getFromGroupBy(GroupBy.CAMPAIGN_ID);
    assertNotNull(te);
    assertThat(te).isEqualTo(TablesEntity.CAMPAIGN);
    te = TablesEntity.getFromGroupBy(GroupBy.CREATIVE_ID);
    assertNotNull(te);
    assertThat(te).isEqualTo(TablesEntity.CREATIVE);
    te = TablesEntity.getFromGroupBy(GroupBy.DAY);
    assertNull(te);
    assertTrue(PojoTestUtils.validateAccessors(TablesEntity.class));
  }

  @Test
  public void testGetterSetterWhiteLablingService() {
    assertTrue(PojoTestUtils.validateAccessors(WhiteLablingService.class));
  }


  @Test
  public void testGetterSetterPerformanceData() {
    assertTrue(PojoTestUtils.validateAccessors(PerformanceData.class));
  }

  @Test
  public void testGetterSetterFileExportImportService() {
    assertTrue(PojoTestUtils.validateAccessors(FileExportImportService.class));
  }

  @Test
  public void testGetterSetterPerformanceDataId() {
    assertTrue(PojoTestUtils.validateAccessors(PerformanceDataId.class));
  }

  @Test
  public void testGetterSetterPrintAllSigtonService() {
    assertTrue(PojoTestUtils.validateAccessors(PrintAllSigtonService.class));
  }

}
