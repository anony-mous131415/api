package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;

import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class TargetDeviceTypes implements ChangeComparable {

	public boolean selectAllMobileDeviceTypes;

	public TargetingObject mobileDeviceTypes;

	public TargetDeviceTypes() {
		selectAllMobileDeviceTypes = false;
		mobileDeviceTypes = new TargetingObject();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof TargetDeviceTypes))
			throw new NotComparableException("argument not of type TargetDeviceTypes, cannot be compared");

		if (!diff.different && selectAllMobileDeviceTypes != ((TargetDeviceTypes) o).selectAllMobileDeviceTypes)
			diff.different = true;

		List<BaseModel> commoni = new ArrayList<BaseModel>();
		if (mobileDeviceTypes != null && mobileDeviceTypes.targetList != null)
			commoni.addAll(mobileDeviceTypes.targetList);
		List<BaseModel> removedi = new ArrayList<BaseModel>();
		if (o != null && ((TargetDeviceTypes) o).mobileDeviceTypes != null
				&& ((TargetDeviceTypes) o).mobileDeviceTypes.targetList != null)
			removedi.addAll(((TargetDeviceTypes) o).mobileDeviceTypes.targetList);
		List<BaseModel> addedi = new ArrayList<BaseModel>();
		if (mobileDeviceTypes != null && mobileDeviceTypes.targetList != null)
			addedi.addAll(mobileDeviceTypes.targetList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<BaseModel> commone = new ArrayList<BaseModel>();
		if (mobileDeviceTypes != null && mobileDeviceTypes.blockedList != null)
			commone.addAll(mobileDeviceTypes.blockedList);
		List<BaseModel> removede = new ArrayList<BaseModel>();
		if (o != null && ((TargetDeviceTypes) o).mobileDeviceTypes != null
				&& ((TargetDeviceTypes) o).mobileDeviceTypes.blockedList != null)
			removede.addAll(((TargetDeviceTypes) o).mobileDeviceTypes.blockedList);
		List<BaseModel> addede = new ArrayList<BaseModel>();
		if (mobileDeviceTypes != null && mobileDeviceTypes.blockedList != null)
			addede.addAll(mobileDeviceTypes.blockedList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue(((TargetDeviceTypes) o));
		diff.newValue = getDiffValue(this);
		return diff;
	}

	private String getDiffValue(TargetDeviceTypes targetMobileTypes) {
		if (targetMobileTypes == null)
			return null;
		return "All mobile device types: " + targetMobileTypes.selectAllMobileDeviceTypes + ", Included: "
				+ StringUtils.formatBaseModel(targetMobileTypes.mobileDeviceTypes.targetList) + ", Excluded: "
				+ StringUtils.formatBaseModel(targetMobileTypes.mobileDeviceTypes.blockedList);
	}
}
