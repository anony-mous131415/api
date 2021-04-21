package io.revx.core.model.creative;


public class NativeAssetData  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7910398840115275818L;
	private String title;
	private String body;
	private String message;
	private Long pageId;
	private Integer brandId;
	private String icon;
	private String ctaText;
	private String backupURL;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String bodyText) {
		this.body = bodyText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public void setIcon(String iconUrl) {
		this.icon=iconUrl;
	}

	public void setctaText(String ctaText) {
		this.ctaText=ctaText;
	}

	public void setBackupURL(String backupUrl) {
		this.backupURL=backupUrl;
	}

	public String getIcon() {
		return icon;
	}

	public String getctaText() {
		return ctaText;
	}

	public String getBackupURL() {
		return backupURL;
	}
}
