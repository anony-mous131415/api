package io.revx.core.model;


@SuppressWarnings("serial")
public class ClickDestinationAutomationUrls {
	
	 private Long mmpId;
	 private String mmpName;
	 private String androidClickUrl;
	 private String androidS2sUrl;
	 private String iosClickUrl;
	 private String iosS2sUrl;

	 //REVX-515 : dynamic s2s fallback
     private String fallBackUrlStatic;
	 private String fallBackUrlDynamic;


	 
	public Long getMmpId() {
		return mmpId;
	}
	public void setMmpId(Long mmpId) {
		this.mmpId = mmpId;
	}
	public String getMmpName() {
		return mmpName;
	}
	public void setMmpName(String mmpName) {
		this.mmpName = mmpName;
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
	
	public String getFallBackUrlStatic() {
		return fallBackUrlStatic;
	}
	public void setFallBackUrlStatic(String fbStatic) {
		this.fallBackUrlStatic = fbStatic;
	}

	public String getFallBackUrlDynamic() {
		return fallBackUrlDynamic;
	}
	public void setFallBackUrlDynamic(String fbDynamic) {
		this.fallBackUrlDynamic = fbDynamic;
	}
	
	@Override
	  public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("[mmpId=").append(mmpId)
	        .append(", mmpName=").append(mmpName)
	        .append(", androidClick=").append(androidClickUrl)
	        .append(", androidS2s=").append(androidS2sUrl)
	        .append(", androidS2s=").append(androidS2sUrl)
	        .append(", iosClick=").append(iosClickUrl)
	        .append(", iosS2s=").append(iosS2sUrl)	 
			.append(", fallBackUrlStatic=").append(fallBackUrlStatic)	
			.append(", fallBackUrlDynamic=").append(fallBackUrlDynamic)	                
	        .append("]");
	    return builder.toString();
	  }
	
	

}
