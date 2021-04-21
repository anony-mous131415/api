
package io.revx.core.model.targetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class TargetingObject implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1830970888079915237L;

  public List<BaseModel> targetList;

  public List<BaseModel> blockedList;

  public TargetingObject() {
    targetList = new ArrayList<>();
    blockedList = new ArrayList<>();
  }

  public void cleanUp() {
    targetList.clear();
    blockedList.clear();
    targetList = null;
    blockedList = null;
  }

  public void clear() {
    targetList.clear();
    blockedList.clear();
  }

  public static boolean isEmptyTargetting(TargetingObject obj) {
    if (obj == null
        || (CollectionUtils.isEmpty(obj.targetList) && CollectionUtils.isEmpty(obj.blockedList)))
      return true;
    return false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Targeted: ").append(StringUtils.formatBaseModel(targetList)).append(", Blocked:")
        .append(StringUtils.formatBaseModel(blockedList));
    return builder.toString();
  }


}
