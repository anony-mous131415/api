package io.revx.auth;

import org.springframework.stereotype.Component;

// This IS A to DO class for verifying All the Annotation Classes will remove this
@Component
public class PrintingDataForTest {
/*
  private static Logger logger = LogManager.getLogger(PrintingDataForTest.class);
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  LicenseeUserRoleRepo licenseeUserRoleRepo;

  @Autowired
  LicenseeRepository licenseeRepository;

  @Autowired
  AdvertiserRepository advertiserRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  LoginLogoutService loginLogoutService;

  @Autowired
  AuthValidaterServiceImpl authValidaterServiceImpl;

  @Autowired
  LogoutCacheHolder logoutCacheHolder;

  @Autowired
  ApiErrorCodeResolver apiErrorCodeResolver;


  public void testAppProperties() {
    Object arr[] = {"replace-1", "replace-2"};
    for (int i = 0; i < 10; i++) {
      logger.info(apiErrorCodeResolver.resolveErrorCode(10000 + i, arr));
    }
  }

  public void print() {
    logger.info(" ************ Users  *********************");
    userRepository.findAll().forEach(user -> {
      logger.debug(" ******************** Printing All Details for :: " + user.getUsername()
          + " *************");
      UserInfoModel uim = (UserInfoModel) userDetailsService.loadUserByUsername(user.getUsername());
      logger.debug(uim);
      logger.debug(user);
    });

    logger.info(" ************ Advertiser  *********************");
    for (AdvertiserEntity ele : advertiserRepository.findByIsActive(true)) {
      logger.debug(ele);
    }
    logger.info(" ************ Licensee  *********************");
    for (LicenseeEntity ele : licenseeRepository.findByIsActive(true)) {
      logger.debug(ele);
    }

    logger.info(" ************ Partial Data  for ur_user_id =1*********************");
    for (LicenseeUserRolesEntity ele : licenseeUserRoleRepo.findByUserIdAndLicenseeIdAndAdvId(1,
        355, 7343)) {
      logger.debug(ele);
    }
  }

  public void testCache() {}

  public void testLogoutUser() {

    String username = "akhilesh";
    UserInfo uInfo = new UserInfo(username, new ArrayList<>());
    List<String> tokens = new ArrayList<String>();
    for (int i = 0; i < 4; i++) {
      tokens.add(jwtTokenProvider.generateAccessToken(uInfo));
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        logger.error(e);
      }
    }
    String randomToken = tokens.get(new Random().nextInt(tokens.size()));
    logger.info("Logout username : " + username);
    ApiResponseObject<Boolean> resp = loginLogoutService.logoutUser(randomToken, username);
    logger.info(resp);
    logger.info(resp.getRespObject());
    try {
      Thread.sleep(5000);
    } catch (Exception e) {
      // TODO: handle exception
    }
    // Validating All token After Logout Only One Should Logout Other Should not
    for (String tok : tokens) {
      logger.info("tok : " + tok);
      ApiResponseObject<UserInfo> respvalidate = null;
      try {
        respvalidate = authValidaterServiceImpl.validateToken(tok);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      logger.info(respvalidate);
      logger.info(respvalidate);
      logger.info(" error Object Should Not be null ", respvalidate.getError());
      logger.info(respvalidate.getRespObject());
    }

    try {
      Thread.sleep(5000);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
*/
}
