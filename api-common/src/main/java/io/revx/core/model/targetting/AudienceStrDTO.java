package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class AudienceStrDTO implements ChangeComparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6222408338317141269L;

	public boolean customSegmentTargeting;

	public List<BaseModel> targetedSegments;

	public String targetedSegmentsOperator;

	public List<BaseModel> blockedSegments;

	public String blockedSegmentsOperator;

	public AudienceStrDTO() {
		customSegmentTargeting = false;
		targetedSegments = new ArrayList<>();
		blockedSegments = new ArrayList<>();
		targetedSegmentsOperator = null;
		blockedSegmentsOperator = null;
	}

	public boolean isCustomSegmentTargeting() {
		return customSegmentTargeting;
	}

	public void setCustomSegmentTargeting(boolean customSegmentTargeting) {
		this.customSegmentTargeting = customSegmentTargeting;
	}

	public List<BaseModel> getTargetedSegments() {
		return targetedSegments;
	}

	public void setTargetedSegments(List<BaseModel> targetedSegments) {
		this.targetedSegments = targetedSegments;
	}

	public String getTargetedSegmentsOperator() {
		return targetedSegmentsOperator;
	}

	public void setTargetedSegmentsOperator(String targetedSegmentsOperator) {
		this.targetedSegmentsOperator = targetedSegmentsOperator;
	}

	public List<BaseModel> getBlockedSegments() {
		return blockedSegments;
	}

	public void setBlockedSegments(List<BaseModel> blockedSegments) {
		this.blockedSegments = blockedSegments;
	}

	public String getBlockedSegmentsOperator() {
		return blockedSegmentsOperator;
	}

	public void setBlockedSegmentsOperator(String blockedSegmentsOperator) {
		this.blockedSegmentsOperator = blockedSegmentsOperator;
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.setDifferent(false);

		if (o == null)
			diff.different = true;
		else if (!(o instanceof AudienceStrDTO))
			throw new NotComparableException("argument not of type AudienceDTO, cannot be compared");

		if (!diff.different && customSegmentTargeting != ((AudienceStrDTO) o).customSegmentTargeting)
			diff.different = true;

		if (!diff.different && targetedSegmentsOperator != null
				&& !(targetedSegmentsOperator.equals(((AudienceStrDTO) o).targetedSegmentsOperator)))
			diff.different = true;

		List<BaseModel> commonts = new ArrayList<BaseModel>();
		if (targetedSegments != null)
			commonts.addAll(targetedSegments);
		List<BaseModel> removedts = new ArrayList<BaseModel>();
		if (o != null && ((AudienceStrDTO) o).targetedSegments != null)
			removedts.addAll(((AudienceStrDTO) o).targetedSegments);
		List<BaseModel> addedts = new ArrayList<BaseModel>();
		if (targetedSegments != null)
			addedts.addAll(targetedSegments);

		commonts.retainAll(removedts);
		addedts.removeAll(commonts);
		removedts.removeAll(commonts);

		if (!diff.different && (addedts.size() > 0 || removedts.size() > 0))
			diff.different = true;

		List<BaseModel> commonbs = new ArrayList<BaseModel>();
		if (blockedSegments != null)
			commonbs.addAll(blockedSegments);
		List<BaseModel> removedbs = new ArrayList<BaseModel>();
		if (o != null && ((AudienceStrDTO) o).blockedSegments != null)
			removedbs.addAll(((AudienceStrDTO) o).blockedSegments);
		List<BaseModel> addedbs = new ArrayList<BaseModel>();
		if (blockedSegments != null)
			addedbs.addAll(blockedSegments);

		commonbs.retainAll(removedbs);
		addedbs.removeAll(commonbs);
		removedbs.removeAll(commonbs);

		if (!diff.different && (addedbs.size() > 0 || removedbs.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((AudienceStrDTO) o, removedts, ((AudienceStrDTO) o).targetedSegmentsOperator,
				removedbs, "removed");
		diff.newValue = getDiffValue(this, addedts, targetedSegmentsOperator, addedbs, "added");
		return diff;
	}

	private String getDiffValue(AudienceStrDTO audienceDTO, List<BaseModel> ts, String tsOp, List<BaseModel> bs,
			String type) {
		if (audienceDTO == null)
			return null;
		return "Custom  targeting: " + audienceDTO.customSegmentTargeting + ", Targeted segments(" + type
				+ "): " + StringUtils.formatBaseModel(ts) + ", Targeted seggments operator = " + tsOp
				+ ", Blocked segments(" + type + "): " + StringUtils.formatBaseModel(bs);
	}

	@Override
	public String toString() {
		return "AudienceStrDTO [customSegmentTargeting=" + customSegmentTargeting + ", targetedSegments="
				+ targetedSegments + ", targetedSegmentsOperator=" + targetedSegmentsOperator + ", blockedSegments="
				+ blockedSegments + ", blockedSegmentsOperator=" + blockedSegmentsOperator + "]";
	}

//  @Override
//  public String toString() {
//    StringBuilder builder = new StringBuilder();
//    builder.append("AudienceStrDTO [customSegmentTargeting=").append(customSegmentTargeting)
//        .append(", targetedSegments=").append(targetedSegments)
//        .append(", targetedSegmentsOperator=").append(targetedSegmentsOperator)
//        .append(", blockedSegments=").append(blockedSegments).append(", blockedSegmentsOperator=")
//        .append(blockedSegmentsOperator).append("]");
//    return builder.toString();
//  }

}
