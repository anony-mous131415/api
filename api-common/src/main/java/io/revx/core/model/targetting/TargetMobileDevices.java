package io.revx.core.model.targetting;

import io.revx.core.exception.NotComparableException;

public class TargetMobileDevices implements ChangeComparable {

  public TargetOperatingSystem targetOperatingSystems;

  public TargetMobileDeviceBrands targetMobileDeviceBrands;

  public TargetMobileDeviceModels targetMobileModels;

  public TargetDeviceTypes targetDeviceTypes;

  @Override
  public Difference compareTo(ChangeComparable o) throws NotComparableException {

    Difference diff = new Difference();
    diff.oldValue = "";
    diff.newValue = "";
    diff.different = false;

    if (o == null)
      diff.different = true;
    else if (!(o instanceof TargetMobileDevices))
      throw new NotComparableException(
          "argument not of type TargetMobileDevices, cannot be compared");

    Difference osDiff = targetOperatingSystems
        .compareTo(((TargetOperatingSystem) ((TargetMobileDevices) o).targetOperatingSystems));
    if (osDiff.different == true) {
      diff.different = true;
      diff.oldValue = diff.oldValue + "targetOperatingSystems : " + osDiff.oldValue + ",";
      diff.newValue = diff.newValue + "targetOperatingSystems : " + osDiff.newValue + ",";
    }

    Difference brandsDiff = targetMobileDeviceBrands
        .compareTo(((TargetMobileDeviceBrands) ((TargetMobileDevices) o).targetMobileDeviceBrands));
    if (brandsDiff.different == true) {
      diff.different = true;
      diff.oldValue = diff.oldValue + "targetMobileDeviceBrands : " + brandsDiff.oldValue + ",";
      diff.newValue = diff.newValue + "targetMobileDeviceBrands : " + brandsDiff.newValue + ",";
    }

    Difference modelsDiff = targetMobileModels
        .compareTo(((TargetMobileDeviceModels) ((TargetMobileDevices) o).targetMobileModels));
    if (modelsDiff.different == true) {
      diff.different = true;
      diff.oldValue = diff.oldValue + "targetMobileDeviceModels : " + modelsDiff.oldValue + ",";
      diff.newValue = diff.newValue + "targetMobileDeviceModels : " + modelsDiff.newValue + ",";
    }

    Difference deviceTypesDiff = targetDeviceTypes
        .compareTo((TargetDeviceTypes) ((TargetMobileDevices) o).targetDeviceTypes);
    if (deviceTypesDiff.different == true) {
      diff.different = true;
      diff.oldValue = diff.oldValue + "targetMobileDeviceTypes : " + deviceTypesDiff.oldValue + ",";
      diff.newValue = diff.newValue + "targetMobileDeviceTypes : " + deviceTypesDiff.newValue + ",";
    }

    // if (!diff.different &&
    // targetMobileModels.compareTo(((TargetMobileDeviceModels)((TargetMobileDevices)o).targetMobileModels)).different)
    // diff.different = true;
    //
    // if (!diff.different &&
    // targetDeviceTypes.compareTo((TargetDeviceTypes)((TargetMobileDevices)o).targetDeviceTypes).different)
    // diff.different = true;

    if (diff.oldValue != null && diff.oldValue.endsWith(","))
      diff.oldValue = diff.oldValue.substring(0, diff.oldValue.length() - 1);

    if (diff.newValue != null && diff.newValue.endsWith(","))
      diff.newValue = diff.newValue.substring(0, diff.newValue.length() - 1);

    // diff.oldValue =

    return diff;
  }

}
