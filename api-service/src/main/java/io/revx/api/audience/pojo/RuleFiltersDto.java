package io.revx.api.audience.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RuleFiltersDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	List<RuleFilterDto> filters;

	public RuleFiltersDto() {
		filters = new ArrayList<RuleFilterDto>();
	}

	public List<RuleFilterDto> getFilters() {
		return filters;
	}

	public void addFilter(RuleFilterDto filter) {
		this.filters.add(filter);
	}
	
	public void addFilters(List<RuleFilterDto> filters) {
		this.filters.addAll(filters);
	}
}
