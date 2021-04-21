/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.mysql.repo.WhiteLablingRepository;
import io.revx.core.response.ApiResponseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WhiteLablingServiceTest {
  @Mock
  private WhiteLablingRepository whiteLablingRepository;
  @InjectMocks
  private WhiteLablingService whiteLablingService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    List<WhitelabelingEntity> entities = new ArrayList<>();
    WhitelabelingEntity entity = TestDataGenerator.getObject(WhitelabelingEntity.class);
    entities.add(entity);
    entity.setLicenseeId(33);
    entity.setSubDomain("affle");
    entity.setId(10);
    List<WhitelabelingEntity> defaultEntities = new ArrayList<>();
    WhitelabelingEntity defaultEntity = TestDataGenerator.getObject(WhitelabelingEntity.class);
    defaultEntities.add(defaultEntity);
    defaultEntity.setLicenseeId(2);
    defaultEntity.setSubDomain("default");
    defaultEntity.setId(1);
    when(whiteLablingRepository.findBySubDomain(Mockito.contains("affle"))).thenReturn(entities);
    when(whiteLablingRepository.findBySubDomain(Mockito.contains("default")))
        .thenReturn(defaultEntities);
    when(whiteLablingRepository.findByLicenseeId(Mockito.eq(1))).thenReturn(defaultEntities);
    when(whiteLablingRepository.findByLicenseeId(Mockito.eq(33))).thenReturn(entities);

  }

  /**
   * Test method for
   * {@link io.revx.api.service.WhiteLablingService#findBySubDomain(java.lang.String)}.
   */
  @Test
  public void testFindBySubDomain() throws Exception {
    ApiResponseObject<WhitelabelingEntity> resp = whiteLablingService.findBySubDomain("affle");
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertThat(resp.getRespObject().getLicenseeId()).isEqualTo(33);
  }

  @Test
  public void testFindBySubDomainNotMatch() throws Exception {
    ApiResponseObject<WhitelabelingEntity> resp = whiteLablingService.findBySubDomain("af3f34le");
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertThat(resp.getRespObject().getLicenseeId()).isEqualTo(2);
  }

  /**
   * Test method for {@link io.revx.api.service.WhiteLablingService#findByLicenseeId(int)}.
   */
  @Test
  public void testFindByLicenseeId() throws Exception {
    ApiResponseObject<WhitelabelingEntity> resp = whiteLablingService.findByLicenseeId(33);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertThat(resp.getRespObject().getLicenseeId()).isEqualTo(33);
  }

  @Test
  public void testFindByLicenseeIdNotMatch() throws Exception {
    ApiResponseObject<WhitelabelingEntity> resp = whiteLablingService.findByLicenseeId(3443);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertThat(resp.getRespObject().getLicenseeId()).isEqualTo(2);
  }

}
