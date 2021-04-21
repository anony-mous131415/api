package io.revx.querybuilder.objs;

import java.util.List;
import java.util.Map;
import java.util.Set;



public abstract class CalculatedFields {

  Map<String, String> fields;
  String tableName;
  List<String> columns;

  public String getFormulaForTheField(String fieldName) {
    return this.fields.get(fieldName);
  }

  public Set<String> getAllFields() {
    return this.fields.keySet();
  }

  public String getTableName() {
    return this.tableName;
  }

  public String getSelectClause() {

    StringBuilder sb = new StringBuilder();

    Set<String> keySet = this.fields.keySet();

    for (String fieldName : keySet) {
      String formula = this.fields.get(fieldName);
      sb.append(formula);
      sb.append(" AS ");
      sb.append(fieldName);
      sb.append(", ");
      this.columns.add(fieldName);
    }

    sb.delete(sb.length() - 2, sb.length() - 1);

    return sb.toString();

  }

  public List<String> getColumns() {
    return this.columns;
  }

}
