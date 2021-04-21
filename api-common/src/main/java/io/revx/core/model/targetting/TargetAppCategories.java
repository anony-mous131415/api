package io.revx.core.model.targetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;

public class TargetAppCategories implements Serializable, ChangeComparable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Integer osId;

  public boolean selectAll;

  public TargetingObject appCategories;


  public TargetAppCategories() {
    selectAll = false;
    osId = null;
    appCategories = new TargetingObject();
  }


  public Integer getOsId() {
    return osId;
  }


  public void setOsId(Integer osId) {
    this.osId = osId;
  }


  public boolean isSelectAll() {
    return selectAll;
  }


  public void setSelectAll(boolean selectAll) {
    this.selectAll = selectAll;
  }


  public TargetingObject getAppCategories() {
    return appCategories;
  }


  public void setAppCategories(TargetingObject appCategories) {
    this.appCategories = appCategories;
  }


  @Override
  public Difference compareTo(ChangeComparable o) throws NotComparableException {
    Difference diff = new Difference();
    diff.different = false;

    if (o == null)
      diff.different = true;
    else if (!(o instanceof TargetAppCategories))
      throw new NotComparableException(
          "argument not of type TargetAppsWithCategories, cannot be compared");

    if (!diff.different && selectAll != ((TargetAppCategories) o).selectAll)
      diff.different = true;

    List<BaseModel> commoni = new ArrayList<BaseModel>();
    if (appCategories != null && appCategories.targetList != null)
      commoni.addAll(appCategories.targetList);
    List<BaseModel> removedi = new ArrayList<BaseModel>();
    if (o != null && ((TargetAppCategories) o).appCategories != null
        && ((TargetAppCategories) o).appCategories.targetList != null)
      removedi.addAll(((TargetAppCategories) o).appCategories.targetList);
    List<BaseModel> addedi = new ArrayList<BaseModel>();
    if (appCategories != null && appCategories.targetList != null)
      addedi.addAll(appCategories.targetList);

    commoni.retainAll(removedi);
    addedi.removeAll(commoni);
    removedi.removeAll(commoni);

    if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
      diff.different = true;

    List<BaseModel> commone = new ArrayList<BaseModel>();
    if (appCategories != null && appCategories.blockedList != null)
      commone.addAll(appCategories.blockedList);
    List<BaseModel> removede = new ArrayList<BaseModel>();
    if (o != null && ((RTBAggregators) o).aggregators != null
        && ((TargetAppCategories) o).appCategories.blockedList != null)
      removede.addAll(((TargetAppCategories) o).appCategories.blockedList);
    List<BaseModel> addede = new ArrayList<BaseModel>();
    if (appCategories != null && appCategories.blockedList != null)
      addede.addAll(appCategories.blockedList);

    commone.retainAll(removede);
    addede.removeAll(commone);
    removede.removeAll(commone);

    if (!diff.different && (addede.size() > 0 || removede.size() > 0))
      diff.different = true;

    diff.oldValue = getDiffValue((TargetAppCategories) o, removedi, removede, "removed");
    diff.newValue = getDiffValue(this, addedi, addede, "added");
    return diff;
  }

  private String getDiffValue(TargetAppCategories targetAppsWithCategories, List<BaseModel> include,
      List<BaseModel> exclude, String type) {
    if (targetAppsWithCategories == null)
      return null;
    // TODO: read from Prop
    Integer appCategoriesCountSavedInAuditChangeTable = 0;// PropertiesReader.getAppCategoriesCountSavedInAuditChangeTable();
    String returnString = "";
    List<BaseModel> newList = new ArrayList<BaseModel>();
    if (include.size() >= appCategoriesCountSavedInAuditChangeTable
        || exclude.size() >= appCategoriesCountSavedInAuditChangeTable) {
      int count = 0;
      if (include.size() > 0) {
        for (BaseModel b : include) {
          count++;
          if (count <= appCategoriesCountSavedInAuditChangeTable)
            newList.add(b);
          else
            break;
        }
        returnString = "select all categories: " + targetAppsWithCategories.selectAll
            + ", include list(" + type + "): " + newList.toString() + "..." + ", exclude list("
            + type + "): " + exclude;
      } else if (exclude.size() > 0) {
        for (BaseModel b : exclude) {
          count++;
          if (count <= appCategoriesCountSavedInAuditChangeTable)
            newList.add(b);
          else
            break;
        }
        returnString = "select all categories: " + targetAppsWithCategories.selectAll
            + ", include list(" + type + "): " + include + ", exclude list(" + type + "): "
            + newList.toString() + "...";
      }

    } else if (include.size() > 0 || exclude.size() > 0) {
      returnString = "select all categories: " + targetAppsWithCategories.selectAll
          + ", include list(" + type + "): " + include + ", exclude list(" + type + "): " + exclude;
    }

    return returnString;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TargetAppCategories [osId=").append(osId).append(", selectAll=")
        .append(selectAll).append(", appCategories=").append(appCategories).append("]");
    return builder.toString();
  }


}
