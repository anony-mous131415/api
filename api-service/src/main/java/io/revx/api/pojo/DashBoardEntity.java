package io.revx.api.pojo;

public enum DashBoardEntity {

  ADVERTISER("ADVERTISER", "advertiserId"), CAMPAIGN("CAMPAIGN", "campaignId"), STRATEGY("STRATEGY",
      "strategyId"), CREATIVE("CREATIVES",
          "creativeId"), HOMEPAGE("HOMEPAGE", "campaignId"), PRODUCT_CATALOG("HOMEPAGE", "none");

  private String name;
  private String column;

  private DashBoardEntity(String name, String column) {
    this.name = name;
    this.column = column;
  }

  public String getName() {
    return this.name;
  }

  public String getColumn() {
    return this.column;
  }

  public static DashBoardEntity fromString(String text) {
    if (text != null) {
      for (DashBoardEntity ge : DashBoardEntity.values()) {
        if (text.equalsIgnoreCase(ge.name)) {
          return ge;
        }
      }
    }
    return null;
  }
}
