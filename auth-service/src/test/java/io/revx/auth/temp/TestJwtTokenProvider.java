package io.revx.auth.temp;

import static io.revx.auth.constants.SecurityConstants.AUTHORITIES_KEY;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.revx.auth.pojo.UserInfoModel;

public class TestJwtTokenProvider {

  private static final String SIGNING_KEY = "Akhilesh@123testingJwtTokensForSomething";

  private static final FSTConfiguration conf = FSTConfiguration.createJsonConfiguration();

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public MyInfo getMyInfoFromToken(String token) {
    String str = getClaimFromToken(token, Claims::getIssuer);
    return (MyInfo) conf.asObject(str.getBytes());
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(Authentication authentication) {
    final String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    return Jwts.builder().setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities)
        .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 18000 * 1000)).setId(getRandomId())
        .compact();
  }

  public String generateToken(UserInfoModel userInfo) {
    final String authorities = userInfo.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    return Jwts.builder().setSubject(userInfo.getUsername()).claim(AUTHORITIES_KEY, authorities)
        .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 18000 * 1000)).setId(getRandomId())
        .compact();
  }

  public String generateToken(MyInfo info) {
    String myInfoJson = new String(conf.asByteArray(info));
    return Jwts.builder().setSubject(info.getUsername())
        .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
        .setIssuedAt(new Date(System.currentTimeMillis())).setIssuer(myInfoJson)
        .setExpiration(new Date(System.currentTimeMillis() + 18000 * 1000)).setId(getRandomId())
        .compact();
  }

  private String getRandomId() {
    StringBuffer sb = new StringBuffer();
    sb.append(RandomStringUtils.random(5));
    return sb.toString();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

}
