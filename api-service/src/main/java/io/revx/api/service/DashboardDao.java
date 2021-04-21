package io.revx.api.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import io.revx.api.reportbuilder.redshift.BigQueryConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.impl.PostgresQueryBuilder;
import io.revx.querybuilder.objs.FilterComponent;

@Component
public class DashboardDao {

    private static final Logger logger = LogManager.getLogger(DashboardDao.class);

    @Autowired
    BigQueryConnectionUtil bigQueryConnectionUtil;

    @Value("${dashboard.source.dataset:prod}")
    private String sourceDataSet;

    @LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.CHART)
    public List<ChartPerformanceDataMetrics> getDashboardChartData(DashboardRequest requestDTO,
                                                                   Set<FilterComponent> tableFilters, boolean showUU) {
        List<ChartPerformanceDataMetrics> result = null;
        try {
            String queryString = getQueryString(requestDTO, tableFilters, DashboardEntities.chart,
                    DashBoardEntity.HOMEPAGE, showUU);
            logger.info("queryString : {} " , queryString);
            TableResult tableResult = bigQueryConnectionUtil.fetchQueryResult(sourceDataSet,queryString);
            result = populateChartPerformanceMetrics(tableResult,requestDTO.getGroupBy(), showUU);
            logger.info("queryString Result : {} " , result);
        } catch (Exception e) {
            logger.info("dashboard Query failed", e);
        }
        return result;
    }

    @LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.WIDGET)
    public List<PerformanceDataMetrics> getDashboardChartWidgetData(DashboardRequest requestDTO,
                                                                    Set<FilterComponent> tableFilters, boolean hideUU) {
        List<PerformanceDataMetrics> result = null;
        try {
            String queryString = getQueryString(requestDTO, tableFilters, DashboardEntities.widget,
                    DashBoardEntity.HOMEPAGE, hideUU);
            logger.info("queryString : {}" , queryString);
            TableResult tableResult = bigQueryConnectionUtil.fetchQueryResult(sourceDataSet,queryString);
            result = populateListPerformanceMetricsList(tableResult, hideUU);
            logger.info("queryString Result : {} " , result);
        } catch (Exception e) {
            logger.info("dashboard Query failed", e);
        }
        return result;
    }


    @LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.LIST)
    public List<ListPerformanceDataMetrics> getDashboardListData(DashBoardEntity entity,
                     DashboardRequest requestDTO, Set<FilterComponent> tableFilters, boolean showUU) {
        List<ListPerformanceDataMetrics> result = null;
        try {
            String queryString = getQueryString(requestDTO, tableFilters, DashboardEntities.list, entity,showUU);
            logger.info("queryString : {}" , queryString);
            TableResult tableResult = bigQueryConnectionUtil.fetchQueryResult(sourceDataSet,queryString);
            result = populateListPerformanceMetrics(entity, tableResult, showUU);
            logger.info("queryString Result : {}" , result);
        } catch (Exception e) {
            logger.info("dashboard Query failed", e);
        }
        return result;
    }

    public String getQueryString(DashboardRequest requestDTO, Set<FilterComponent> tableFilters,
                                 DashboardEntities dashboardEntity, DashBoardEntity entity, boolean showUU) throws Exception {
        String queryString = null;
        try {
            String groupBy = requestDTO.getGroupBy();
            if (dashboardEntity == DashboardEntities.list) {
                groupBy = entity.getColumn();
                requestDTO.setGroupBy(groupBy);
            }
            logger.debug(" groupBy {}  :: {} ", groupBy, entity);
            PostgresQueryBuilder mysql = new PostgresQueryBuilder();
            queryString = mysql.buildQuery((long) requestDTO.getDuration().getStartTimeStamp(),
                    (long) requestDTO.getDuration().getEndTimeStamp(), tableFilters,
                    dashboardEntity == DashboardEntities.widget ? GroupBy.NONE : GroupBy.fromString(groupBy), showUU);

        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            throw new Exception("error occurred while creating perf query string");
        }

        return queryString;
    }

    private List<ListPerformanceDataMetrics> populateListPerformanceMetrics(DashBoardEntity entity,
                                                                            TableResult result, boolean showUU) {
        List<ListPerformanceDataMetrics> dataMetrics = new ArrayList<>();
        for (FieldValueList fieldValues : result.getValues()) {
            ListPerformanceDataMetrics metrics = new ListPerformanceDataMetrics();
            int index = 0;
            switch (entity) {
                case CAMPAIGN:
                    metrics.setCampaignid(new BigInteger(fieldValues.get(index++).getStringValue()));
                    break;
                case ADVERTISER:
                    metrics.setAdvertiserid(new BigInteger(fieldValues.get(index++).getStringValue()));
                    break;
                case STRATEGY:
                    metrics.setStrategyid(new BigInteger(fieldValues.get(index++).getStringValue()));
                    break;
                default:
                    index++;
            }
            populatePerformanceMetrics(fieldValues, metrics, index, showUU);
            dataMetrics.add(metrics);
        }
        return dataMetrics;
    }

    private List<ChartPerformanceDataMetrics> populateChartPerformanceMetrics(TableResult result, String groupBy,
                                                                              boolean showUU) {
        List<ChartPerformanceDataMetrics> dataMetrics = new ArrayList<>();
        for (FieldValueList fieldValues : result.getValues()) {
            ChartPerformanceDataMetrics metrics = new ChartPerformanceDataMetrics();
            int index = 0;
            if (groupBy.equalsIgnoreCase("day")) {
                metrics.setDay(fieldValues.get(index++).getNumericValue());
            }
            if (groupBy.equalsIgnoreCase("hour")) {
                metrics.setHour(fieldValues.get(index++).getNumericValue());
            }
            populatePerformanceMetrics(fieldValues, metrics, index, showUU);
            dataMetrics.add(metrics);
        }
        return dataMetrics;
    }


    private void populatePerformanceMetrics(FieldValueList fieldValues, PerformanceDataMetrics metrics, int index,
                                            boolean showUU) {
        FieldValue decimal = fieldValues.get(index++);
        metrics.setImpressions(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setClicks(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setViewconversions(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setClickconversions(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        decimal = fieldValues.get(index++);
        metrics.setViewinstalls(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setClickinstalls(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        decimal = fieldValues.get(index++);
        metrics.setRevenueinadvertisercurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setRevenueinlicenseecurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setRevenueinplatformcurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        decimal = fieldValues.get(index++);
        metrics.setCostinadvertisercurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setCostinlicenseecurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setCostinplatformcurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        decimal = fieldValues.get(index++);
        metrics.setTxnamountinadvertisercurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setTxnamountinlicenseecurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setTxnamountinplatformcurrency(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        decimal = fieldValues.get(index++);
        metrics.setBidsplaced(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        decimal = fieldValues.get(index++);
        metrics.setInvalidclicks(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);

        if (showUU) {
            decimal = fieldValues.get(index++);
            metrics.setImpressionuniqusers(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
            decimal = fieldValues.get(index++);
            metrics.setEligibleuniqusers(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
            decimal = fieldValues.get(index);
            metrics.setEligiblebids(!decimal.isNull() ? decimal.getNumericValue() : BigDecimal.ZERO);
        }
    }

    private List<PerformanceDataMetrics> populateListPerformanceMetricsList(TableResult tableResult, boolean showUU) {
        List<PerformanceDataMetrics> dataMetrics = new ArrayList<>();
        for (FieldValueList fieldValues : tableResult.getValues()) {
            PerformanceDataMetrics metrics = new PerformanceDataMetrics();
            populatePerformanceMetrics(fieldValues, metrics, 0, showUU);
            dataMetrics.add(metrics);
        }
        return dataMetrics;
    }

}
