package io.revx.core.model.creative;

import java.io.Serializable;

public enum CreativeVideoFormat implements Serializable {
	/*
	 * This enum should be sync with DB and UI. We will update xml using this
	 * enums.
	 */

	FLV("video/x-flv"), MP4("video/mp4"), THREE_GPP("video/3gpp"), MOV("video/quicktime"), WEBM("video/webm"), DASH("video/dash"), HLS("video/hls");

	private String xmlAttributeValue;

	private CreativeVideoFormat(String xmlAttributeValue) {
		this.xmlAttributeValue = xmlAttributeValue;
	}

	public String getXmlAttributeValue() {
		return xmlAttributeValue;
	}

	public void setXmlAttributeValue(String xmlAttributeValue) {
		this.xmlAttributeValue = xmlAttributeValue;
	}

  public static CreativeVideoFormat getByXmlAttributeValue(String xmlAttributeValue) {
    if (xmlAttributeValue.equals("video/x-flv")) {
      return CreativeVideoFormat.FLV;
    } else if (xmlAttributeValue.equals("video/mp4")) {
      return CreativeVideoFormat.MP4;
    } else if (xmlAttributeValue.equals("video/3gpp")) {
      return CreativeVideoFormat.THREE_GPP;
    } else if (xmlAttributeValue.equals("video/quicktime")) {
      return CreativeVideoFormat.MOV;
    } else if (xmlAttributeValue.equals("video/webm")) {
      return CreativeVideoFormat.WEBM;
    } else if (xmlAttributeValue.equals("video/dash")) {
      return CreativeVideoFormat.DASH;
    } else if (xmlAttributeValue.equals("video/hls")) {
      return CreativeVideoFormat.HLS;
    } else {
      // default
      return CreativeVideoFormat.MP4;
    }
  }
}
