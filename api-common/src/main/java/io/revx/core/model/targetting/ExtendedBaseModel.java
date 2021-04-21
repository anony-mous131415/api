package io.revx.core.model.targetting;

import java.util.HashMap;
import java.util.Map;
import io.revx.core.model.BaseModel;

public class ExtendedBaseModel extends BaseModel {

  private static final long serialVersionUID = 3565224363562104934L;

  public Map<String, BaseModel> properties;

  public ExtendedBaseModel() {}

  public ExtendedBaseModel(Long id, String name) {
    super(id, name);
    this.properties = new HashMap<>();
  }

  public ExtendedBaseModel(Integer id, String name, Map<String, BaseModel> properties) {
    super(id, name);
    this.properties = properties;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id: " + id + ", name: " + name);
    if (properties != null && properties.size() > 0) {
      sb.append(", properties : { ");
      for (Map.Entry<String, BaseModel> entry : properties.entrySet()) {
        String key = entry.getKey();
        BaseModel value = entry.getValue();
        sb.append(key + " : " + value.toString() + " ");
      }

      sb.append("}");
    }

    return sb.toString();
  }
}
