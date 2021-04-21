package io.revx.api.controller.strategy;

public class BulkstrategiesConstants {

	public static final String STRATEGY_ID_NOT_FOUND = "A row having no strategy Id";

	public static final String INVALID = "Invalid";

	public static final String STRATEGY_ID_NOT_VALID = "Strategy Id is not valid";

	public static final String CAMPAIGN_ID_NOT_VALID = "Campaign Id is not valid";

	public static final String STRATEGY_NAME_NOT_FOUND = "Strategy name can not be empty";

	public static final String BID_PRICE_NOT_FOUND = "Bid price field can not be empty";

	public static final String BID_TYPE_NOT_FOUND = "Bid type field can not be empty";

	public static final String EC_CAMPAIGN_PIXEL_REQUIRED_FOR_CPA_STRATEGY =
			"A pixel on parent campaign must be selected when the bid type is CPA";

	public static final String FCAP_NOT_FOUND = "Fcap field can not be empty";

	public static final String BID_PRICE_NOT_VALID =
			"Bid price is not a valid. It should be a numeric value";

	public static final String BID_TYPE_NOT_VALID =
			"Bid price type is not valid. It should be CPM, CPC, CPA or CPI";

	public static final String FCAP_NOT_VALID =
			"F-cap is not a valid. It should be whole number greater than 0";

	public static final String MIN_BID_NOT_VALID = "Min bid price is not a valid. It should be whole number.";

	public static final String MAX_BID_NOT_VALID =
			"Max bid price is not a valid. It should be greater than equal to -1";
	
	public static final String MAX_BID_SHOULD_BE_GREATER_THAN_MIN_BID =
        "Max bid price should be greater than min bid price";
}
