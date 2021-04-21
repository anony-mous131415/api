package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;
import io.revx.core.utils.StringUtils;

public class TargetOperatingSystem implements ChangeComparable {

	public boolean selectAllOperatingSystems;

	public ExtendedTargetingObject operatingSystems;

	public TargetOperatingSystem() {
		selectAllOperatingSystems = false;
		operatingSystems = new ExtendedTargetingObject();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof TargetOperatingSystem))
			throw new NotComparableException("argument not of type TargetOperatingSystem, cannot be compared");

		if (!diff.different && selectAllOperatingSystems != ((TargetOperatingSystem) o).selectAllOperatingSystems)
			diff.different = true;

		List<ExtendedBaseModel> commoni = new ArrayList<ExtendedBaseModel>();
		if (operatingSystems != null && operatingSystems.includeList != null)
			commoni.addAll(operatingSystems.includeList);
		List<ExtendedBaseModel> removedi = new ArrayList<ExtendedBaseModel>();
		if (o != null && ((TargetOperatingSystem) o).operatingSystems != null
				&& ((TargetOperatingSystem) o).operatingSystems.includeList != null)
			removedi.addAll(((TargetOperatingSystem) o).operatingSystems.includeList);
		List<ExtendedBaseModel> addedi = new ArrayList<ExtendedBaseModel>();
		if (operatingSystems != null && operatingSystems.includeList != null)
			addedi.addAll(operatingSystems.includeList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<ExtendedBaseModel> commone = new ArrayList<ExtendedBaseModel>();
		if (operatingSystems != null && operatingSystems.excludeList != null)
			commone.addAll(operatingSystems.excludeList);
		List<ExtendedBaseModel> removede = new ArrayList<ExtendedBaseModel>();
		if (o != null && ((TargetOperatingSystem) o).operatingSystems != null
				&& ((TargetOperatingSystem) o).operatingSystems.excludeList != null)
			removede.addAll(((TargetOperatingSystem) o).operatingSystems.excludeList);
		List<ExtendedBaseModel> addede = new ArrayList<ExtendedBaseModel>();
		if (operatingSystems != null && operatingSystems.excludeList != null)
			addede.addAll(operatingSystems.excludeList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((TargetOperatingSystem) o);
		diff.newValue = getDiffValue(this);
		return diff;
	}

	private String getDiffValue(TargetOperatingSystem targetOperatingSystem) {
		if (targetOperatingSystem == null)
			return null;
		return "All operating systems: " + targetOperatingSystem.selectAllOperatingSystems + ", Included: "
				+ StringUtils.formatExtendedBaseModel(targetOperatingSystem.operatingSystems.includeList)
				+ ", Excluded: " + StringUtils.formatExtendedBaseModel(targetOperatingSystem.operatingSystems.excludeList);
	}

}
