package io.revx.api.enums;

import java.util.HashMap;
import java.util.Map;

public enum CampaignObjective {

  WEBSITE(1l), MOBILE_APP_INSTALLS(2l), MOBILE_APP_ENGAGEMENT(3l), BRAND_AWARENESS(4l);

  private Long id;

  private static Map<Long, CampaignObjective> campaignObjectiveIdNameMap =
      new HashMap<Long, CampaignObjective>();

  static {
    for (CampaignObjective campaignObjective : CampaignObjective.values()) {
      campaignObjectiveIdNameMap.put(campaignObjective.getId(), campaignObjective);
    }
  }

  public Long getId() {
    return id;
  }

  private CampaignObjective(Long id) {
    this.id = id;
  }

  public static CampaignObjective getById(Long id) {
    return campaignObjectiveIdNameMap.get(id);
  }

}
