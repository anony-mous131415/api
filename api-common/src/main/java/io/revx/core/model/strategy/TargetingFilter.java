package io.revx.core.model.strategy;

public enum TargetingFilter {
  DAY_OF_WEEK(1),
  HOUR_OF_DAY(2),
  GEO_CITY(3),
  GEO_REGION(4),
  GEO_COUNTRY(5),
  BROWSER(6),
  OS(7),
  LANGUAGE(8),
  RESOLUTION(9),
  FOLD_POSITION(10),
  USER_SEGMENT(11),
  RTB_AGGREGATOR(12),
  RTB_PUBLISHER(13),
  RTB_SITE(14),
  RTB_PUB_CATEGORY(15),
  DEV_BRAND(16),
  DEV_MODEL(17),
  PLACEMENT(18),
  DEV_TYPE(19),
  OS_VERSION(20),
  CONNECTION_TYPE(21),
  CARRIER(22),
  NONSKIPPABLE_VIDEO(23),
  REWARDED_VIDEO(24),
  IS_APP_PUBLISHED(25),
  APP_REVIEW_SCORE(26),
  APP_CATEGORY(27),
  COMPANION_SLOT(28),
  DEAL_CATEGORY(29),
  AUCTION_TYPE(30); 

  private long id;

  TargetingFilter(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }
}
