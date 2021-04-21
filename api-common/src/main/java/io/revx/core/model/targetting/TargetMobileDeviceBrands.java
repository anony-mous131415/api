package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class TargetMobileDeviceBrands implements ChangeComparable {

	public boolean selectAllMobileDeviceBrands;

	public TargetingObject mobileDeviceBrands;

	public TargetMobileDeviceBrands() {
		selectAllMobileDeviceBrands = false;
		mobileDeviceBrands = new TargetingObject();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof TargetMobileDeviceBrands))
			throw new NotComparableException("argument not of type TargetMobileDeviceBrands, cannot be compared");

		if (!diff.different
				&& selectAllMobileDeviceBrands != ((TargetMobileDeviceBrands) o).selectAllMobileDeviceBrands)
			diff.different = true;

		List<BaseModel> commoni = new ArrayList<BaseModel>();
		if (mobileDeviceBrands != null && mobileDeviceBrands.targetList != null)
			commoni.addAll(mobileDeviceBrands.targetList);
		List<BaseModel> removedi = new ArrayList<BaseModel>();
		if (o != null && ((TargetMobileDeviceBrands) o).mobileDeviceBrands != null
				&& ((TargetMobileDeviceBrands) o).mobileDeviceBrands.targetList != null)
			removedi.addAll(((TargetMobileDeviceBrands) o).mobileDeviceBrands.targetList);
		List<BaseModel> addedi = new ArrayList<BaseModel>();
		if (mobileDeviceBrands != null && mobileDeviceBrands.targetList != null)
			addedi.addAll(mobileDeviceBrands.targetList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<BaseModel> commone = new ArrayList<BaseModel>();
		if (mobileDeviceBrands != null && mobileDeviceBrands.blockedList != null)
			commone.addAll(mobileDeviceBrands.blockedList);
		List<BaseModel> removede = new ArrayList<BaseModel>();
		if (o != null && ((TargetMobileDeviceBrands) o).mobileDeviceBrands != null
				&& ((TargetMobileDeviceBrands) o).mobileDeviceBrands.blockedList != null)
			removede.addAll(((TargetMobileDeviceBrands) o).mobileDeviceBrands.blockedList);
		List<BaseModel> addede = new ArrayList<BaseModel>();
		if (mobileDeviceBrands != null && mobileDeviceBrands.blockedList != null)
			addede.addAll(mobileDeviceBrands.blockedList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((TargetMobileDeviceBrands) o, removedi, removede, "removed");
		diff.newValue = getDiffValue(this, addedi, addede, "added");
		return diff;
	}

	private String getDiffValue(TargetMobileDeviceBrands targetMobileModels, List<BaseModel> include,
			List<BaseModel> exclude, String type) {
		if (targetMobileModels == null)
			return null;
		return "All mobile device brands: " + targetMobileModels.selectAllMobileDeviceBrands + ", Included(" + type
				+ "): " + StringUtils.formatBaseModel(include) + ", Excluded(" + type + "): "
				+ StringUtils.formatBaseModel(exclude);
	}
}
