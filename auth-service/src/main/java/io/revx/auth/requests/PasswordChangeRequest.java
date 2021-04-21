package io.revx.auth.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PasswordChangeRequest {

  @NotNull
  @NotBlank
  private String username;

  private String oldPassword;

  @NotNull
  @NotBlank
  private String newPassword;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ChangePasswordRequest [username=");
    builder.append(username);
    builder.append("]");
    return builder.toString();
  }

}
