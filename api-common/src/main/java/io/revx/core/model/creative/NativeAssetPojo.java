package io.revx.core.model.creative;

public class NativeAssetPojo {

	private Long id;

	private String iconurl;

	private byte[] data;

	private String title;

	private String body;

	private String previewTitle;

	private String previewBody;

	private String callToAction;

	private Long width;

	private Long height;

	private Long licenseeId;

	private Long advertiserId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCallToAction() {
		return callToAction;
	}

	public void setCallToAction(String callToAction) {
		this.callToAction = callToAction;
	}

	public String getPreviewTitle() {
		return previewTitle;
	}

	public void setPreviewTitle(String previewTitle) {
		this.previewTitle = previewTitle;
	}

	public String getPreviewBody() {
		return previewBody;
	}

	public void setPreviewBody(String previewBody) {
		this.previewBody = previewBody;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "NativeAssetPojo [id=" + id + ", iconurl=" + iconurl + ", data=" + data + ", title="
				+ title + ", body=" + body + ", previewTitle=" + previewTitle + ", previewBody="
				+ previewBody + ", callToAction=" + callToAction + ", width=" + width + ", height=" + height
				+ ", licenseeId=" + licenseeId + ", advertiserId=" + advertiserId + "]";
	}

}
