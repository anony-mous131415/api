package io.revx.auth.requests;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.NotBlank;

@ApiModel
public class UserLoginRequest {

  @ApiModelProperty(notes = "User Name", required = true)
  @NotBlank
  private String username;

  @ApiModelProperty(notes = "User Password", required = true)
  @NotBlank
  private String password;

  @ApiModelProperty(notes = "User Licensee for Login")
  private int licenseeId;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(int licenseeId) {
    this.licenseeId = licenseeId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserLoginRequest [username=");
    builder.append(username);
    builder.append(", licenseeId=");
    builder.append(licenseeId);
    builder.append("]");
    return builder.toString();
  }

  public static UserLoginRequest getFromAuth(Authentication auth) {
    if (auth != null && auth.getDetails() instanceof UserLoginRequest) {
        return (UserLoginRequest) auth.getDetails();
    }
    return null;

  }
}
