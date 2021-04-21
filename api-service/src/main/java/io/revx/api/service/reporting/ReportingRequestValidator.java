package io.revx.api.service.reporting;

import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.reportbuilder.ReportBuilderUtil;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.Entity;
import io.revx.core.enums.reporting.Interval;
import io.revx.core.enums.reporting.OperatorModel;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.reporting.DurationModel;
import io.revx.core.model.reporting.FilterModel;
import io.revx.core.model.reporting.ReportProperty;
import io.revx.core.model.reporting.ReportingRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportingRequestValidator {

	private static final Logger logger = LogManager.getLogger(ReportingRequestValidator.class);

	@Autowired
	private LoginUserDetailsService loginService;

	@Autowired
	private ReportBuilderUtil reportBuilderUtil;

	private static String LICENSEE = "licensee";
	private static String ADVERTISER = "advertiser";

	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING
			+ GraphiteConstants.REPORTING_VALIDATION)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING + GraphiteConstants.REPORTING_VALIDATION)
	public boolean validateRequest(ReportingRequest reportingRequest, boolean isExport) throws ValidationException {
		// remove invalid metrics if RO user
		checkInvalidMetricsForROUser(reportingRequest);

		// logic to validate the request

		validateEntity(reportingRequest);
		if (reportingRequest.getEntityName().equals(ApiConstant.RTB_ENTITY)) {
			validateCurrencyOf(reportingRequest);
		}
		validateDuration(reportingRequest);
		if(!isExport)
			validatePagination(reportingRequest);
		validateInterval(reportingRequest);
		validateColumns(reportingRequest);
		validateFilters(reportingRequest);
		validateGroupBy(reportingRequest);

		// check and add user access filters
		addUserAccessFilters(reportingRequest);

		return true;
	}

	/**
	 * 1. Add current active licensee filter 2. Validate existing advertiser filter
	 * against what the user is authorized
	 * 
	 * @param reportingRequest
	 * @throws ValidationException
	 */
	private void addUserAccessFilters(ReportingRequest reportingRequest) throws ValidationException {

		List<FilterModel> filters = reportingRequest.getFilters();
		if (filters == null || filters.isEmpty()) {
			filters = new ArrayList<FilterModel>();
		}

		if (loginService != null) {
			// check licensee details
			checkAndAddLicenseeFilter(filters);

			// check advertiser details
			checkAndAddAdvertiserFilter(filters);

			reportingRequest.setFilters(filters);
		} else {
			logger.debug("Unable to get user authorization");
			throw new ValidationException(ErrorCode.INVALID_AUTHORIZATION);
		}
	}

	/**
	 * Function to add default licensee filter to the FilterModel object based on
	 * the user authorization
	 * 
	 * @param filters - the list of FilterModel objects present in the request
	 *                object.
	 * @throws ValidationException
	 */
	private void checkAndAddLicenseeFilter(List<FilterModel> filters) throws ValidationException {
		if (loginService.getSelectedLicensee() != null && loginService.getSelectedLicensee().getId() != null) {
			Long licenseeId = loginService.getSelectedLicensee().getId();
			FilterModel licenseeFilter = new FilterModel(LICENSEE, OperatorModel.eq, licenseeId);

			filters.add(licenseeFilter);
		} else {
			logger.debug("Unable to get the current active licensee");
			throw new ValidationException(ErrorCode.INVALID_LICENSEE);
		}
	}

	/**
	 * This method does 2 things 1. If the FilterModel object in the
	 * reportingRequest contains a advertiser filter, then check if the values
	 * provided are valid for the user. 2. If the FilterModel object in the
	 * reportingRequest does not contain advertiser filter, then add default
	 * advertiser filter based on the user authorization.
	 * 
	 * @param filters - the list of FilterModel objects present in the request
	 *                object.
	 * @throws ValidationException
	 */
	private void checkAndAddAdvertiserFilter(List<FilterModel> filters) throws ValidationException {
		List<Long> advertiserList = loginService.getAdvertisers();
		// check advertiser filter
		if (advertiserList != null && !advertiserList.isEmpty()) {
			FilterModel advFilter = null;
			for (FilterModel fil : filters) {
				if (fil.getColumn().equalsIgnoreCase(ADVERTISER)) {
					advFilter = fil;
					break;
				}
			}
			if (advFilter != null) {
				// advertiser filter exists in the request
				// check if user has permission to access all advIds
				List<Long> validAdvIds = new ArrayList<Long>();
				List<Long> advIds = (ArrayList<Long>) advFilter.getValue();
				for (Long advId : advIds) {
					if (advertiserList.contains(advId)) {
						validAdvIds.add(advId);
					} else {
						logger.debug("Do not have permission to access advertiser details | AdvId : {}", advId);
						throw new ValidationException(ErrorCode.ADVERTISER_ACCESS_DENIED);
					}
				}
				advFilter.setValue(validAdvIds);
			} else {
				// add default advertiser filter based on user access
				advFilter = new FilterModel(ADVERTISER, OperatorModel.in, advertiserList);
				filters.add(advFilter);
			}
		}
	}

	private void checkInvalidMetricsForROUser(ReportingRequest reportingRequest) {

		boolean isROUser = loginService.isReadOnlyUser();
		if (isROUser) {
			// TODO: read these values from prop file.
			List<String> colToBeRemoved = new ArrayList<String>(Arrays.asList("spend", "margin",
					"publisher_ecpm", "publisher_ecpc", "publisher_ecpa"));
			List<String> columns = reportingRequest.getColumns();
			List<String> filteredColumns = new ArrayList<String>();
			for (String col : columns) {
				if (!colToBeRemoved.contains(col)) {
					filteredColumns.add(col);
				}
			}
			reportingRequest.setColumns(filteredColumns);
		}
	}

	private void validateEntity(ReportingRequest reportingRequest) throws ValidationException {
		String entity = reportingRequest.getEntityName();
		List<String> validEntities = new ArrayList<String>();
		for (Entity ent : Entity.values()) {
			validEntities.add(ent.getEntity());
		}
		if (!validEntities.contains(entity)) {
			throw new ValidationException(ErrorCode.INVALID_ENTITY);
		}
	}

	private void validateColumns(ReportingRequest reportingRequest) throws ValidationException {
		List<String> columns = reportingRequest.getColumns();

		if (columns == null || columns.isEmpty()) {
			throw new ValidationException(ErrorCode.COLUMN_LIST_EMPTY);
		}

		Map<String, ReportProperty> properties = reportingRequest.getProperties();
		columns = reportBuilderUtil.modifyColumnsBasedOnCurrency(columns, reportingRequest.getCurrency_of()); //doesnot need this for conversion reports
		for (String column : columns) {
			if (!properties.containsKey(column)) {
				logger.debug("COLUMN UNKNOWN : {}", column);
				throw new ValidationException(ErrorCode.COLUMN_UNKNOWN);
			}
		}
	}

	private void validateFilters(ReportingRequest reportingRequest) throws ValidationException {
		List<FilterModel> filters = reportingRequest.getFilters();

		Map<String, ReportProperty> properties = reportingRequest.getProperties();
		if (filters != null && !filters.isEmpty()) {
			List<String> filterColumns = filters.stream().map(FilterModel::getColumn)
					.collect(Collectors.toList());
			for (String col : filterColumns) {
				if (!properties.containsKey(col)) {
					throw new ValidationException(ErrorCode.FILTER_COLUMN_UNKNOWN);
				}
			}
		}
	}

	private void validateGroupBy(ReportingRequest reportingRequest) throws ValidationException {
		List<String> groupBy = reportingRequest.getGroup_by();

		// TODO: check if only the values that can be used in GroupBy are present in the
		// list.

		if (groupBy != null && !groupBy.isEmpty()) {
			Map<String, ReportProperty> properties = reportingRequest.getProperties();
			for (String group : groupBy) {
				if (!properties.containsKey(group)) {
					throw new ValidationException(ErrorCode.GROUP_UNKNOWN);
				}
			}
		}
	}

	private void validateDuration(ReportingRequest reportingRequest) throws ValidationException {
		DurationModel duration = reportingRequest.getDuration();

		if (duration == null) {
			throw new ValidationException(ErrorCode.DURATION_EMPTY);
		}

		if (duration.getStart_timestamp() == null && duration.getEnd_timestamp() == null) {
			throw new ValidationException(ErrorCode.DURATION_START_AND_END_EMPTY);
		}

		if (duration.getStart_timestamp() != null && duration.getEnd_timestamp() == null) {
			// can be only today and yesterday options
			LocalDate now = LocalDateTime.now().toLocalDate();
			LocalDate today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
					.atOffset(ZoneOffset.UTC).toLocalDate();

			LocalDate yesterday = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
					.minusDays(1).atOffset(ZoneOffset.UTC).toLocalDate();

			LocalDate start = LocalDateTime.ofEpochSecond(duration.getStart_timestamp(), 0, ZoneOffset.UTC)
					.toLocalDate();
			if (!start.isEqual(today) && !start.isEqual(yesterday)) {
				throw new ValidationException(ErrorCode.DURATION_START_AND_END_EMPTY);
			}
		}

		if (duration.getStart_timestamp() != null && duration.getEnd_timestamp() != null) {
			LocalDate start = LocalDateTime.ofEpochSecond(duration.getStart_timestamp(), 0, ZoneOffset.UTC)
					.toLocalDate();
			LocalDate end = LocalDateTime.ofEpochSecond(duration.getEnd_timestamp(), 0, ZoneOffset.UTC).toLocalDate();

			LocalDate now = LocalDateTime.now().toLocalDate();
			LocalDate dateRangeMin = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
					.minusYears(1).atOffset(ZoneOffset.UTC).toLocalDate();

			LocalDate dateRangeMax = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
					.plusDays(1).atOffset(ZoneOffset.UTC).toLocalDate();

			if (start.isBefore(dateRangeMin) || start.isAfter(dateRangeMax) || end.isBefore(dateRangeMin)
					|| end.isAfter(dateRangeMax)) {
				throw new ValidationException(ErrorCode.DURATION_START_END_INVALID);
			}
		}
	}

	private void validatePagination(ReportingRequest reportingRequest) throws ValidationException {
		Integer pageSize = reportingRequest.getPage_size();
		Integer pageNum = reportingRequest.getPage_number();

		if (pageSize == null || pageSize <= 0) {
			throw new ValidationException(ErrorCode.INVALID_PAGESIZE);
		}

		if (pageNum == null || pageNum <= 0) {
			throw new ValidationException(ErrorCode.INVALID_PAGE_NUMBER);
		}
	}

	private void validateInterval(ReportingRequest reportingRequest) throws ValidationException {
		Interval interval = reportingRequest.getInterval();
		List<Interval> validIntervals = Arrays.asList(Interval.values());

		if (interval == null) {
			throw new ValidationException(ErrorCode.INTERVAL_EMPTY);
		}

		if (!validIntervals.contains(interval)) {
			throw new ValidationException(ErrorCode.INTERVAL_INVALID);
		}
	}

	private void validateCurrencyOf(ReportingRequest reportingRequest) throws ValidationException {
		CurrencyOf currencyOf = reportingRequest.getCurrency_of();
		List<CurrencyOf> validCurrencyOf = Arrays.asList(CurrencyOf.values());

		if (currencyOf == null) {
			throw new ValidationException(ErrorCode.CURRENCY_OFF_EMPTY);
		}

		if (!validCurrencyOf.contains(currencyOf)) {
			throw new ValidationException(ErrorCode.CURRENCY_OFF_INVALID);
		}
	}
}
