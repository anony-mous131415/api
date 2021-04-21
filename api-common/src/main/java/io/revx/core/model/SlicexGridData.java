package io.revx.core.model;

import java.math.BigDecimal;

public class SlicexGridData extends SlicexData {

  private static final long serialVersionUID = -8875753091948685040L;

  protected BigDecimal compareToValue;

  public SlicexGridData() {

  }

  public SlicexGridData(BigDecimal impressions, BigDecimal clicks, BigDecimal viewConversions,
      BigDecimal clickConversions, BigDecimal impInstalls, BigDecimal clickInstalls,
      BigDecimal revenue, BigDecimal cost, Long id, String name, String currencyId,
      BigDecimal compareToValue) {
    super(null, null, impressions, clicks, viewConversions, clickConversions, impInstalls,
        clickInstalls, revenue, cost, currencyId);
    setId(id);
    setName(name);
    setCompareToValue(compareToValue);
  }

  public BigDecimal getCompareToValue() {
    return compareToValue;
  }

  public void setCompareToValue(BigDecimal compareToValue) {
    this.compareToValue = compareToValue;
  }
}
