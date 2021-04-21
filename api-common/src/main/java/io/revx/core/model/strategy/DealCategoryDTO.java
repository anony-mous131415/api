package io.revx.core.model.strategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.targetting.ChangeComparable;
import io.revx.core.model.targetting.Difference;
import io.revx.core.model.targetting.TargetingObject;
import io.revx.core.utils.StringUtils;

public class DealCategoryDTO implements Serializable, ChangeComparable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public boolean selectAll;

	public TargetingObject dealCategory;

	public DealCategoryDTO() {
		this.selectAll = true;
		this.dealCategory = new TargetingObject();
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public TargetingObject getDealCategory() {
		return dealCategory;
	}

	public void setDealCategory(TargetingObject dealCategory) {
		this.dealCategory = dealCategory;
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof DealCategoryDTO))
			throw new NotComparableException("argument not of type AdvanceTargetingDTO, cannot be compared");

		if (!diff.different && selectAll != ((DealCategoryDTO) o).selectAll)
			diff.different = true;

		List<BaseModel> commoni = new ArrayList<BaseModel>();
		if (dealCategory != null && dealCategory.targetList != null)
			commoni.addAll(dealCategory.targetList);
		List<BaseModel> removedi = new ArrayList<BaseModel>();
		if (o != null && ((DealCategoryDTO) o).dealCategory != null
				&& ((DealCategoryDTO) o).dealCategory.targetList != null)
			removedi.addAll(((DealCategoryDTO) o).dealCategory.targetList);
		List<BaseModel> addedi = new ArrayList<BaseModel>();
		if (dealCategory != null && dealCategory.targetList != null)
			addedi.addAll(dealCategory.targetList);

		commoni.retainAll(removedi);
		addedi.removeAll(commoni);
		removedi.removeAll(commoni);

		if (!diff.different && (addedi.size() > 0 || removedi.size() > 0))
			diff.different = true;

		List<BaseModel> commone = new ArrayList<BaseModel>();
		if (dealCategory != null && dealCategory.blockedList != null)
			commone.addAll(dealCategory.blockedList);
		List<BaseModel> removede = new ArrayList<BaseModel>();
		if (o != null && ((DealCategoryDTO) o).dealCategory.blockedList != null)
			removede.addAll(((DealCategoryDTO) o).dealCategory.blockedList);
		List<BaseModel> addede = new ArrayList<BaseModel>();
		if (dealCategory != null && dealCategory.blockedList != null)
			addede.addAll(dealCategory.blockedList);

		commone.retainAll(removede);
		addede.removeAll(commone);
		removede.removeAll(commone);

		if (!diff.different && (addede.size() > 0 || removede.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((DealCategoryDTO) o, removedi, removede, "removed");
		diff.newValue = getDiffValue(this, addedi, addede, "added");
		return diff;
	}

	private String getDiffValue(DealCategoryDTO targetDealCategory, List<BaseModel> include, List<BaseModel> exclude,
			String type) {
		if (targetDealCategory == null)
			return null;

		Integer targetAdvanceCountSavedInAuditChangeTable = 0;
		String returnString = "";
		List<BaseModel> newList = new ArrayList<BaseModel>();
		if (include.size() >= targetAdvanceCountSavedInAuditChangeTable
				|| exclude.size() >= targetAdvanceCountSavedInAuditChangeTable) {
			int count = 0;
			if (include.size() > 0) {
				for (BaseModel b : include) {
					count++;
					if (count <= targetAdvanceCountSavedInAuditChangeTable)
						newList.add(b);
					else
						break;
				}
				returnString = "All categories: " + targetDealCategory.selectAll + ", Included(" + type + "): "
						+ StringUtils.formatBaseModel(newList) + ", Excluded(" + type + "): "
						+ StringUtils.formatBaseModel(exclude);
			} else if (exclude.size() > 0) {
				for (BaseModel b : exclude) {
					count++;
					if (count <= targetAdvanceCountSavedInAuditChangeTable)
						newList.add(b);
					else
						break;
				}
				returnString = "All categories: " + targetDealCategory.selectAll + ", Included(" + type + "): "
						+ StringUtils.formatBaseModel(include) + ", Excluded(" + type + "): "
						+ StringUtils.formatBaseModel(newList);
			}

		} else if (include.size() > 0 || exclude.size() > 0) {
			returnString = "All categories: " + targetDealCategory.selectAll + ", Included(" + type + "): "
					+ StringUtils.formatBaseModel(include) + ", Excluded(" + type + "): "
					+ StringUtils.formatBaseModel(exclude);
		}

		return returnString;
	}

	@Override
	public String toString() {
		return "AdvanceTargetingDTO [selectAll=" + selectAll + ", targetDealCategory=" + dealCategory + "]";
	}

}
