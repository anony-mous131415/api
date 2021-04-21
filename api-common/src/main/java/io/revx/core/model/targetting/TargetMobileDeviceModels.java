package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;

import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class TargetMobileDeviceModels implements ChangeComparable {

	public boolean selectAllMobileDeviceModels;

	public ExtendedTargetingObject mobileDeviceModels;

	public TargetMobileDeviceModels() {
		selectAllMobileDeviceModels = false;
		mobileDeviceModels = new ExtendedTargetingObject();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof TargetMobileDeviceModels))
			throw new NotComparableException("argument not of type TargetMobileDeviceModels, cannot be compared");

		if (!diff.different
				&& selectAllMobileDeviceModels != ((TargetMobileDeviceModels) o).selectAllMobileDeviceModels)
			diff.different = true;

		List<BaseModel> commoni = new ArrayList<BaseModel>();
		if (mobileDeviceModels != null && mobileDeviceModels.includeList != null)
			commoni.addAll(mobileDeviceModels.includeList);
		List<BaseModel> removedi = new ArrayList<BaseModel>();
		if (o != null && ((TargetMobileDeviceModels) o).mobileDeviceModels != null
				&& ((TargetMobileDeviceModels) o).mobileDeviceModels.includeList != null)
			removedi.addAll(((TargetMobileDeviceModels) o).mobileDeviceModels.includeList);
		List<BaseModel> addedi = new ArrayList<BaseModel>();
		if (mobileDeviceModels != null && mobileDeviceModels.includeList != null)
			addedi.addAll(mobileDeviceModels.includeList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<BaseModel> commone = new ArrayList<BaseModel>();
		if (mobileDeviceModels != null && mobileDeviceModels.excludeList != null)
			commone.addAll(mobileDeviceModels.excludeList);
		List<BaseModel> removede = new ArrayList<BaseModel>();
		if (o != null && ((TargetMobileDeviceModels) o).mobileDeviceModels != null
				&& ((TargetMobileDeviceModels) o).mobileDeviceModels.excludeList != null)
			removede.addAll(((TargetMobileDeviceModels) o).mobileDeviceModels.excludeList);
		List<BaseModel> addede = new ArrayList<BaseModel>();
		if (mobileDeviceModels != null && mobileDeviceModels.excludeList != null)
			addede.addAll(mobileDeviceModels.excludeList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((TargetMobileDeviceModels) o, removedi, removede, "removed");
		diff.newValue = getDiffValue(this, addedi, addede, "added");
		return diff;
	}

	private String getDiffValue(TargetMobileDeviceModels targetMobileModels, List<BaseModel> include,
			List<BaseModel> exclude, String type) {
		if (targetMobileModels == null)
			return null;
		return "All mobile device models: " + targetMobileModels.selectAllMobileDeviceModels + ", Included(" + type
				+ "): " + StringUtils.formatBaseModel(include) + ", Excluded(" + type + "): "
				+ StringUtils.formatBaseModel(exclude);
	}
}
