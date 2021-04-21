package io.revx.core.model.audience;


public enum UserDataType {
  // User based on their browsing behavior on your desktop web-site or mobile
  // optimized site
  WEB_BROWSING(1),

  // User based on their activity on your Mobile Application
  MOBILE_APP(2),
  

  // Email Ids exported from your CRM System
  CRM_EMAIL(3),

  // Facebook Ids exported from your CRM System
  FACEBOOK_ID(4),

  // Phone numbers exported from your CRM System
  CRM_PHONE(5),

  //Lookalike audience for facebook
  LOOKALIKE_AUDIENCE(6);
  
  public final Integer id;

  private UserDataType(int id) {
      this.id = id;
  }

  public static UserDataType getById(Integer id) {
      for (UserDataType type : values()) {
          if (type.id.equals(id))
              return type;
      }
      return null;
  }
}
