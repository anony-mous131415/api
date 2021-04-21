package io.revx.core.model;

@SuppressWarnings("serial")
public class Licensee extends StatusTimeModel {

  private String currencyCode;

  public Licensee() {}

  public Licensee(long id, String name) {
    super(id, name);
  }

  public Licensee(long id, String name, String currencyCode) {
    super(id, name);
    this.currencyCode = currencyCode;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Licensee other = (Licensee) obj;
    if (currencyCode == null) {
      if (other.currencyCode != null)
        return false;
    } else if (!currencyCode.equals(other.currencyCode))
      return false;
    return true;
  }

}
