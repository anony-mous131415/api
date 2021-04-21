package io.revx.querybuilder.objs;

import io.revx.querybuilder.enums.Filter;

public class FilterComponent {

  private String value;
  private Filter field;

  public FilterComponent(Filter field, String value) {
    this.setValue(value);
    this.setField(field);
  }

  public FilterComponent(Filter field, long value) {
    this.setValue(String.valueOf(value));
    this.setField(field);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Filter getField() {
    return field;
  }

  public void setField(Filter field) {
    this.field = field;
  }

  public String getFilterString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.field.getColumnNameInTable());
    sb.append(" = ");
    sb.append(this.value);

    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FilterComponent other = (FilterComponent) obj;
    if (field != other.field)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

}
