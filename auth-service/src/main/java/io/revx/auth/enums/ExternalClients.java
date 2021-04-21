package io.revx.auth.enums;

import java.util.HashMap;
import java.util.Map;

public enum ExternalClients {
  GOOGLE("google"), FACEBOOK("facebook"), OFFICE("office");
  private static Map<String, ExternalClients> map = new HashMap<String, ExternalClients>();

  static {
    for (ExternalClients legEnum : ExternalClients.values()) {
      map.put(legEnum.clientname, legEnum);
    }
  }
  String clientname;

  private ExternalClients(String clientname) {
    this.clientname = clientname;
  }

  public static ExternalClients getByClientName(String clientName) {
    return map.get(clientName);
  }
}
