package io.revx.core.model.targetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class RTBAggregators implements Serializable, ChangeComparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2273552405772485851L;

	public boolean selectAllAggregators;

	public TargetingObject aggregators;

	public RTBAggregators() {
		selectAllAggregators = false;
		aggregators = new TargetingObject();
	}

	public void cleanUp() {
		aggregators.cleanUp();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof RTBAggregators))
			throw new NotComparableException("argument not of type RTBAggregators, cannot be compared");

		if (!diff.different && selectAllAggregators != ((RTBAggregators) o).selectAllAggregators)
			diff.different = true;

		List<BaseModel> commoni = new ArrayList<BaseModel>();
		if (aggregators != null && aggregators.targetList != null)
			commoni.addAll(aggregators.targetList);
		List<BaseModel> removedi = new ArrayList<BaseModel>();
		if (o != null && ((RTBAggregators) o).aggregators != null
				&& ((RTBAggregators) o).aggregators.targetList != null)
			removedi.addAll(((RTBAggregators) o).aggregators.targetList);
		List<BaseModel> addedi = new ArrayList<BaseModel>();
		if (aggregators != null && aggregators.targetList != null)
			addedi.addAll(aggregators.targetList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<BaseModel> commone = new ArrayList<BaseModel>();
		if (aggregators != null && aggregators.blockedList != null)
			commone.addAll(aggregators.blockedList);
		List<BaseModel> removede = new ArrayList<BaseModel>();
		if (o != null && ((RTBAggregators) o).aggregators != null
				&& ((RTBAggregators) o).aggregators.blockedList != null)
			removede.addAll(((RTBAggregators) o).aggregators.blockedList);
		List<BaseModel> addede = new ArrayList<BaseModel>();
		if (aggregators != null && aggregators.blockedList != null)
			addede.addAll(aggregators.blockedList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((RTBAggregators) o, removedi, removede, "removed");
		diff.newValue = getDiffValue(this, addedi, addede, "added");
		return diff;
	}

	private String getDiffValue(RTBAggregators rtbAggregators, List<BaseModel> include, List<BaseModel> exclude,
			String type) {
		if (rtbAggregators == null)
			return null;
		// TODO: Read from Prop
		Integer aggregatorCountSavedInAuditChangeTable = 0; // PropertiesReader.getAggregatorCountSavedInAuditChangeTable();
		String returnString = "";
		List<BaseModel> newList = new ArrayList<BaseModel>();
		if (include.size() >= aggregatorCountSavedInAuditChangeTable
				|| exclude.size() >= aggregatorCountSavedInAuditChangeTable) {
//			int count = 0;
			if (include.size() > 0) {
				for (BaseModel b : include) {
//					count++;
//					if (count <= aggregatorCountSavedInAuditChangeTable)
					newList.add(b);
//					else
//						break;
				}
				returnString = "All aggregators: " + rtbAggregators.selectAllAggregators + ", Included: " + StringUtils.formatBaseModel(rtbAggregators.aggregators.targetList) + ", Excluded:"
						+ StringUtils.formatBaseModel(rtbAggregators.aggregators.blockedList);
			} else if (exclude.size() > 0) {
				for (BaseModel b : exclude) {
//					count++;
//					if (count <= aggregatorCountSavedInAuditChangeTable)
					newList.add(b);
//					else
//						break;
				}
				returnString = "All aggregators: " + rtbAggregators.selectAllAggregators + ", Included: " + StringUtils.formatBaseModel(rtbAggregators.aggregators.targetList) + ", Excluded:"
						+ StringUtils.formatBaseModel(rtbAggregators.aggregators.blockedList);
			}

		} else if (include.size() > 0 || exclude.size() > 0) {
			returnString = "All aggregators: " + rtbAggregators.selectAllAggregators + ", Included: " + StringUtils.formatBaseModel(rtbAggregators.aggregators.targetList) + ", Excluded:"
					+ StringUtils.formatBaseModel(rtbAggregators.aggregators.blockedList);
		}

		return returnString;
	}
}
