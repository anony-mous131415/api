package com.komli.prime.service.reporting.jmx;

public class SLRerportingData implements SLReportingMBean {

	public static long numRawTableFetchQueries;
	public static long numRawTableQueriesFailed;
	public static long numTempTableCreationQueries;
	public static long numTempTableSizeQueries;
	public static long numTempTableFetchQueries;
	public static long numTempTableQueriesFailed;
	public static long numSessionCreations;
	public static long numSessionTimedOuts;
	public static long numBusinessQueries;
	public static long numAdhocQueries;

	@Override
	public long getTotalRawTableFetchQueries() {
		return numRawTableFetchQueries;
	}

	@Override
	public long getTotalRawTableQueriesFailed() {
		return numRawTableQueriesFailed;
	}

	@Override
	public long getTotalTempTableCreationQueries() {
		return numTempTableCreationQueries;
	}

	@Override
	public long getTotalTempTableSizeQueries() {
		return numTempTableSizeQueries;
	}

	@Override
	public long getTotalTempTableFetchQueries() {
		return numTempTableFetchQueries;
	}

	@Override
	public long getTotalTempTableQueriesFailed() {
		return numTempTableQueriesFailed;
	}

	@Override
	public long getTotalSessionCreations() {
		return numSessionCreations;
	}

	@Override
	public long getTotalSessionTimedOuts() {
		return numSessionTimedOuts;
	}

	@Override
	public long getTotalBusinessQueries() {
		return numSessionCreations;
	}

	@Override
	public long getTotalAdhocQueries() {
		return numAdhocQueries;
	}

}
