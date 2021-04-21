package io.revx.core.model;

@SuppressWarnings("serial")
public class MobileMeasurementPartner extends StatusTimeModel{

	
	 private Long id;
	 private String name;
	 private String androidClickUrl;
	 private String androidS2sUrl;
	 private String iosClickUrl;
	 private String iosS2sUrl;
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAndroidClickUrl() {
		return androidClickUrl;
	}
	public void setAndroidClickUrl(String androidClickUrl) {
		this.androidClickUrl = androidClickUrl;
	}
	public String getAndroidS2sUrl() {
		return androidS2sUrl;
	}
	public void setAndroidS2sUrl(String androidS2sUrl) {
		this.androidS2sUrl = androidS2sUrl;
	}
	public String getIosClickUrl() {
		return iosClickUrl;
	}
	public void setIosClickUrl(String iosClickUrl) {
		this.iosClickUrl = iosClickUrl;
	}
	public String getIosS2sUrl() {
		return iosS2sUrl;
	}
	public void setIosS2sUrl(String iosS2sUrl) {
		this.iosS2sUrl = iosS2sUrl;
	}
	
	
	
	@Override
	  public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("[mmpId=").append(id)
	        .append(", mmpName=").append(name)
	        .append(", androidClick=").append(androidClickUrl)
	        .append(", androidS2s=").append(androidS2sUrl)
	        .append(", androidS2s=").append(androidS2sUrl)
	        .append(", iosClick=").append(iosClickUrl)
	        .append(", iosS2s=").append(iosS2sUrl)	        
	        .append("]");
	    return builder.toString();
	  }
	 
	 
	 

}
