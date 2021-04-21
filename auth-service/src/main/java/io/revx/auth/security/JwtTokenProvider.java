package io.revx.auth.security;


import static io.revx.auth.constants.SecurityConstants.AUTHORITIES_KEY;
import static io.revx.auth.constants.SecurityConstants.AUTH_HEADER;
import static io.revx.auth.constants.SecurityConstants.IS_LIFETIME_AUTH;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.revx.auth.service.LifeTimeAuthTokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.utils.LogoutCacheHolder;
import io.revx.auth.utils.UserUtils;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.response.UserInfo;

@Component
public class JwtTokenProvider {

  @Autowired
  public LogoutCacheHolder logoutCacheHolder;

  @Autowired
  public SecurityConstants securityConstants;

  @Autowired
  public LifeTimeAuthTokenService lifeTimeAuthTokenService;

  private static Logger logger = LogManager.getLogger(JwtTokenProvider.class);

  public String getUsernameFromToken(String token) {

    return getClaimFromToken(token, Claims::getSubject);

  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  @LogMetrics(name = GraphiteConstants.JWT + GraphiteConstants.DE_SER + ".master")
  public UserInfoMasterPojo getUserInfoFromMasterToken(String token) {
    return UserInfoMasterPojo.deSerializeUserInfoModel(getClaimFromToken(token, Claims::getIssuer));
  }

  @LogMetrics(name = GraphiteConstants.JWT + GraphiteConstants.DE_SER + ".access")
  public UserInfo getUserFromAccessToken(String token) {
    return UserInfo.deSerializeUser(getClaimFromToken(token, Claims::getIssuer));
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(securityConstants.getSIGNING_KEY()).parseClaimsJws(token)
        .getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateMasterToken(Authentication authentication) {
    UserInfoModel uim = UserInfoModel.getFromAuth(authentication);
    return generateMasterToken(uim);
  }

  @LogMetrics(name = GraphiteConstants.JWT + GraphiteConstants.SER + ".master")
  public String generateMasterToken(UserInfoModel uim) {
    UserInfoMasterPojo ump = new UserInfoMasterPojo();
    UserUtils.copyProperties(uim, ump);
    
    final String authorities = ump.getAuthorities().stream().collect(Collectors.joining(","));
    return Jwts.builder().setSubject(ump.getUsername()).claim(AUTHORITIES_KEY, authorities)
        .compressWith(CompressionCodecs.DEFLATE)
        .signWith(SignatureAlgorithm.HS256, securityConstants.getSIGNING_KEY())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()
            + securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS() * 1000))
        .setId(getRandomId()).setIssuer(ump.serialize()).compact();
  }

  @LogMetrics(name = GraphiteConstants.JWT + GraphiteConstants.SER + ".access")
  public String generateAccessToken(UserInfo uInfo) {
    final String authorities = uInfo.getAuthorities().stream().collect(Collectors.joining(","));
    logger.debug(" authorities {} ", authorities);
    return Jwts.builder().setSubject(uInfo.getUsername()).claim(AUTHORITIES_KEY, authorities)
        .compressWith(CompressionCodecs.DEFLATE)
        .signWith(SignatureAlgorithm.HS256, securityConstants.getSIGNING_KEY())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()
            + securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS() * 1000))
        .setId(getRandomId()).setIssuer(uInfo.serialize()).compact();
  }

  private String getRandomId() {
    StringBuilder sb = new StringBuilder();
    sb.append(RandomStringUtils.random(5));
    return sb.toString();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && validateToken(token));
  }

  public Boolean validateToken(String token) {
    try {
      if(Boolean.TRUE.equals(isLifeTimeAuthToken(token)))
        return validateLifeTimeAuthToken(token);
      final String username = getUsernameFromToken(token);
      boolean isLogout = isAnyKeyPresent(token);
      return !isTokenExpired(token) && !isLogout && !isUsernameLogout(token, username);
    } catch (Exception e) {
    }
    return false;
  }

  public Boolean validateToken(String token, String username) {
    if(Boolean.TRUE.equals(isLifeTimeAuthToken(token)))
      return validateLifeTimeAuthToken(token);
    boolean isLogout = isAnyKeyPresent(token);
    logger.debug("username" + username);
    return !isTokenExpired(token) && !isLogout;
  }

  public Boolean validateLifeTimeAuthToken(String token){
    return lifeTimeAuthTokenService.isActiveLifeTimeToken(token);
  }

  // is lifeTime token if the token contains IS_LIFETIME_TOKN claim
  public Boolean isLifeTimeAuthToken(String token){
    Claims claims = getAllClaimsFromToken(token);
    return  claims.get(IS_LIFETIME_AUTH) != null;
  }

  private boolean isUsernameLogout(String token, String username) {
    boolean isUserLogout = false;
    long userLogoutEpoc = logoutCacheHolder.getCache(username);
    logger.debug("userLogoutEpoc " + isUserLogout + " ,  username :" + username);
    if (userLogoutEpoc > 0) {
      final Date expirationDate = getExpirationDateFromToken(token);
      long tokenCreationEpoc =
          expirationDate.getTime() - securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS() * 1000;
      if (tokenCreationEpoc <= userLogoutEpoc) {
        isUserLogout = true;
      }
    }
    logger.debug("isUserLogout" + isUserLogout);
    return isUserLogout;
  }

  private boolean isAnyKeyPresent(String... keys) {
    boolean isPresent = false;
    if (keys != null) {
      for (String key : keys) {
        isPresent = isPresent || (logoutCacheHolder.getCache(key) > 0);
        if (isPresent)
          break;
      }
    }
    return isPresent;
  }

  public String getUsernameFromTokenAfterValidation(String token) {
    try {
      String username = getUsernameFromToken(token);
      logger.debug("username" + username);
      return validateToken(token, username) ? username : null;
    } catch (Exception e) {
    }
    return null;
  }

  public UsernamePasswordAuthenticationToken getAuthentication(final String token,
      final UserDetails userDetails) {
    final JwtParser jwtParser = Jwts.parser().setSigningKey(securityConstants.getSIGNING_KEY());
    final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
    final Claims claims = claimsJws.getBody();
    final Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
  }

  public UsernamePasswordAuthenticationToken getAuthentication(final String accessToken) {
    final JwtParser jwtParser = Jwts.parser().setSigningKey(securityConstants.getSIGNING_KEY());
    final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(accessToken);
    final Claims claims = claimsJws.getBody();
    final Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    UserInfo ui = getUserFromAccessToken(accessToken);
    return (ui == null) ? null : new UsernamePasswordAuthenticationToken(ui, "", authorities);
  }

  public UsernamePasswordAuthenticationToken getAuthenticationAfterSwitching(final String token,
      final UserDetails userDetails) {

    final JwtParser jwtParser = Jwts.parser().setSigningKey(securityConstants.getSIGNING_KEY());
    final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

    final Claims claims = claimsJws.getBody();

    final Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
  }

  public String getTokenFromRequest(HttpServletRequest request) {
    return StringUtils.trim(request.getHeader(AUTH_HEADER));

  }

}
