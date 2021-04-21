package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.utils.StringUtils;

public class TargetGeoDTO implements ChangeComparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean customGeoTargeting;

	public TargetingObject country;
	public TargetingObject state;
	public TargetingObject city;

	public TargetGeoDTO() {
		customGeoTargeting = false;
		country = new TargetingObject();
		state = new TargetingObject();
		city = new TargetingObject();
	}

	public boolean isCustomGeoTargeting() {
		if (TargetingObject.isEmptyTargetting(country) && TargetingObject.isEmptyTargetting(state)
				&& TargetingObject.isEmptyTargetting(city))
			return false;
		return true;
	}

	public void setCustomGeoTargeting(boolean customGeoTargeting) {
		this.customGeoTargeting = customGeoTargeting;
	}

	public TargetingObject getCountry() {
		return country;
	}

	public void setCountry(TargetingObject country) {
		this.country = country;
	}

	public TargetingObject getState() {
		return state;
	}

	public void setState(TargetingObject state) {
		this.state = state;
	}

	public TargetingObject getCity() {
		return city;
	}

	public void setCity(TargetingObject city) {
		this.city = city;
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (o == null)
			diff.different = true;
		else if (!(o instanceof TargetGeoDTO))
			throw new NotComparableException("argument not of type TargetDeoDTO, cannot be compared");

		if (!diff.different && customGeoTargeting != ((TargetGeoDTO) o).customGeoTargeting)
			diff.different = true;

		List<BaseModel> cityCommon = new ArrayList<BaseModel>();
		List<BaseModel> cityAdded = new ArrayList<BaseModel>();
		List<BaseModel> cityRemoved = new ArrayList<BaseModel>();
		if (city.targetList != null)
			cityCommon.addAll(city.targetList);
		if (o != null && ((TargetGeoDTO) o).city.targetList != null)
			cityRemoved.addAll(((TargetGeoDTO) o).city.targetList.stream()
					.map(target -> new BaseModel(target.getId(), target.getName())).collect(Collectors.toList()));
		if (city.targetList != null)
			cityAdded.addAll(city.targetList);
		cityCommon.retainAll(cityRemoved);
		cityAdded.removeAll(cityCommon);
		cityRemoved.removeAll(cityCommon);
		if (!diff.different && (cityAdded.size() > 0 || cityRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> cityBCommon = new ArrayList<BaseModel>();
		List<BaseModel> cityBAdded = new ArrayList<BaseModel>();
		List<BaseModel> cityBRemoved = new ArrayList<BaseModel>();
		if (city.blockedList != null)
			cityBCommon.addAll(city.blockedList);
		if (o != null && ((TargetGeoDTO) o).city.blockedList != null)
			cityBRemoved.addAll(((TargetGeoDTO) o).city.blockedList.stream()
					.map(block -> new BaseModel(block.getId(), block.getName())).collect(Collectors.toList()));
		if (city.blockedList != null)
			cityBAdded.addAll(city.blockedList);
		cityBCommon.retainAll(cityBRemoved);
		cityBAdded.removeAll(cityBCommon);
		cityBRemoved.removeAll(cityBCommon);
		if (!diff.different && (cityBAdded.size() > 0 || cityBRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> stateCommon = new ArrayList<BaseModel>();
		List<BaseModel> stateAdded = new ArrayList<BaseModel>();
		List<BaseModel> stateRemoved = new ArrayList<BaseModel>();
		if (state.targetList != null)
			stateCommon.addAll(state.targetList);
		if (o != null && ((TargetGeoDTO) o).state.targetList != null)
			stateRemoved.addAll(((TargetGeoDTO) o).state.targetList.stream()
					.map(target -> new BaseModel(target.getId(), target.getName())).collect(Collectors.toList()));
		if (state.targetList != null)
			stateAdded.addAll(state.targetList);
		stateCommon.retainAll(stateRemoved);
		stateAdded.removeAll(stateCommon);
		stateRemoved.removeAll(stateCommon);
		if (!diff.different && (stateAdded.size() > 0 || stateRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> stateBCommon = new ArrayList<BaseModel>();
		List<BaseModel> stateBAdded = new ArrayList<BaseModel>();
		List<BaseModel> stateBRemoved = new ArrayList<BaseModel>();
		if (state.blockedList != null)
			stateBCommon.addAll(state.blockedList);
		if (o != null && ((TargetGeoDTO) o).state.blockedList != null)
			stateBRemoved.addAll(((TargetGeoDTO) o).state.blockedList.stream()
					.map(block -> new BaseModel(block.getId(), block.getName())).collect(Collectors.toList()));
		if (state.blockedList != null)
			stateBAdded.addAll(state.blockedList);
		stateBCommon.retainAll(stateBRemoved);
		stateBAdded.removeAll(stateBCommon);
		stateBRemoved.removeAll(stateBCommon);
		if (!diff.different && (stateBAdded.size() > 0 || stateBRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> countryCommon = new ArrayList<BaseModel>();
		List<BaseModel> countryAdded = new ArrayList<BaseModel>();
		List<BaseModel> countryRemoved = new ArrayList<BaseModel>();
		if (country.targetList != null)
			countryCommon.addAll(country.targetList);
		if (o != null && ((TargetGeoDTO) o).country.targetList != null)
			countryRemoved.addAll(((TargetGeoDTO) o).country.targetList.stream()
					.map(target -> new BaseModel(target.getId(), target.getName())).collect(Collectors.toList()));
		if (country.targetList != null)
			countryAdded.addAll(country.targetList);
		countryCommon.retainAll(countryRemoved);
		countryAdded.removeAll(countryCommon);
		countryRemoved.removeAll(countryCommon);
		if (!diff.different && (countryAdded.size() > 0 || countryRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> countryBCommon = new ArrayList<BaseModel>();
		List<BaseModel> countryBAdded = new ArrayList<BaseModel>();
		List<BaseModel> countryBRemoved = new ArrayList<BaseModel>();
		if (country.blockedList != null)
			countryBCommon.addAll(country.blockedList);
		if (o != null && ((TargetGeoDTO) o).country.blockedList != null) {
			countryBRemoved.addAll(((TargetGeoDTO) o).country.blockedList.stream()
					.map(block -> new BaseModel(block.getId(), block.getName())).collect(Collectors.toList()));
		}
		if (country.blockedList != null)
			countryBAdded.addAll(country.blockedList);
		countryBCommon.retainAll(countryBRemoved);
		countryBAdded.removeAll(countryBCommon);
		countryBRemoved.removeAll(countryBCommon);
		if (!diff.different && (countryBAdded.size() > 0 || countryBRemoved.size() > 0))
			diff.different = true;

		String countryOld = getDiffValue("Country", ((TargetGeoDTO) o).country);
		String countryNew = getDiffValue("Country", this.country);
		String stateOld = getDiffValue("State", ((TargetGeoDTO) o).state);
		String stateNew = getDiffValue("State", this.state);
		String cityOld = getDiffValue("City", ((TargetGeoDTO) o).city);
		String cityNew = getDiffValue("City", this.city);
		diff.oldValue = countryOld + "\n" + stateOld + "\n" + cityOld;
		diff.newValue = countryNew + "\n" + stateNew + "\n" + cityNew;
		return diff;
	}

	private String getDiffValue(String geoType, TargetingObject targetingObject) {
		if (targetingObject == null)
			return null;
		return geoType + "[Targeted: "
				+ StringUtils.formatBaseModel(targetingObject.targetList) + ", Blocked: "
				+ StringUtils.formatBaseModel(targetingObject.blockedList) + "] ";
	}

	public void test(ChangeComparable o, Difference diff) {
		List<BaseModel> stateCommon = new ArrayList<>();
		List<BaseModel> stateAdded = new ArrayList<>();
		List<BaseModel> stateRemoved = new ArrayList<>();
		if (state.targetList != null)
			stateCommon.addAll(state.targetList);
		if (o != null && ((TargetGeoDTO) o).state.targetList != null)
			stateRemoved.addAll(((TargetGeoDTO) o).state.targetList);
		if (state.targetList != null)
			stateAdded.addAll(state.targetList);
		stateCommon.retainAll(stateRemoved);
		stateAdded.removeAll(stateCommon);
		stateRemoved.removeAll(stateCommon);
		if (!diff.different && (stateAdded.size() > 0 || stateRemoved.size() > 0))
			diff.different = true;

		List<BaseModel> stateBCommon = new ArrayList<>();
		List<BaseModel> stateBAdded = new ArrayList<>();
		List<BaseModel> stateBRemoved = new ArrayList<>();
		if (state.blockedList != null)
			stateBCommon.addAll(state.blockedList);
		if (o != null && ((TargetGeoDTO) o).state.blockedList != null)
			stateBRemoved.addAll(((TargetGeoDTO) o).state.blockedList);
		if (state.blockedList != null)
			stateBAdded.addAll(state.blockedList);
		stateBCommon.retainAll(stateBRemoved);
		stateBAdded.removeAll(stateBCommon);
		stateBRemoved.removeAll(stateBCommon);
		if (!diff.different && (stateBAdded.size() > 0 || stateBRemoved.size() > 0))
			diff.different = true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TargetGeoDTO [customGeoTargeting=").append(customGeoTargeting).append(", country=")
				.append(country).append(", state=").append(state).append(", city=").append(city).append("]");
		return builder.toString();
	}

}
