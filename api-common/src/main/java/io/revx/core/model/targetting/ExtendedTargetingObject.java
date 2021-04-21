package io.revx.core.model.targetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ExtendedTargetingObject implements Serializable {

  public List<ExtendedBaseModel> includeList;

  public List<ExtendedBaseModel> excludeList;

  public ExtendedTargetingObject() {
    includeList = new ArrayList<ExtendedBaseModel>();
    excludeList = new ArrayList<ExtendedBaseModel>();
  }

  public void cleanUp() {
    includeList.clear();
    excludeList.clear();
    includeList = null;
    excludeList = null;
  }

  public void clear() {
    includeList.clear();
    excludeList.clear();
  }
  
  public void addIntoIncludeList(ExtendedBaseModel extendedBaseModel) {
   if(CollectionUtils.isEmpty(includeList))
     includeList = new ArrayList<ExtendedBaseModel>();
   
   includeList.add(extendedBaseModel);
  }
  
  public void addAllIntoIncludeList(List<ExtendedBaseModel> extendedBaseModel) {
    if(CollectionUtils.isEmpty(includeList))
      includeList = new ArrayList<ExtendedBaseModel>();
    
    includeList.addAll(extendedBaseModel);
   }
  
  public void addIntoExcludeeList(ExtendedBaseModel extendedBaseModel) {
    if(CollectionUtils.isEmpty(excludeList))
      excludeList = new ArrayList<ExtendedBaseModel>();
    
    excludeList.add(extendedBaseModel);
   }
   
   public void addAllIntoExcludeeList(List<ExtendedBaseModel> extendedBaseModel) {
     if(CollectionUtils.isEmpty(excludeList))
       excludeList = new ArrayList<ExtendedBaseModel>();
     
     excludeList.addAll(extendedBaseModel);
    }
}
