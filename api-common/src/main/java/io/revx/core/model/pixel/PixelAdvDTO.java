package io.revx.core.model.pixel;

public class PixelAdvDTO extends DataPixelDTO {

	private static final long serialVersionUID = 7287371717348614035L;

	private Long licenseeId;

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}
}
