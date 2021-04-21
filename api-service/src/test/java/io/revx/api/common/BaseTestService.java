package io.revx.api.common;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import io.revx.api.mysql.entity.LicenseeEntity;
import io.revx.api.mysql.entity.UserInfoEntity;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.repo.LicenseeRepository;
import io.revx.api.mysql.repo.UserRepository;
import io.revx.api.mysql.repo.WhiteLablingRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.core.response.UserInfo;

public class BaseTestService {

  @Mock
  protected AdvertiserRepository advertiserRepository;

  @Mock
  protected LicenseeRepository licenseeRepository;

  @Mock
  protected UserRepository userRepository;

  @Mock
  protected WhiteLablingRepository whiteLablingRepository;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();


  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  public void mockMysqlRepo() {
    List<AdvertiserEntity> advList = new ArrayList<AdvertiserEntity>();
    for (Long i = 0l; i < 10; i++) {
      AdvertiserEntity entity = new AdvertiserEntity();
      entity.setId((i + 1) * 10);
      entity.setAdvertiserName("Adv " + (i + 1) * 10);
      entity.setIsActive(true);
      advList.add(entity);
    }

    List<LicenseeEntity> liList = new ArrayList<LicenseeEntity>();
    for (int i = 0; i < 10; i++) {
      LicenseeEntity entity = new LicenseeEntity();
      entity.setId((i + 1) * 10);
      entity.setLicenseeName("Licensee " + (i + 1) * 10);
      entity.setActive(true);
      liList.add(entity);
    }
    UserInfoEntity uEntity = TestDataGenerator.getUserEntityObject("akhilesh");
    List<WhitelabelingEntity> entities = new ArrayList<>();
    WhitelabelingEntity wentity = TestDataGenerator.getObject(WhitelabelingEntity.class);
    entities.add(wentity);
    when(licenseeRepository.findByIsActive(true)).thenReturn(liList);
    when(advertiserRepository.findByIsActive(true)).thenReturn(advList);
    when(advertiserRepository.findAll()).thenReturn(advList);
    when(userRepository.findByUsername("akhilesh")).thenReturn(uEntity);
    when(whiteLablingRepository.findBySubDomain("revx")).thenReturn(entities);
    when(whiteLablingRepository.findByLicenseeId(33)).thenReturn(entities);
  }


  protected void mockSecurityContext(String userName, String role, long selectedLicenseeId,
      boolean isAdvAccess, boolean isLicenseeAccess) {
    UserInfo ui = TestDataGenerator.getUserInfo(userName, role, selectedLicenseeId, isAdvAccess,
        isLicenseeAccess);
    UsernamePasswordAuthenticationToken upa =
        new UsernamePasswordAuthenticationToken(ui, "", getAuthority(ui.getAuthorities()));
    SecurityContextHolder.getContext().setAuthentication(upa);
  }

  protected void mockSecurityContext(String userName, boolean isAdvAccess,
      boolean isLicenseeAccess) {
    mockSecurityContext(userName, "ROLE_RW", 33, isAdvAccess, isLicenseeAccess);
  }

  public Collection<? extends GrantedAuthority> getAuthority(Set<String> roles) {
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    for (String role : roles) {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    return authorities;
  }

}
