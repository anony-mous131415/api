package io.revx.core.enums;

public enum AudienceType {
	WEB_BROWSING("WEB_BROWSING"), MOBILE_APP("MOBILE_APP"), DMP("DMP"), // active audience types
	CRM_EMAIL("CRM_EMAIL"), CRM_PHONE("CRM_PHONE"), LOOKALIKE_AUDIENCE("LOOKALIKE_AUDIENCE"), FACEBOOK_ID("FACEBOOK_ID"); // unused audience types
	
	private String audienceType;

	AudienceType(String audienceType) {
		this.audienceType = audienceType;
	}

	public String getAudienceType() {
		return audienceType;
	}

	public void setAudienceType(String audienceType) {
		this.audienceType = audienceType;
	}
}
