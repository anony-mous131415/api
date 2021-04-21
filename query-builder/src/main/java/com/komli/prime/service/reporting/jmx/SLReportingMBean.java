package com.komli.prime.service.reporting.jmx;

public interface SLReportingMBean {

	public long getTotalRawTableFetchQueries();
	
	public long getTotalRawTableQueriesFailed();
	
    public long getTotalTempTableCreationQueries();
    
    public long getTotalTempTableSizeQueries();
    
    public long getTotalTempTableFetchQueries();
    
    public long getTotalTempTableQueriesFailed();
    
    public long getTotalSessionCreations();
    
    public long getTotalSessionTimedOuts();
    
    public long getTotalBusinessQueries();
    
    public long getTotalAdhocQueries();
 
}
