package io.revx.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import io.revx.auth.utils.Md5Utils;

@Component
public class Md5PasswordEncoder implements PasswordEncoder {

  public String encode(CharSequence rawPassword) {
	  //Removing the encoding
//    String encodePassword = Md5Utils.getMd5(rawPassword.toString());
//    return encodePassword;
    return rawPassword.toString();
  }

  public boolean matches(CharSequence rawPassword, String encodedPassword) {
	//Removing the encoding
//    String encodePass = Md5Utils.getMd5(rawPassword.toString());
    return StringUtils.equals(rawPassword, encodedPassword);
  }
}
