/**
 * 
 */
package io.revx.api.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.core.response.UserInfo;
import io.revx.querybuilder.enums.GroupBy;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ValidationServiceTest extends BaseTestService {
  @Mock
  private LoginUserDetailsService loginUserDetailsService;
  @InjectMocks
  private ValidationService validationService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    super.setUp();
    validationService.loginUserDetailsService = loginUserDetailsService;
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ValidationService#validateRequest(io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testValidateRequestDashBoardEntityDashboardRequest() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    validationService.validateRequest(DashBoardEntity.ADVERTISER, req);
    assertTrue(true);
  }

  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestFailed() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(null, req);
  }

  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestFailedWrongGroupBY()
      throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    req.setGroupBy("testMyGroup");
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(DashBoardEntity.ADVERTISER, req);
  }

  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestFailedWrongDuration()
      throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    req.setGroupBy(GroupBy.HOUR.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-20));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(DashBoardEntity.ADVERTISER, req);
  }

  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestFailedInvalidDuration()
      throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    req.setDuration(null);
    req.setGroupBy(GroupBy.HOUR.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);

    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(DashBoardEntity.ADVERTISER, req);
  }

  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestNUll() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(DashBoardEntity.ADVERTISER, null);
  }


  @Test
  public void testValidateRequestDashBoardEntityDashboardRequestFailedWrongFilter()
      throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    req.setGroupBy(GroupBy.HOUR.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("testMyFilter", "6427"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    exceptionRule.expect(ValidationException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_PARAMETER_IN_REQUEST.name());
    validationService.validateRequest(DashBoardEntity.ADVERTISER, req);
  }



  /**
   * Test method for
   * {@link io.revx.api.service.ValidationService#validateRequest(io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testValidateRequestDashboardRequest() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ValidationService#invalidFilter(io.revx.core.model.Licensee, java.util.Set, io.revx.querybuilder.enums.Filter, java.lang.String)}.
   */
  @Test
  public void testInvalidFilter() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for {@link io.revx.api.service.ValidationService#getFiltersMap(java.util.List)}.
   */
  @Test
  public void testGetFiltersMap() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ValidationService#getDashBoardFilterForData(java.util.Set)}.
   */
  @Test
  public void testGetDashBoardFilterForData() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

}
