package io.revx.core.exception;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(10000,"Internal server error"),
    DEFAULT_ERROR(10001,""),
    MISSING_VARIABLE_ERROR(10002,""),
    USER_NAME_NOT_FOUND_ERROR(10003,"User details cannot be found"),
    USER_NOT_ACTIVE_ERROR(10004,"Selected user is not active currently"),
    USERNAME_OR_PASSWORD_SHOULD_NOT_BLANK(10005,"Username amd/orPassword should not be empty"),
    USER_DO_NOT_ROLE_ERROR(10006,"User is not authorized for performing the action"),
    USER_PASSWORD_MISMATCH_ERROR(10007,"Username/Password is not correct"),
    SOCIAL_SERVER_ERROR(10008,"Failed to fetch token from external authentication service"),
    AUTHORIZATION_TOKEN_NOT_FOUND_ERROR(10009,"Token not found"),
    USER_DONT_HAVE_ACCESS_ON_GIVEN_LICENSEE_ERROR(10010,"Do not have permissions to access the licensee"),
    INVALID_ACCESS_TOKEN(10011,"Token is Not Valid"),
    ACCESS_TOKEN_EXPIRED(10012,"The token is expired and not valid anymore"),
    INVALID_PARAMETER_IN_REQUEST(10013,"Invalid parameters found in request"),
    BAD_REQUEST(10014,"Request is not valid"),
    BAD_CONFIGURATION(10015,""),
    INVALID_SEARCH_FILTER(10016,""),
    EC_SLM_SERVICE_GENERIC_ERROR(10017,""),
    EC_SLM_SERVICE_CLIENT_GENERIC_ERROR(10018,""),
    EC_SLM_INVALID_VALUE_FOR_DURATION(10019,""),
    EC_SLM_WRITE_ACCESS_NOT_ALLOWED(10020,""),
    ENTITY_NOT_FOUND(10021,"Entity record is not available"),
    ENTITY_REQUIRED(10022,"Entity object "),
    ENTITY_INVALID_VALUE(10023,"Fetched invalid entity object"),
    STRATEGY_PLACEMENTS_IS_REQ(10024,"No valid placement associated with Strategy"),
    SAME_SEGMENT_CANT_BE_PRESENT_IN_BOTH_LISTS(10025,"Targeting and Blocking same set of Audience is not allowed"),
    PARSING_INVALID_JSON(10026,"Found Invalid json"),
    CREATE_MODIFY_NOT_ALLOWED(10027,"Updating strategy under clicker tracker is not allowed"),
    ENTITY_NAME_VALIDATION(10028,"Entity name already exists in records"),
    RTB_STRATEGY_AUDIENCE_REQUIRED(10029,"No valid audience types associated with Strategy"),
    CLICK_TRACKER_CONV_NOT_ALLOWED(10030,"Click tracker conversion not allowed for current user"),
    CAMPAIGN_HAS_EXPIRED(10031,"Campaign is already expired ,strategy duplication not allowed"),
    MSG_ALREADY_SET(10032,"Selected entity is already active"),
    MSG_ALREADY_UNSET(10033,"Selected entity is already inactive"),
    ENTITY_IMMUTABLE_FIELD(10034,"Updating value of the fields is not valid"),
    ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT(10035,"Entity value exceeds the upper limit"),
    SOONER_DATE_VALUE(10036,"Selected entity start time cannot be set before start time of parent entity"),
    LATER_DATE_VALUE(10037,"Selected entity end time cannot be set after end time of parent entity"),
    START_DATE_EQUAL_END_DATE(10038,"Start and end timestamp should not be equal"),
    ENTITY_OTHER_ADVERTISER_PIXELS_OR_CREATIVES_NOT_ALLOWED(10039,"Associated objects with entity mismatch error"),
    PACING_TYPE_NOT_SUPPORTED_WITH_UNLIMITED_BUDGET(10040,"Pacing type is not supported with unlimited budget"),
    EMPTY_RESULT(10041,"Result should not be empty"),
    INVALID_ICON_PATH(10042,"Error while creating icon url for uploaded icon"),
    CRATIVE_PERFORMANCE_DATA_EXCEPTION(10043,"Exception while fetching the performance data of creatives"),
    QUICK_EDIT_UPDATE_QUERY_ERROR(10044,"Error occurred while executing query for qucik update"),
    VAST_FORMATION_EXCEPTION(10045,"Error while creating vast xml"),
    MULTIPLE_ROW_UPDATED(10046, "Multiple rows updated."),
    COLUMN_LIST_EMPTY(2001, "Columns attribute cannot be empty"),
    INVALID_JSON_FORMAT(2002, "Invalid JSON format in the property configuration file"),
    UNKNOWN_ATTRIBUTE_IN_JSON(2003, "Unknown attribute in the property configuration file"),
    FILE_NOT_FOUND(2004, "File not found"),
    INVALID_ENTITY(2005, "Specified entity is unknown"),
    COLUMN_UNKNOWN(2006, "Unknown value in columns list"),
    CURRENCY_OFF_EMPTY(2007, "No value entered for currency_of attribute"),
    CURRENCY_OFF_INVALID(2008, "Unknown value for currency_of attribute"),
    GROUP_UNKNOWN(2009, "Unknown value in group_by list"),
    FILTER_COLUMN_UNKNOWN(2010, "Unknown value in filter column"),
    DURATION_EMPTY(2011, "Duration attribute cannot be empty"),
    DURATION_START_AND_END_EMPTY(2012, "Start and end timestamps are empty"),
    DURATION_END_TIMESTAMP_EMPTY(2013, "End timestamp empty"),
    DURATION_START_END_INVALID(2014, "Invalid start and/or end timestamps"),
    INVALID_PAGESIZE(2015, "invalid page size value"),
    INVALID_PAGE_NUMBER(2016, "Invalid page number value"),
    INTERVAL_EMPTY(2017, "No value entered for interval attribute"),
    INTERVAL_INVALID(2018, "Unknown value for interval attribute"),
    ADVERTISER_ACCESS_DENIED(2019, "Do not have permission to access advertiser"),
    SQL_QUERY_EXECUTION_FAILED(2020, "Exception while executing query"),
    EXPORT_CSV_FAILED(2021, "Failed to export data to csv file"),
    EXPORT_COMMAND_EXECUTION_FAILED(2022, "Exception while executing export command"),
    INVALID_LICENSEE(2023, "Unable to get the current active licensee"),
    INVALID_AUTHORIZATION(2024, "Unable to get user authorization details"),
    RESULT_TOO_LARGE_TO_EXPORT(2025,"Query result is too large to export to csv file");

    private final int errorCodeValue;
    private final String errorMessage;

    ErrorCode(int errorCodeValue, String errorMessage) {
        this.errorCodeValue = errorCodeValue;
        this.errorMessage = errorMessage;
    }

    private static final Map<Integer, ErrorCode> ordinalMap = new HashMap<>();

    public String getErrorMessage() {
        return errorMessage;
    }

    static {
        for (ErrorCode ele : ErrorCode.values()) {
            ordinalMap.put(ele.getValue(), ele);
        }
    }

    public static ErrorCode parseFrom(int code) {
        return ordinalMap.get(code);
    }

    public int getValue() {
        return errorCodeValue;
    }

}
