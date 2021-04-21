package io.revx.core.response;

public class TokenResponse {
  private String username;
  private String token;
  private String masterToken;

  public TokenResponse(String username, String masterToken) {
    super();
    this.username = username;
    this.masterToken = masterToken;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the masterToken
   */
  public String getMasterToken() {
    return masterToken;
  }

  /**
   * @param masterToken the masterToken to set
   */
  public void setMasterToken(String masterToken) {
    this.masterToken = masterToken;
  }



  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TokenResponse [username=");
    builder.append(username);
    builder.append(", token=");
    builder.append(token);
    builder.append(", masterToken=");
    builder.append(masterToken);
    builder.append("]");
    return builder.toString();
  }

}
