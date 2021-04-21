package io.revx.api.reportbuilder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.revx.api.reportbuilder.redshift.ReportBuilderImpl;
import io.revx.core.enums.reporting.Interval;
import io.revx.core.enums.reporting.TableName;
import io.revx.core.exception.QueryBuilderException;
import io.revx.core.model.reporting.DurationModel;
import io.revx.core.model.reporting.ReportProperty;
import io.revx.core.model.reporting.ReportingRequest;

public class ReportBuilderImplTest {

    ReportBuilderImpl builder = new ReportBuilderImpl();

    @Test
    public void testRSTableForGivenDurationAndInterval() throws QueryBuilderException {

        TableName table;
        ReportingRequest reportingRequest = new ReportingRequest();

        LocalDateTime now = LocalDateTime.now();
        // Test today or yesterday
        long today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
                .toEpochSecond(ZoneOffset.UTC);
        long yesterday = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0).minusDays(1)
                .toEpochSecond(ZoneOffset.UTC);
        DurationModel todayModel = new DurationModel(today, null);
        DurationModel yesterdayModel = new DurationModel(yesterday, null);

        table = builder.getTableToQuery(todayModel, Interval.none, reportingRequest);
        assertEquals(TableName.HOURLY, table);

        table = builder.getTableToQuery(yesterdayModel, Interval.none, reportingRequest);
        assertEquals(TableName.HOURLY, table);

        long last7DaysStart = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0).minusDays(7)
                .toEpochSecond(ZoneOffset.UTC);
        long last7DaysEnd = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
                .toEpochSecond(ZoneOffset.UTC);
        DurationModel last7Days = new DurationModel(last7DaysStart, last7DaysEnd);
        table = builder.getTableToQuery(last7Days, Interval.none, reportingRequest);
        assertEquals(TableName.HOURLY, table);
        table = builder.getTableToQuery(last7Days, Interval.hourly, reportingRequest);
        assertEquals(TableName.HOURLY, table);

        long last15DaysStart = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0).minusDays(15)
                .toEpochSecond(ZoneOffset.UTC);
        long last15DaysEnd = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
                .toEpochSecond(ZoneOffset.UTC);
        DurationModel last15Days = new DurationModel(last15DaysStart, last15DaysEnd);
        table = builder.getTableToQuery(last15Days, Interval.none, reportingRequest);
        assertEquals(TableName.HOURLY, table);
        table = builder.getTableToQuery(last15Days, Interval.hourly, reportingRequest);
        assertEquals(TableName.HOURLY, table);
        table = builder.getTableToQuery(last15Days, Interval.daily, reportingRequest);
        assertEquals(TableName.DAILY, table);
        table = builder.getTableToQuery(last15Days, Interval.weekly, reportingRequest);
        assertEquals(TableName.WEEKLY, table);

        long lastMonthStart = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0).minusMonths(1)
                .toEpochSecond(ZoneOffset.UTC);
        long lastMonthEnd = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        DurationModel lastMonth = new DurationModel(lastMonthStart, lastMonthEnd);
        table = builder.getTableToQuery(lastMonth, Interval.none, reportingRequest);
        assertEquals(TableName.DAILY, table);
        table = builder.getTableToQuery(lastMonth, Interval.hourly, reportingRequest);
        assertEquals(TableName.HOURLY, table);
        table = builder.getTableToQuery(lastMonth, Interval.daily, reportingRequest);
        assertEquals(TableName.DAILY, table);
        table = builder.getTableToQuery(lastMonth, Interval.weekly, reportingRequest);
        assertEquals(TableName.WEEKLY, table);
        table = builder.getTableToQuery(lastMonth, Interval.monthly, reportingRequest);
        assertEquals(TableName.MONTHLY, table);

        long last3MonthsStart = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
                .minusMonths(3).toEpochSecond(ZoneOffset.UTC);
        long last3MonthsEnd = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
                .toEpochSecond(ZoneOffset.UTC);
        DurationModel last3Months = new DurationModel(last3MonthsStart, last3MonthsEnd);
        table = builder.getTableToQuery(last3Months, Interval.none, reportingRequest);
        assertEquals(TableName.DAILY, table);
        table = builder.getTableToQuery(last3Months, Interval.hourly, reportingRequest);
        assertEquals(TableName.HOURLY, table);
        table = builder.getTableToQuery(last3Months, Interval.daily, reportingRequest);
        assertEquals(TableName.DAILY, table);
        table = builder.getTableToQuery(last3Months, Interval.weekly, reportingRequest);
        assertEquals(TableName.WEEKLY, table);
        table = builder.getTableToQuery(last3Months, Interval.monthly, reportingRequest);
        assertEquals(TableName.MONTHLY, table);
    }

    @Test
    public void testProperty() {

        List<String> selects = new ArrayList<>();
        List<String> outerSelects = new ArrayList<>();
        List<String> joins = new ArrayList<>();
        List<String> groupBys = new ArrayList<>();

        Map<String, ReportProperty> properties;
        try {
            properties = ReportProperty.getProperties("rtb.json");
            builder.setReportProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.addPropertyToList("impressions", false, false, selects, outerSelects, joins, groupBys);
        assertImpressionsList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("clicks", false, false, selects, outerSelects, joins, groupBys);
        assertClicksList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("invalid_clicks", false, false, selects, outerSelects, joins, groupBys);
        assertInvalidClicksList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("imp_installs", false, false, selects, outerSelects, joins, groupBys);
        assertImpInstallsList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("click_installs", false, false, selects, outerSelects, joins, groupBys);
        assertClickInstallsList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("total_install", false, false, selects, outerSelects, joins, groupBys);
        assertTotalInstallList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("conversions", false, false, selects, outerSelects, joins, groupBys);
        assertConversionsList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("conversions_view", false, false, selects, outerSelects, joins, groupBys);
        assertConversionsViewList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("conversions_click", false, false, selects, outerSelects, joins, groupBys);
        assertConversionsClickList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("revenue_in_licensee_currency", false, false, selects, outerSelects, joins, groupBys);
        assertRevenueInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("revenue_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertRevenueInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("roi_in_licensee_currency", false, false, selects, outerSelects, joins, groupBys);
        assertRoiInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("roi_in_advertiser_currency", false, false, selects, outerSelects, joins, groupBys);
        assertRoiInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("spend_in_licensee_currency", false, false, selects, outerSelects, joins, groupBys);
        assertSpendInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("spend_in_advertiser_currency", false, false, selects, outerSelects, joins, groupBys);
        assertSpendInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("margin_in_licensee_currency", false, false, selects, outerSelects, joins, groupBys);
        assertMarginInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("margin_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertMarginInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("ctr", false, false, selects, outerSelects, joins, groupBys);
        assertCTRList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("ctc", false, false, selects, outerSelects, joins, groupBys);
        assertCTCList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("cpi", false, false, selects, outerSelects, joins, groupBys);
        assertCPIList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("iti", false, false, selects, outerSelects, joins, groupBys);
        assertITIList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("conv_rate", false, false, selects, outerSelects, joins, groupBys);
        assertConvRateList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("cvr", false, false, selects, outerSelects, joins, groupBys);
        assertCVRList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("advertiser_ecpm_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertAdvertiserECPMInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("advertiser_ecpm_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertAdvertiserECPMInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("bid_price", false, false, selects, outerSelects, joins, groupBys);
        assertBidPriceList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("bid_price_currency", false, false, selects, outerSelects, joins, groupBys);
        assertBidPriceCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("imp_per_conv", false, false, selects, outerSelects, joins, groupBys);
        assertImpPerConvList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpm_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPMInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpm_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPMInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpc_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPCInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpc_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPCInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpa_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPAInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("publisher_ecpa_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertPublisherECPAInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("txn_amount_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertTxnAmountInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("txn_amount_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertTxnAmountInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("click_txn_amount_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertClickTxnAmountInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("click_txn_amount_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertClickTxnAmountInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("view_txn_amount_in_licensee_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertViewTxnAmountInLicenseeCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("view_txn_amount_in_advertiser_currency", false, false, selects, outerSelects, joins,
                groupBys);
        assertViewTxnAmountInAdvertiserCurrencyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("creative_offer_type", true, false, selects, outerSelects, joins, groupBys);
        assertCreativeOfferTypeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("advertiser_pricing", true, false, selects, outerSelects, joins, groupBys);
        assertAdvertiserPricingList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("country", true, false, selects, outerSelects, joins, groupBys);
        assertCountryList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("state", true, false, selects, outerSelects, joins, groupBys);
        assertStateList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("city", true, false, selects, outerSelects, joins, groupBys);
        assertCityList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("licensee", true, false, selects, outerSelects, joins, groupBys);
        assertLicenseeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("advertiser", true, false, selects, outerSelects, joins, groupBys);
        assertAdvertiserList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("aggregator", true, false, selects, outerSelects, joins, groupBys);
        assertAggregatorList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("campaign", true, false, selects, outerSelects, joins, groupBys);
        assertCampaignList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("strategy", true, false, selects, outerSelects, joins, groupBys);
        assertStrategyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("site", true, false, selects, outerSelects, joins, groupBys);
        assertSiteList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("app_rating", true, false, selects, outerSelects, joins, groupBys);
        assertAppRatingList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("app_store_certified", true, false, selects, outerSelects, joins, groupBys);
        assertAppStoreCertifiedList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("app_categories", true, false, selects, outerSelects, joins, groupBys);
        assertAppCategoriesList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("creative", true, false, selects, outerSelects, joins, groupBys);
        assertCreativeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("creative_size", true, false, selects, outerSelects, joins, groupBys);
        assertCreativeSizeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("media_type", true, false, selects, outerSelects, joins, groupBys);
        assertMediaTypeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("bid_strategy", true, false, selects, outerSelects, joins, groupBys);
        assertBidStrategyList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("optimization_type", true, false, selects, outerSelects, joins, groupBys);
        assertOptimizationTypeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("position", true, false, selects, outerSelects, joins, groupBys);
        assertPositionList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("source_type", true, false, selects, outerSelects, joins, groupBys);
        assertSourceTypeList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("os", true, false, selects, outerSelects, joins, groupBys);
        assertOSList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("device_brand", true, false, selects, outerSelects, joins, groupBys);
        assertDeviceBrandList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);

        builder.addPropertyToList("device_model", true, false, selects, outerSelects, joins, groupBys);
        assertDeviceModelList(selects, outerSelects, joins, groupBys);
        resetLists(selects, outerSelects, joins, groupBys);
    }

    private void assertLists(List<String> expSelects, List<String> selects, List<String> expOuterSelects,
                             List<String> outerSelects, List<String> expJoins, List<String> actJoins, List<String> expGroupBys,
                             List<String> groupBys) {
        assertArrayEquals(expSelects.toArray(), selects.toArray());
        assertArrayEquals(expOuterSelects.toArray(), outerSelects.toArray());
        assertArrayEquals(expJoins.toArray(), actJoins.toArray());
        assertArrayEquals(expGroupBys.toArray(), groupBys.toArray());
    }

    private void assertDeviceModelList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_dm_id AS user_dm_id"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("dmm.dm_name AS device_model"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.devicemodelmaster dmm ON A.user_dm_id = dmm.dm_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_dm_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertDeviceBrandList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_db_id AS user_db_id"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("dbm.db_name AS device_brand"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.devicebrandmaster dbm ON A.user_db_id = dbm.db_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_db_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertOSList(List<String> selects, List<String> outerSelects, List<String> joins,
                              List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_os_id AS user_os_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("osm.os_name AS os_name", "A.user_os_id AS user_os_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.osmaster osm ON A.user_os_id = osm.os_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_os_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertSourceTypeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                      List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("source_type AS source_type"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("st.st_source_name AS source_type"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.sourcetype st ON A.source_type = st.st_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("source_type"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPositionList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("fold_position AS fold_position"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("crp.crp_name AS position"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.creativeposition crp ON A.fold_position = crp.crp_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("fold_position"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertOptimizationTypeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                            List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("is_learning AS optimization_type"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("CASE WHEN optimization_type = 0 THEN 'NONLEARNING' ELSE 'LEARNING' END AS optimization_type"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("optimization_type"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertBidStrategyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("bid_strategy_type_id AS bid_strategy_type_id"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("bid_strategy_type_id AS bid_strategy"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("bid_strategy_type_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertMediaTypeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                     List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("media_type AS media_type"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("cmt.crm_name AS media_type"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.creativemediatype cmt ON A.media_type = cmt.crm_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("media_type"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCreativeSizeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                        List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Arrays.asList("nvl(creative_width, 0) AS creative_width", "nvl(creative_height,0) AS creative_height"));
        List<String> expOuterSelects = new ArrayList<>(Arrays.asList("nvl(A.creative_width, 0) AS creative_width",
                "nvl(A.creative_height,0) AS creative_height",
                "A.creative_width||'X'||A.creative_height AS creative_size"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("creative_width, creative_height"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCreativeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("creative_id AS creative_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("A.creative_id AS creative_id", "cr.cr_name AS cr_name"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.creative cr ON A.creative_id = cr.cr_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("creative_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAppCategoriesList(List<String> selects, List<String> outerSelects, List<String> joins,
                                         List<String> groupBys) {
        List<String> expSelects = new ArrayList<>();
        List<String> expOuterSelects = new ArrayList<>(
                Collections.singletonList("rtbse.rse_categories_names AS app_categories"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("app_categories"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAppStoreCertifiedList(List<String> selects, List<String> outerSelects, List<String> joins,
                                             List<String> groupBys) {
        List<String> expSelects = new ArrayList<>();
        List<String> expOuterSelects = new ArrayList<>(
                Collections.singletonList("rtbse.rse_app_store_certified AS app_store_certified"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("app_store_certified"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAppRatingList(List<String> selects, List<String> outerSelects, List<String> joins,
                                     List<String> groupBys) {
        List<String> expSelects = new ArrayList<>();
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("rtbse.rse_app_rating AS app_rating"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("app_rating"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertSiteList(List<String> selects, List<String> outerSelects, List<String> joins,
                                List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("site_id AS site_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("rtbse.rse_package_name AS rse_package_name", "A.site_id AS site_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.rtbsiteextended rtbse ON A.site_id = rtbse.rse_site_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("site_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertStrategyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("advertiser_li_id AS advertiser_li_id"));
        List<String> expOuterSelects = new ArrayList<>(Arrays.asList("A.advertiser_li_id AS advertiser_li_id",
                "advli.al_is_active AS al_is_active", "advli.al_li_name AS al_li_name",
                "advli.al_pricing_id AS al_pricing_id", "advli.al_type AS al_type"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.advertiserlineitem advli ON A.advertiser_li_id = advli.al_id "));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("advertiser_li_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCampaignList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("advertiser_io_id AS advertiser_io_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("A.advertiser_io_id AS advertiser_io_id", "advio.ai_io_name AS ai_io_name",
                        "advio.ai_sales_manager_id AS ai_sales_manager_id", "advio.ai_is_active AS ai_is_active"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.advertiserio advio ON A.advertiser_io_id = advio.ai_id "));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("advertiser_io_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAggregatorList(List<String> selects, List<String> outerSelects, List<String> joins,
                                      List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("aggregator_id AS aggregator_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("rtba.rag_display_name AS rag_display_name", "A.aggregator_id AS aggregator_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.rtbaggregator rtba ON A.aggregator_id = rtba.rag_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("aggregator_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAdvertiserList(List<String> selects, List<String> outerSelects, List<String> joins,
                                      List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("advertiser_id AS advertiser_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("A.advertiser_id AS advertiser_id", "adv.av_advertiser_name AS av_advertiser_name"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.advertiser adv ON A.advertiser_id = adv.av_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("advertiser_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertLicenseeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("licensee_id as licensee_id"));
        List<String> expOuterSelects = new ArrayList<>(Arrays.asList("A.licensee_id as licensee_id",
                "ln.ln_company_name as ln_name", "ln.ln_currency_id as ln_currency_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.licensee ln ON A.licensee_id = ln.ln_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("licensee_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCityList(List<String> selects, List<String> outerSelects, List<String> joins,
                                List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_city_id AS user_city_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("city.tm_city_name AS tm_city_name", "A.user_city_id AS user_city_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.citymaster city ON A.user_city_id = city.tm_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_city_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertStateList(List<String> selects, List<String> outerSelects, List<String> joins,
                                 List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_region_id AS user_region_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("state.sm_state_name AS sm_state_name", "A.user_region_id AS user_region_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.statemaster state ON A.user_region_id = state.sm_id "));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_region_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCountryList(List<String> selects, List<String> outerSelects, List<String> joins,
                                   List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("user_country_id AS user_country_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Arrays.asList("country.cm_country_name AS cm_country_name", "A.user_country_id AS user_country_id"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.countrymaster country ON A.user_country_id = country.cm_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("user_country_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAdvertiserPricingList(List<String> selects, List<String> outerSelects, List<String> joins,
                                             List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("advertiser_pricing_type AS advertiser_pricing_type"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("ap.pc_type_name AS advertiser_pricing"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.pricing ap ON A.advertiser_pricing_type = ap.pc_id"));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("advertiser_pricing_type"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCreativeOfferTypeList(List<String> selects, List<String> outerSelects, List<String> joins,
                                             List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("creative_offer_type_id AS creative_offer_type_id"));
        List<String> expOuterSelects = new ArrayList<>(
                Collections.singletonList("ot.ot_offer_type_name AS creative_offer_type"));
        List<String> expJoins = new ArrayList<>(
                Collections.singletonList("LEFT JOIN adx.offertype ot ON A.creative_offer_type_id = ot.ot_id "));
        List<String> expGroupBys = new ArrayList<>(Collections.singletonList("creative_offer_type_id"));

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertViewTxnAmountInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                             List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(imp_conversions)=1 THEN SUM(txn_amount_in_advertiser_currency) WHEN SUM(imp_conversions)=0 THEN 0 ELSE NULL END AS view_txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.view_txn_amount as view_txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertViewTxnAmountInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                           List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(imp_conversions)=1 THEN SUM(txn_amount_in_licensee_currency) WHEN SUM(imp_conversions)=0 THEN 0 ELSE NULL END AS view_txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.view_txn_amount as view_txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertClickTxnAmountInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                              List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(click_conversions) = 1 THEN SUM(txn_amount_in_advertiser_currency) WHEN SUM(click_conversions) = 0 THEN 0 ELSE NULL END AS click_txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.click_txn_amount as click_txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertClickTxnAmountInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                            List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(click_conversions) = 1 THEN SUM(txn_amount_in_licensee_currency) WHEN SUM(click_conversions) = 0 THEN 0 ELSE NULL END AS click_txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.click_txn_amount as click_txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertTxnAmountInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                         List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("SUM(txn_amount_in_advertiser_currency) AS txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.txn_amount AS txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertTxnAmountInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                       List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("SUM(txn_amount_in_licensee_currency) AS txn_amount"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.txn_amount AS txn_amount"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPAInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                             List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(conversion_delivery) = 0 THEN 0 WHEN SUM(conversion_delivery) > 0 THEN SUM(media_cost_in_advertiser_currency) / SUM(conversion_delivery) ELSE NULL END AS publisher_ecpa"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpa AS publisher_ecpa"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPAInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                           List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(conversion_delivery) = 0 THEN 0 WHEN SUM(conversion_delivery) > 0 THEN SUM(media_cost_in_licensee_currency) / SUM(conversion_delivery) ELSE NULL END AS publisher_ecpa"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpa AS publisher_ecpa"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPCInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                             List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(clicks) = 0 THEN 0 WHEN SUM(clicks) > 0 THEN SUM(media_cost_in_advertiser_currency) / SUM(clicks) ELSE NULL END AS publisher_ecpc"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpc AS publisher_ecpc"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPCInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                           List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(clicks) = 0 THEN 0 WHEN SUM(clicks) > 0 THEN SUM(media_cost_in_licensee_currency) / SUM(clicks) ELSE NULL END AS publisher_ecpc"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpc AS publisher_ecpc"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPMInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                             List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(impressions) = 0 THEN 0 WHEN SUM(impressions) > 0 THEN SUM(media_cost_in_advertiser_currency) * 1000 / SUM(impressions) ELSE NULL END AS publisher_ecpm"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpm AS publisher_ecpm"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertPublisherECPMInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                           List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(impressions) = 0 THEN 0 WHEN SUM(impressions) > 0 THEN SUM(media_cost_in_licensee_currency) * 1000 / sum(impressions) ELSE NULL END AS publisher_ecpm"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.publisher_ecpm AS publisher_ecpm"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertImpPerConvList(List<String> selects, List<String> outerSelects, List<String> joins,
                                      List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(impressions) = 0 THEN 0 WHEN (SUM(imp_conversions) + SUM(click_conversions)) > 0 THEN SUM(impressions) / (SUM(imp_conversions)+ SUM(click_conversions)) ELSE NULL END AS imp_per_conv"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.imp_per_conv AS imp_per_conv"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertBidPriceCurrencyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                            List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Arrays.asList("163 AS bid_price_currency_id",
                "'USD' AS bid_price_currency", "'US Dollar' AS bid_price_currency_name"));
        List<String> expOuterSelects = new ArrayList<>(Arrays.asList(
                "bid_price_currency_id AS bid_price_currency_id", "bid_price_currency AS bid_price_currency",
                "bid_price_currency_name AS bid_price_currency_name"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertBidPriceList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("sum(final_bid_price) AS final_bid_price"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("final_bid_price AS bid_price"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAdvertiserECPMInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                              List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(true_msba_av) = 0 THEN 0 WHEN SUM(impressions) > 0 THEN SUM(true_msba_av) * 1000 / SUM(impressions) ELSE NULL END AS advertiser_ecpm"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.advertiser_ecpm AS advertiser_ecpm"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertAdvertiserECPMInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                            List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(true_msba_l) = 0 THEN 0 WHEN SUM(impressions) > 0 THEN SUM(true_msba_l) * 1000 / SUM(impressions) ELSE NULL END AS advertiser_ecpm"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.advertiser_ecpm AS advertiser_ecpm"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCVRList(List<String> selects, List<String> outerSelects, List<String> joins,
                               List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(impressions) = 0 THEN 0 WHEN SUM(impressions) > 0 THEN (SUM(imp_conversions) + SUM(click_conversions)) * 1000 * 100.0 / SUM(impressions) ELSE NULL END AS cvr"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.cvr AS cvr"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertConvRateList(List<String> selects, List<String> outerSelects, List<String> joins,
                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN (SUM(imp_installs) + SUM(click_installs) = 0) THEN 0 WHEN (SUM(imp_conversions) + SUM(click_conversions)) = 0 THEN 0 WHEN SUM(imp_installs) + SUM(click_installs) > 0 THEN (SUM(imp_conversions) + SUM(click_conversions)) * 1.0 / (SUM(imp_installs) + sum(click_installs)) ELSE NULL END AS conv_rate"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.conv_rate AS conv_rate"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertITIList(List<String> selects, List<String> outerSelects, List<String> joins,
                               List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN (SUM(imp_installs) + SUM(click_installs) = 0) THEN 0 WHEN SUM(impressions) = 0 THEN 0 WHEN SUM(imp_installs) + SUM(click_installs) > 0 THEN SUM(impressions) / (SUM(imp_installs) + SUM(click_installs)) ELSE NULL END AS iti"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.iti AS iti"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCPIList(List<String> selects, List<String> outerSelects, List<String> joins,
                               List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN (SUM(imp_installs) + SUM(click_installs) = 0) THEN 0 WHEN (SUM(imp_installs) + SUM(click_installs) >0) THEN SUM(true_msba_l) / (SUM(imp_installs) + SUM(click_installs)) ELSE NULL END AS cpi"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.cpi AS cpi"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCTCList(List<String> selects, List<String> outerSelects, List<String> joins,
                               List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(clicks) = 0 THEN 0 WHEN SUM(clicks) >0 THEN SUM(click_conversions) * 100.0/SUM(clicks) ELSE NULL END AS ctc"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.ctc AS ctc"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertCTRList(List<String> selects, List<String> outerSelects, List<String> joins,
                               List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(impressions) = 0 THEN 0 WHEN SUM(impressions) >0 THEN SUM(clicks) * 100.0/SUM(impressions) ELSE NULL END AS ctr"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.ctr AS ctr"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertMarginInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                      List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN (SUM(true_msba_av) - SUM(media_cost_in_advertiser_currency * (1+0))) = 0 THEN 0 WHEN SUM(true_msba_av) > 0 THEN (SUM(true_msba_av) - SUM(media_cost_in_advertiser_currency * (1+0))) * 100.0 / SUM(true_msba_av) ELSE NULL END AS margin"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.margin AS margin"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertMarginInLicenseeCurrencyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                                    List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN (SUM(true_msba_l) - SUM(media_cost_in_licensee_currency * (1+0))) = 0 THEN 0 WHEN SUM(true_msba_l) > 0 THEN (SUM(true_msba_l) - SUM(media_cost_in_licensee_currency * (1+0))) * 100.0 / SUM(true_msba_l) ELSE NULL END AS margin"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.margin AS margin"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertSpendInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                     List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("SUM(media_cost_in_advertiser_currency) AS spend"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.spend AS spend"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertSpendInLicenseeCurrencyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                                   List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(media_cost_in_licensee_currency) AS spend"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.spend AS spend"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertRoiInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                                   List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(true_msba_av)=0 THEN 0 WHEN SUM(true_msba_av)>0 THEN SUM(txn_amount_in_advertiser_currency) / SUM(true_msba_av) ELSE NULL END AS roi"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.roi AS roi"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertRoiInLicenseeCurrencyList(List<String> selects, List<String> outerSelects, List<String> joins,
                                                 List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList(
                "CASE WHEN SUM(true_msba_l)=0 THEN 0 WHEN SUM(true_msba_l)>0 THEN SUM(txn_amount_in_licensee_currency) / SUM(true_msba_l) ELSE NULL END AS roi"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.roi AS roi"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertRevenueInAdvertiserCurrencyList(List<String> selects, List<String> outerSelects,
                                                       List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("SUM(true_msba_av) AS revenue_in_advertiser_currency"));
        List<String> expOuterSelects = new ArrayList<>(
                Collections.singletonList("A.revenue_in_advertiser_currency AS revenue"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertRevenueInLicenseeCurrencyList(List<String> selects, List<String> outerSelects,
                                                     List<String> joins, List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("SUM(true_msba_l) AS revenue_in_licensee_currency"));
        List<String> expOuterSelects = new ArrayList<>(
                Collections.singletonList("A.revenue_in_licensee_currency AS revenue"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertConversionsClickList(List<String> selects, List<String> outerSelects, List<String> joins,
                                            List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(click_conversions) AS conversions_click"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.conversions_click AS conversions_click"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertConversionsViewList(List<String> selects, List<String> outerSelects, List<String> joins,
                                           List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(imp_conversions) AS conversions_view"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.conversions_view AS conversions_view"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertConversionsList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("(SUM(imp_conversions) + SUM(click_conversions)) AS conversions"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.conversions AS conversions"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertTotalInstallList(List<String> selects, List<String> outerSelects, List<String> joins,
                                        List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(
                Collections.singletonList("(SUM(imp_installs) + SUM(click_installs)) AS total_install"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.total_install AS total_install"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertClickInstallsList(List<String> selects, List<String> outerSelects, List<String> joins,
                                         List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(click_installs) AS click_installs"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.click_installs AS click_installs"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertImpInstallsList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(imp_installs) AS imp_installs"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.imp_installs AS imp_installs"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertInvalidClicksList(List<String> selects, List<String> outerSelects, List<String> joins,
                                         List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(invalid_clicks) AS invalid_clicks"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.invalid_clicks AS invalid_clicks"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertClicksList(List<String> selects, List<String> outerSelects, List<String> joins,
                                  List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(clicks) as clicks"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.clicks AS clicks"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void assertImpressionsList(List<String> selects, List<String> outerSelects, List<String> joins,
                                       List<String> groupBys) {
        List<String> expSelects = new ArrayList<>(Collections.singletonList("SUM(impressions) AS impressions"));
        List<String> expOuterSelects = new ArrayList<>(Collections.singletonList("A.impressions AS impressions"));
        List<String> expJoins = new ArrayList<>();
        List<String> expGroupBys = new ArrayList<>();

        assertLists(expSelects, selects, expOuterSelects, outerSelects, expJoins, joins, expGroupBys, groupBys);
    }

    private void resetLists(List<String> selects, List<String> outerSelects, List<String> joins,
                            List<String> groupBys) {
        selects.clear();
        outerSelects.clear();
        joins.clear();
        groupBys.clear();
    }
}
