package io.revx.api.reportbuilder.redshift;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.BigQueryException;
import io.revx.api.config.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class BigQueryConnectionUtil {

    private static final Logger logger = LogManager.getLogger(BigQueryConnectionUtil.class);

    @Autowired
    private ApplicationProperties properties;

    private static final String DATA_FORMAT = "CSV";

    private static BigQuery bigquery;

    private synchronized BigQuery getInstance() {
        if (bigquery == null) {
            try {
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream(properties.getGcsCredentialsLocation()));
                bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
            } catch (IOException e) {
                logger.info("Failed to read google credential file :{}",properties.getGcsCredentialsLocation());
                throw new BigQueryException(1001,"Failed to read google credential file");
            }
        }
        return bigquery;
    }

    public TableResult fetchQueryResult(String dataSet, String queryString) throws InterruptedException {

        BigQuery bigQueryInstance = getInstance();
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(queryString)
                .setDefaultDataset(dataSet)
                .build();

        Job queryJob = createJob(bigQueryInstance, queryConfig);
        return queryJob.getQueryResults();
    }

    public void saveResultToTable(String sourceDataSet, String queryString, String tableName) throws InterruptedException {

        BigQuery bigQueryInstance = getInstance();
        TableId destinationTable = TableId.of(properties.getReportingDestinationDataSet(), tableName);

        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(queryString)
                        .setDefaultDataset(sourceDataSet)
                        .setDestinationTable(destinationTable)
                        .build();

        Job queryJob = createJob(bigQueryInstance, queryConfig);
        queryJob.waitFor();
    }

    public void exportReportToBucket(String tableName, String csvFileName) throws InterruptedException{

        TableId tableId = TableId.of(properties.getGccProjectId(), properties.getReportingDestinationDataSet(), tableName);
        Table table = bigquery.getTable(tableId);

        Job job = table.extract(DATA_FORMAT, properties.getReportingExportBucket() + csvFileName);
        Job completedJob = job.waitFor();
        if (completedJob == null || completedJob.getStatus().getError() != null) {
            throw new BigQueryException(500,"Failed to export Bigquery Table in CSV format");
        }
        logger.info("Table export successful. Check in GCS bucket for the {} file.",csvFileName);
    }

    private Job createJob(BigQuery bigquery, QueryJobConfiguration queryConfig) {

        JobId jobId = JobId.of(UUID.randomUUID().toString());

        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        if (queryJob == null ) {
            throw new BigQueryException(500,"Failed to create/execute Bigquery Job");
        }
        if ( queryJob.getStatus().getError() != null ) {
            throw new BigQueryException(500,queryJob.getStatus().getError().getMessage());
        }

        return queryJob;
    }

    public Long setExpirationOnTableAndFetchRowCount(String tempTableName) {
        Table table = null;
        try {
            BigQuery bigQueryInstance = getInstance();
            Long expirationTime = Instant.now()
                    .plusSeconds(Long.parseLong(properties.getTempTableTimeToLive())).toEpochMilli();
            table = bigQueryInstance.getTable(properties.getReportingDestinationDataSet(), tempTableName);
            bigQueryInstance.update(table.toBuilder().setExpirationTime(expirationTime).build());
            logger.info("Table expiration updated successfully : {}", tempTableName);
        } catch (BigQueryException e) {
            logger.info("Table expiration was not updated : {}",e.getMessage());
        }
        return table != null ? table.getNumRows().longValue() : null;
    }

    public Long checkIfTemporaryTableExists(String tableName) {
        BigQuery bigQueryInstance = getInstance();
        TableId tableId = TableId.of(properties.getReportingDestinationDataSet(), tableName);
        Table table = bigQueryInstance.getTable(tableId);
        if (table == null) {
            logger.debug("Temporary table : {} does not exist", tableName);
            return null;
        }
        long dataRows = table.getNumRows().longValue();
        logger.debug("Temporary table : {} exists with data rows {}", tableName, dataRows);
        return dataRows;
    }
}
