package io.revx.core.model.audience;

import java.net.MalformedURLException;
import java.net.URL;
import io.revx.core.enums.AuthMethod;
import io.revx.core.enums.Protocol;
import io.revx.core.model.crm.RemoteFileDTO;

public class PixelRemoteConfigDTO {


  protected Integer protocol;


  protected String url;


  protected String username;


  protected String password;


  public Integer getProtocol() {
    return protocol;
  }


  public void setProtocol(Integer protocol) {
    this.protocol = protocol;
  }


  public String getUrl() {
    return url;
  }


  public void setUrl(String url) {
    this.url = url;
  }


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


  protected static void setUrl(String url, RemoteFileDTO config)
          throws MalformedURLException {
      URL u = new URL(Protocol.HTTP.name().toLowerCase() + "://" + url);
      config.setHost(u.getHost());
      config.setPort(u.getPort() < 0 ? null : u.getPort());
      if (u.getPath() != null && u.getQuery() != null)
          config.setPathTemplate(u.getPath() + "?" + u.getQuery());
      else if (u.getPath() != null)
          config.setPathTemplate(u.getPath());
      else if (u.getQuery() != null)
          config.setPathTemplate("?" + u.getQuery());
  }

  protected static String getUrl(String host, Integer port,
          String pathTemplate) {
      StringBuilder url = new StringBuilder();
      url.append(host);
      if (port != null && port != 0) {
          url.append(":").append(port);
      }
      if (pathTemplate != null && !pathTemplate.isEmpty()) {
          url.append(pathTemplate);
      }
      return url.toString();
  }
  
  public RemoteFileDTO getRemoteFile(){
      return updateRemoteFile(new RemoteFileDTO());
  }
  
  public RemoteFileDTO updateRemoteFile(RemoteFileDTO file){
      file.setProtocol(Protocol.getById(this.protocol));
      try {
          setUrl(this.url, file);
      } catch (MalformedURLException e) {
          throw new RuntimeException("Exception parsing the url ", e);
      }
      file.setUsername(this.username);
      file.setPassword(this.password);
      if (this.username != null && !this.username.trim().equals("")
              && this.password != null && !this.password.trim().equals("")) {
          file.setAuthMethod(AuthMethod.LOGIN);
      }
      return file;
  }


}
