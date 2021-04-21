package io.revx.core.model;

public class AudienceESDTO extends StatusTimeModel {

	private static final long serialVersionUID = 1L;

	private Long licenseeId;

	private Long advertiserId;

	private String advertiserName;
	/*
	 * NULL -> Rule base/CRM CLICKER -> 1 HASH_BUCKET -> 2 DMP -> 3 PLATFORM -> 4
	 */
	private Integer segmentType;

	private Long pixelId;

	private Long totalUU;

	private Long dailyUU;

	// APP or WEBSITE or DMP
	private String user_data_type;

	public Long getPixelId() {
		return pixelId;
	}

	public void setPixelId(Long pixelId) {
		this.pixelId = pixelId;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
	}

	public Long getTotalUU() {
		return totalUU;
	}

	public void setTotalUU(Long totalUU) {
		this.totalUU = totalUU;
	}

	public Long getDailyUU() {
		return dailyUU;
	}

	public void setDailyUU(Long dailyUU) {
		this.dailyUU = dailyUU;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public Integer getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(Integer segmentType) {
		this.segmentType = segmentType;
	}

	public String getUser_data_type() {
		return user_data_type;
	}

	public void setUser_data_type(String user_data_type) {
		this.user_data_type = user_data_type;
	}
}
