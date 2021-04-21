package io.revx.core.model.creative;

public enum CompanionFormat {

  GIF("image/gif"), JPG("image/jpeg"), PNG("image/png");

  private String xmlAttributeValue;

  private CompanionFormat(String xmlAttributeValue) {
    this.xmlAttributeValue = xmlAttributeValue;
  }

  public String getXmlAttributeValue() {
    return xmlAttributeValue;
  }

  public void setXmlAttributeValue(String xmlAttributeValue) {
    this.xmlAttributeValue = xmlAttributeValue;
  }

  public static CompanionFormat getByXmlAttributeValue(String xmlAttributeValue) {
    if (xmlAttributeValue.equals("image/gif")) {
      return CompanionFormat.GIF;
    } else if (xmlAttributeValue.equals("image/jpeg")) {
      return CompanionFormat.JPG;
    } else if (xmlAttributeValue.equals("image/png")) {
      return CompanionFormat.PNG;
    } else {
      // default
      return CompanionFormat.JPG;
    }
  }
}
