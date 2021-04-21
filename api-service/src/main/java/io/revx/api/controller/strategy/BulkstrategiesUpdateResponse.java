package io.revx.api.controller.strategy;

import java.io.Serializable;
import java.util.ArrayList;

public class BulkstrategiesUpdateResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<StrategyUpdateResponse> strategiesUpdated;
	
	private ArrayList<StrategyUpdateResponse> strategiesFailedToUpdate;
	
//	private ArrayList<StrategyUpdateResponse> strategiesWithNoChange;
	
	private Integer totalNumberOfProcessedStrategies;

	public ArrayList<StrategyUpdateResponse> getStrategiesUpdated() {
		return strategiesUpdated;
	}

	public void setStrategiesUpdated(ArrayList<StrategyUpdateResponse> strategiesUpdated) {
		this.strategiesUpdated = strategiesUpdated;
	}

	public ArrayList<StrategyUpdateResponse> getStrategiesFailedToUpdate() {
		return strategiesFailedToUpdate;
	}

	public void setStrategiesFailedToUpdate(ArrayList<StrategyUpdateResponse> strategiesFailedToUpdate) {
		this.strategiesFailedToUpdate = strategiesFailedToUpdate;
	}

//	public ArrayList<StrategyUpdateResponse> getStrategiesWithNoChange() {
//		return strategiesWithNoChange;
//	}
//
//	public void setStrategiesWithNoChange(ArrayList<StrategyUpdateResponse> strategiesWithNoChange) {
//		this.strategiesWithNoChange = strategiesWithNoChange;
//	}

	public Integer getTotalNumberOfProcessedStrategies() {
		return totalNumberOfProcessedStrategies;
	}

	public void setTotalNumberOfProcessedStrategies(Integer totalNumberOfProcessedStrategies) {
		this.totalNumberOfProcessedStrategies = totalNumberOfProcessedStrategies;
	}

	public BulkstrategiesUpdateResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

}
