package io.revx.api.es.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.revx.core.constant.Constants;

public class ElasticSearchTerm {

	private String serachInIdOrName;
	private List<Long> licensies;
	private List<Long> advertisers;
	private List<Long> campaigns;
	private List<Long> strategies;
	private List<Long> aggregators;
	private Map<String, Set<String>> filters;
	private List<String> sortList;
	private int pageNumber = 0;
	private int pageSize = 999;

	public ElasticSearchTerm() {
		// Adding This mapping As HardCoded For New Creative
		filters = new HashMap<>();
		filters.put("refactor", new HashSet<>());
		filters.get("refactor").add("true");
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getSortList() {
		return sortList;
	}

	public void setSortList(List<String> sortList) {
		this.sortList = sortList;
	}

	public String getSerachInIdOrName() {
		return serachInIdOrName;
	}

	public void setSerachInIdOrName(String serachInIdOrName) {
		this.serachInIdOrName = serachInIdOrName;
	}

	public void setLicenseeId(Long id) {
		if (licensies == null)
			licensies = new ArrayList<Long>();
		licensies.add(id);
	}

	public List<Long> getLicensies() {
		return licensies;
	}

	public void setLicensies(List<Long> licensies) {
		this.licensies = licensies;
	}

	public List<Long> getAdvertisers() {
		return advertisers;
	}

	public void setAdvertisers(Long advertiser) {
		if (advertisers == null)
			advertisers = new ArrayList<Long>();
		advertisers.add(advertiser);
	}

	public void setAdvertisers(List<Long> advertisers) {
		this.advertisers = advertisers;
	}

	public List<Long> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Long campaign) {
		if (campaigns == null)
			campaigns = new ArrayList<Long>();
		campaigns.add(campaign);
	}

	public void setCampaigns(List<Long> campaigns) {
		this.campaigns = campaigns;
	}

	public List<Long> getStrategies() {
		return strategies;
	}

	public void setStrategies(List<Long> strategies) {
		this.strategies = strategies;
	}

	public void setStrategies(Long strategy) {
		if (strategies == null)
			strategies = new ArrayList<Long>();
		strategies.add(strategy);
	}

	public Map<String, Set<String>> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Set<String>> filters) {
		this.filters = filters;
	}

	public void setFilters(String key, String value) {
		if (this.filters == null)
			this.filters = new HashMap<>();
		if (this.filters.get(key) == null)
			this.filters.put(key, new HashSet<>());
		this.filters.get(key).add(value);
	}
	
	public List<Long> getAggregators() {
		return aggregators;
	}

	public void setAggregators(List<Long> aggregators) {
		this.aggregators = aggregators;
	}
	
	public void setAggregators(String csvAggregators) {
		if (this.aggregators == null)
			this.aggregators = new ArrayList<Long>();
		if (!csvAggregators.isEmpty()) {
			String[] parts = csvAggregators.split("" + Constants.COMMA_SEPARATOR);
			for(String part : parts) {
				this.aggregators.add(Long.parseLong(part));
			}
		}
	}

	public void setAggregators(Long aggregator) {
		if (this.aggregators == null)
			this.aggregators = new ArrayList<Long>();
		this.aggregators.add(aggregator);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ElasticSearchTerm [serachInIdOrName=").append(serachInIdOrName).append(", licensies=")
				.append(licensies).append(", advertisers=").append(advertisers).append(", campaigns=").append(campaigns)
				.append(", strategies=").append(strategies).append(", aggregators=").append(aggregators)
				.append(", filters=").append(filters).append(", sortList=").append(sortList).append(", pageNumber=")
				.append(pageNumber).append(", pageSize=").append(pageSize).append("]");
		return builder.toString();
	}

}
