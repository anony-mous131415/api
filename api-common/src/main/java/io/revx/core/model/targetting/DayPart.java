package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.exception.NotComparableException;

public class DayPart implements ChangeComparable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private List<Day> daypart;

	public DayPart() {
		daypart = new ArrayList<Day>();
	}

	public DayPart(List<Day> dayPart) {
		this.daypart.addAll(dayPart);
	}

	public List<Day> getDaypart() {
		return daypart;
	}

	public void setDaypart(List<Day> daypart) {
		this.daypart = daypart;
	}

	public void constructDayPartWithFullWeekTargeted() {
		for (int i = 0; i < 7; i++) {
			Day day = new Day();
			day.setDay(i);
			day.setAllHoursForADay();
			daypart.add(day);
		}
	}

	public boolean isFullWeekTargetted(DayPart daypart) {
		if (daypart == null || daypart.getDaypart() == null || daypart.getDaypart().isEmpty()) {
			return true;
		}

		if (daypart.getDaypart() != null && daypart.getDaypart().size() == 7) {
			for (Day day : daypart.getDaypart()) {
				if (day.getHours().size() == 24) {
					continue;
				} else {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public void cleanUp() {
		for (int i = 0; i < 7; i++) {
			daypart.get(i).cleanUp();
		}
		daypart.clear();
	}

	@Override
	public Difference compareTo(ChangeComparable o) throws NotComparableException {
		Difference diff = new Difference();
		diff.different = false;

		if (isFullWeekTargetted((DayPart) o)) {
			o = new DayPart();
		}
		
		if(isFullWeekTargetted(this)) {
			daypart = new ArrayList<Day>();
		}

//		if (o == null)
//			diff.different = true;
//		else 
		if (!(o instanceof DayPart))
			throw new NotComparableException("argument not of type DayPart, cannot be compared");

		List<Day> common = new ArrayList<Day>();
		if (daypart != null)
			common.addAll(daypart);
		List<Day> removed = new ArrayList<Day>();
		if (o != null && ((DayPart) o).daypart != null)
			removed.addAll(((DayPart) o).daypart);
		List<Day> added = new ArrayList<Day>();
		if (daypart != null)
			added.addAll(daypart);

		common.retainAll(removed);
		added.removeAll(common);
		removed.removeAll(common);

		if (!diff.different && (added.size() > 0 || removed.size() > 0))
			diff.different = true;

		diff.oldValue = getDiffValue((DayPart) o, removed, "removed");
		diff.newValue = getDiffValue(this, added, "added");
		return diff;
	}

	private String getDiffValue(DayPart dayPart, List<Day> list, String type) {
		if (dayPart == null)
			return null;
		
		if(isFullWeekTargetted(dayPart)) {
			return "All days and all hours";
		}
		
		return "Days(" + type + "): " + list;
	}

}
