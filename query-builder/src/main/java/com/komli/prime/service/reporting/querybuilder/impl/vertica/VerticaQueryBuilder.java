package com.komli.prime.service.reporting.querybuilder.impl.vertica;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.cache.VerticaCache;
import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.export.vertica.VerticaExportUtil;
import com.komli.prime.service.reporting.jmx.SLRerportingData;
import com.komli.prime.service.reporting.pojo.InputParameters;
import com.komli.prime.service.reporting.pojo.InputParameters.Interval;
import com.komli.prime.service.reporting.pojo.InputParameters.SLOutput;
import com.komli.prime.service.reporting.pojo.QueryResult;
import com.komli.prime.service.reporting.querybuilder.api.Query;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.helper.VerticaQueryInputParameters;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.helper.VerticaQueryStrings;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.helper.VerticaRawQueriesBuilder;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.helper.VerticaTempQueriesBuilder;
import com.komli.prime.service.reporting.utils.JDBCConnectionUtils;
import com.komli.prime.service.reporting.utils.JDBCConnectionUtils.DatabaseName;
import com.komli.prime.service.reporting.utils.ReportingUtil;
import com.komli.prime.service.reporting.utils.SLResultSetHandlers;

public class VerticaQueryBuilder implements Query
{

    private static final Logger logger = LoggerFactory.getLogger(VerticaQueryBuilder.class);

    private String sessionId;

    private String logSession;

    private boolean toBeCached = false;

    private InputParameters inputObj;

    private VerticaQueryInputParameters inputParams;

    private VerticaQueryStrings queryStrings;

    private String reportId;

    private boolean alreadyBuilt = false;

    // public List<String>

    public String tablesToQuery;

    public String tempTable;

    private DataSource ds;

    private String adhocQuery;

    public VerticaQueryBuilder(InputParameters inputObj, String sessionId)
    {
        this.inputObj = inputObj;
        this.sessionId = sessionId;
        this.logSession = "Session :[" + sessionId + "]";
        reportId = inputObj.getReportId();
        toBeCached = inputObj.isToBeCached();
        if (Interval.METADATA == inputObj.getInterval()) {
            toBeCached = false;
        }
        if (StringUtils.isBlank(reportId)) {
            reportId = sessionId;
        }
    }

    @Override
    public void buildQuery() throws ReportGeneratingException
    {
        if (SLOutput.ADHOC == inputObj.getOutput()) {
            adhocQuery = inputObj.getAdhocQuery();
            alreadyBuilt = true;
            return;
        }
        logger.trace(logSession + " Entering buildQuery.");
        if (validateParameters()) {
            build();
        }
        alreadyBuilt = true;
        logger.trace(logSession + "Exiting buildQuery.");
    }

    private void build() throws QueryBuilderException
    {
        if (!alreadyBuilt) {
            inputParams = new VerticaQueryInputParameters(inputObj, sessionId);
            queryStrings = new VerticaQueryStrings(inputParams, sessionId);
        }
    }

    private boolean validateParameters() throws QueryBuilderException
    {
        if (inputObj.getInterval() == null) {
            throw new QueryBuilderException(ReportingError.UNDEFINED_INTERVAL);
        }
        if (inputObj.getReportType() == null) {
            throw new QueryBuilderException(ReportingError.UNDEFINED_REPORT_TYPE);
        }
        return true;
    }

    @Override
    public QueryResult executeQuery() throws ReportGeneratingException
    {
        logger.info(logSession + "Entering ExecuteQuery.");
        int retry = 1;
        QueryResult qr = new QueryResult();
        while (retry < 4) {
            ds = JDBCConnectionUtils.getInstance().getDataSource(DatabaseName.VERTICA);
            ds.setDefaultAutoCommit(true);
            boolean isConnect = ds.isTestOnBorrow();
            logger.info("Check Connection Established : " + isConnect);
            if (isConnect) {
                if (retry > 1) {
                    logger.info("Retrying redshift connection to get the data : " + retry);
                }
                retry = 4;
                qr.setReportId(reportId);
                logger.info("Report ID : " + reportId);
                logger.info("Session ID : " + sessionId);
                logger.info("tobeCached : " + toBeCached);
                if (!toBeCached) {
                    // If not to be cached, all records will be fetched.
                    qr = fetchAllRecords();
                } else {
                    if (!reportId.equalsIgnoreCase(sessionId)) {
                        int rows = queryAndGetTempTableQuerySize();
                        if (rows > 0) {
                            qr = fetchFromTempTable();
                            qr.setTotalRows(rows);
                        } else if (rows == -1) {
                            reportId = sessionId;
                            // fetchFromTempTableQueryStr =
                            // fetchFromTempTableQueryStr.replace(sessionId, reportId);
                            // createTempTableAndFetch();
                        } else {
                            logger.warn(logSession + "No records found.");
                            qr = new QueryResult();
                            qr.setReportId(reportId);
                            qr.setTempTable(tempTable);
                            qr.setTotalRows(0);
                            return qr;
                        }
                    }
                    if (reportId.equalsIgnoreCase(sessionId)) {
                        qr = createTempTableAndFetch();
                    }
                }
            } else {
                retry++;
            }
        }

        logger.trace(logSession + " Exiting ExecuteQuery.");
        return qr;
    }

    @Override
    public QueryResult executeAdhocQuery() throws ReportGeneratingException
    {
        logger.trace(logSession + "Entering executeAdhocQuery.");
        ds = JDBCConnectionUtils.getInstance().getDataSource(DatabaseName.VERTICA);
        QueryResult qr = new QueryResult();
        qr.setReportId(reportId);
        String query = adhocQuery;
        qr = fetchAdhocQueryOutput(query);
        logger.trace(logSession + "Exiting executeAdhocQuery.");
        return qr;
    }

    @Override
    public QueryResult exportQuery() throws ReportGeneratingException
    {
        logger.trace(logSession + "Entering ExecuteQuery.");
        QueryResult qr = new QueryResult();
        qr.setReportId(reportId);
        qr.setCsvFileName(ReportingUtil.getUniqueCSVFile(reportId));
        exportToCSV();
        logger.trace(logSession + "Exiting ExecuteQuery.");
        return qr;
    }

    private void exportToCSV() throws ReportGeneratingException
    {
        logger.trace(logSession + "Entering exportToCSV.");
        ds = JDBCConnectionUtils.getInstance().getDataSource(DatabaseName.VERTICA);

        try {
            logger.info(reportId + ": Entering exportToCSV: " + sessionId);
            if (reportId.equalsIgnoreCase(sessionId)) {
                // reportId is not provided. hence fetching directly from raw
                // tables.
                VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(queryStrings, inputParams);
                String queryCSVStr = rawQueryBuilder.getRawCSVQuery();
                VerticaExportUtil.generateCSVFile(queryCSVStr, reportId);
            } else {
                int rows = queryAndGetTempTableQuerySize();
                // logger.info("************queryAndGetTempTableQuerySize Rows size: "+rows+" ,reportId: "+reportId+" ,sessionId: "+sessionId);
                if (rows > 0) {
                    VerticaTempQueriesBuilder tempQueryBuilder =
                        new VerticaTempQueriesBuilder(queryStrings, inputParams);
                    VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(queryStrings, inputParams);
                    String createTableQueryStr = rawQueryBuilder.getRawCreateTempTableCsvQuery(reportId);
                    String fetchFromTempTableCSVQueryStr = tempQueryBuilder.getTempTableCSVQuery(reportId);
                    VerticaExportUtil.generateTempCSVFile(fetchFromTempTableCSVQueryStr, reportId, createTableQueryStr);
                    /*
                     * VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder( queryStrings,
                     * inputParams); String queryCSVStr = rawQueryBuilder.getRawCSVQuery();
                     * VerticaExportUtil.generateCSVFile(queryCSVStr, reportId);
                     */

                } else if (rows == -1) {
                    VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(queryStrings, inputParams);
                    String queryCSVStr = rawQueryBuilder.getRawCSVQuery();
                    VerticaExportUtil.generateCSVFile(queryCSVStr, reportId);
                }

                /*
                 * if (rows > 0 or rows==-1) { VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(
                 * queryStrings, inputParams); String queryCSVStr = rawQueryBuilder.getRawCSVQuery();
                 * VerticaExportUtil.generateCSVFile(queryCSVStr, reportId); }
                 */

            }
        } catch (ReportGeneratingException e) {
            logger.warn(logSession + "CSV generation failed");
            throw e;
        }
        logger.trace(logSession + "Exiting exportToCSV.");
        return;
    }

    private QueryResult createTempTableAndFetch() throws ReportGeneratingException
    {
        QueryResult qr = null;
        try {
            createTempTable(true);
        } catch (Exception e) {
            logger.error(logSession + " Create Temp Table Error : " + e.getMessage(), e);
            throw new ReportGeneratingException(ReportingError.INTERNAL_ERROR);
        }
        int rows = queryAndGetTempTableQuerySize();
        qr = new QueryResult();
        if (rows > 0) {
            qr = fetchFromTempTable();
            qr.setTotalRows(rows);
        } else {
            qr.setReportId(reportId);
            qr.setTempTable(tempTable);
            qr.setTotalRows(0);
        }
        return qr;
    }

    private int queryAndGetTempTableQuerySize() throws ReportGeneratingException
    {
        logger.info(logSession + "Entering queryAndGetTempTableQuerySize.");
        QueryRunner qRunner = new QueryRunner(ds);
        int rows = 0;
        VerticaTempQueriesBuilder tempQueryBuilder = new VerticaTempQueriesBuilder(queryStrings, inputParams);
        String tempTableSizeQuery = tempQueryBuilder.getTempTableSizeQuery(reportId);
        try {
            logger.debug(logSession + "Querying TempTableSizeQuery:[" + tempTableSizeQuery + "]");
            SLRerportingData.numTempTableSizeQueries++;
            rows = qRunner.query(tempTableSizeQuery, SLResultSetHandlers.COUNTROWSHANDLER);
            logger.info(logSession + "Exiting queryAndGetTempTableQuerySize.");
        } catch (SQLException e) {
            logger.info("Error in queryAndGetTempTableQuerySize : " + logSession + e.getMessage(), e);
            SLRerportingData.numTempTableQueriesFailed++;
            rows = -1;
        }
        return rows;
    }

    private void createTempTable(boolean retry) throws ReportGeneratingException
    {
        logger.info(logSession + "Entering createTempTable. retry: " + retry);
        QueryRunner qRunner = new QueryRunner(ds);
        VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(queryStrings, inputParams);
        String createTableQueryStr = rawQueryBuilder.getRawCreateTempTableQuery();

        try {
            logger.debug(logSession + "Querying CreateTableQuery:[" + createTableQueryStr + "]");
            SLRerportingData.numTempTableCreationQueries++;
            int rowsupdated = qRunner.update(createTableQueryStr);
            logger.info(logSession + " Rows updated while creating temp table : " + rowsupdated);
            logger.info(logSession + " Exiting createTempTable.");
        } catch (SQLException e) {
            logger.error(logSession + e.getMessage(), e);
            SLRerportingData.numRawTableQueriesFailed++;
            throw new ReportGeneratingException(ReportingError.SQL_EXCEPTION);
        } catch (Exception e) {
            logger.error(logSession + " Create Temp Table Error : " + e.getMessage(), e);
            throw new ReportGeneratingException(ReportingError.INTERNAL_ERROR);
        }
        if (retry) {
            String tempTable = ReportingUtil.getUniqueTempTable(inputParams.getSessionId());
            String selectTempQuery = "select count(1) from "+ tempTable + " limit 1";
            try {
                logger.info(logSession + "Validating temp table '" + tempTable +
                            "' is created or not, By executing '" + selectTempQuery + "'...");
                int rows = qRunner.query(selectTempQuery, SLResultSetHandlers.COUNTROWSHANDLER);
                logger.info(logSession + "Successfully validated tempTable '" + tempTable + "'.");
            } catch (SQLException e) {
                logger.info(logSession + "Failed to validate tempTable '" + tempTable + "'.");
                logger.error(logSession + e.getMessage(), e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                logger.info("Retrying create temp table...");
                createTempTable(false);
            }
        }
    }

    private QueryResult fetchFromTempTable() throws ReportGeneratingException
    {
        logger.info(logSession + "Entering fetchFromTempTable.");
        QueryRunner qRunner = new QueryRunner(ds);
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        VerticaTempQueriesBuilder tempQueryBuilder = new VerticaTempQueriesBuilder(queryStrings, inputParams);
        String fetchFromTempTableQueryStr = tempQueryBuilder.getTempTableFetchQuery(reportId);
        try {
            logger.debug(logSession + "Querying FetchFromTempTableQuery:[" + fetchFromTempTableQueryStr + "]");
            SLRerportingData.numTempTableFetchQueries++;
            records = qRunner.query(fetchFromTempTableQueryStr, new MapListHandler());
        } catch (SQLException e) {
            logger.error(logSession + e.getMessage(), e);
            SLRerportingData.numTempTableQueriesFailed++;
            throw new ReportGeneratingException(ReportingError.SQL_EXCEPTION);
        }
        QueryResult qr = new QueryResult();
        if (records != null && records.size() > 0) {
            qr.setResult(records);
            qr.setReportId(reportId);
            qr.setTempTable(tempTable);
            logger.info(logSession + " Exiting fetchFromTempTable having fetched {} records.", records.size());
            return qr;
        } else {
            logger.warn(logSession + " No records found.");
            qr.setReportId(reportId);
            qr.setTempTable(tempTable);
            return qr;
        }
    }

    private QueryResult fetchAllRecords() throws ReportGeneratingException
    {
        logger.info(logSession + "Entering fetchAllRecords.");
        String queryStr = new String();
        QueryRunner qRunner = new QueryRunner(ds);
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        VerticaRawQueriesBuilder rawQueryBuilder = new VerticaRawQueriesBuilder(queryStrings, inputParams);
        queryStr = rawQueryBuilder.getRawFetchQuery();

        try {
            logger.debug(logSession + "Querying fetchRawTable:[" + queryStr + "]");
            SLRerportingData.numRawTableFetchQueries++;
            records = qRunner.query(queryStr, new MapListHandler());

        } catch (SQLException e) {
            SLRerportingData.numRawTableQueriesFailed++;
            logger.error(logSession + e.getMessage(), e);
            throw new ReportGeneratingException(ReportingError.SQL_EXCEPTION);
        }
        QueryResult qr = new QueryResult();
        if (records != null && records.size() > 0) {
            qr.setResult(records);
            qr.setReportId(reportId);
            qr.setTotalRows(records.size());
            // qr.setTempTable(tempTable);
            logger.info(logSession + "Exiting fetchAllRecords.");
            return qr;
        } else {
            logger.info(logSession + "No records found.");
            qr.setReportId(reportId);
            return qr;
        }
    }

    private QueryResult fetchAdhocQueryOutput(String queryStr) throws ReportGeneratingException
    {
        logger.trace(logSession + "Entering fetchAdhocQueryOutput.");
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        QueryRunner qRunner = new QueryRunner(ds);

        try {
            logger.info(logSession + "Querying fetchAdhocQueryOutput:[" + queryStr + "]");
            SLRerportingData.numAdhocQueries++;
            records = qRunner.query(queryStr, new MapListHandler());

        } catch (SQLException e) {
            logger.error(logSession + e.getMessage(), e);
            throw new ReportGeneratingException(ReportingError.SQL_EXCEPTION);
        }
        QueryResult qr = new QueryResult();
        if (records != null && records.size() > 0) {
            qr.setResult(records);
            qr.setReportId(reportId);
            qr.setTotalRows(records.size());
            // qr.setTempTable(tempTable);
            logger.trace(logSession + "Exiting fetchAdhocQueryOutput.");
            return qr;
        } else {
            logger.warn(logSession + "No records found.");
            qr.setReportId(reportId);
            return qr;
        }
    }

    @Override
    public boolean checkConfiguration() throws ReportGeneratingException
    {
        if (!VerticaCache.cachePopulated) {
            throw new QueryBuilderException(ReportingError.NO_VERTICA_CACHE);
        }
        return true;
    }
}
