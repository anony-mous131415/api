package io.revx.core.model.targetting;

public enum PricingType {
  CPM, CPC, CPA, REV_SHARE, Margin;

  public Long id;

  public static PricingType get(Long i) {
    if (i == null)
      return null;
    if (i.equals(PricingType.CPM.id))
      return PricingType.CPM;
    if (i.equals(PricingType.CPC.id))
      return PricingType.CPC;
    if (i.equals(PricingType.CPA.id))
      return PricingType.CPA;
    if (i.equals(PricingType.REV_SHARE.id))
      return PricingType.REV_SHARE;
    if (i.equals(PricingType.Margin.id))
      return PricingType.Margin;
    return null;
  }
}
