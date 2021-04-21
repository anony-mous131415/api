
package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;

public class TargetBrowsers implements ChangeComparable {

  public boolean selectAllBrowsers;

  public TargetingObject browsers;

  public TargetBrowsers() {
    selectAllBrowsers = false;
    browsers = new TargetingObject();
  }

  @Override
  public Difference compareTo(ChangeComparable o) throws NotComparableException {
    Difference diff = new Difference();
    diff.different = false;

    if (o == null)
      diff.different = true;
    else if (!(o instanceof TargetBrowsers))
      throw new NotComparableException("argument not of type TargetBrowsers, cannot be compared");

    if (!diff.different && selectAllBrowsers != ((TargetBrowsers) o).selectAllBrowsers)
      diff.different = true;

    List<BaseModel> commoni = new ArrayList<BaseModel>();
    if (browsers != null && browsers.targetList != null)
      commoni.addAll(browsers.targetList);
    List<BaseModel> removedi = new ArrayList<BaseModel>();
    if (o != null && ((TargetBrowsers) o).browsers != null
        && ((TargetBrowsers) o).browsers.targetList != null)
      removedi.addAll(((TargetBrowsers) o).browsers.targetList);
    List<BaseModel> addedi = new ArrayList<BaseModel>();
    if (browsers != null && browsers.targetList != null)
      addedi.addAll(browsers.targetList);

    commoni.retainAll(removedi);
    addedi.removeAll(commoni);
    removedi.removeAll(commoni);

    if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
      diff.different = true;

    List<BaseModel> commone = new ArrayList<BaseModel>();
    if (browsers != null && browsers.blockedList != null)
      commone.addAll(browsers.blockedList);
    List<BaseModel> removede = new ArrayList<BaseModel>();
    if (o != null && ((TargetBrowsers) o).browsers != null
        && ((TargetBrowsers) o).browsers.blockedList != null)
      removede.addAll(((TargetBrowsers) o).browsers.blockedList);
    List<BaseModel> addede = new ArrayList<BaseModel>();
    if (browsers != null && browsers.blockedList != null)
      addede.addAll(browsers.blockedList);

    commone.retainAll(removede);
    addede.removeAll(commone);
    removede.removeAll(commone);

    if (!diff.different && (addede.size() > 0 || removede.size() > 0))
      diff.different = true;

    diff.oldValue = getDiffValue((TargetBrowsers) o, removedi, removede, "removed");
    diff.newValue = getDiffValue(this, addedi, addede, "added");
    return diff;
  }

  private String getDiffValue(TargetBrowsers targetBrowsers, List<BaseModel> include,
      List<BaseModel> exclude, String type) {
    if (targetBrowsers == null)
      return null;
    return "select all browsers: " + targetBrowsers.selectAllBrowsers + ", include list(" + type
        + "): " + include + ", exclude list(" + type + "): " + exclude;
  }

}
