package io.revx.core.model.requests;

import java.util.List;
import io.revx.core.model.StatusBaseObject;

public class MenuCrubResponse {

  private String menuName;
  private List<StatusBaseObject> menuList;

  public MenuCrubResponse() {
    super();
  }

  public MenuCrubResponse(String menuName) {
    super();
    this.menuName = menuName;
  }

  public MenuCrubResponse(String menuName, List<StatusBaseObject> menuList) {
    super();
    this.menuName = menuName;
    this.menuList = menuList;
  }

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public List<StatusBaseObject> getMenuList() {
    return menuList;
  }

  public void setMenuList(List<StatusBaseObject> menuList) {
    this.menuList = menuList;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MenuCrubResponse [menuName=");
    builder.append(menuName);
    builder.append(", menuList=");
    builder.append(menuList);
    builder.append("]");
    return builder.toString();
  }

}
