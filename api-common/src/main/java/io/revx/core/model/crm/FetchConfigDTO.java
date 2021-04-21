package io.revx.core.model.crm;

import io.revx.core.enums.CompressionType;
import io.revx.core.enums.EncodingType;
import io.revx.core.model.audience.UserDataType;

import java.util.regex.Pattern;

public class FetchConfigDTO extends RemoteFileDTO {

	private Long id;

	private Long pixelId;

	private Long licenseeId;

	private CompressionType compressionType;
	// Ideally userType should be fetched via API call to SLM but for
	// simplicity of the design its duplicated here from Audience data
	private UserDataType userDataType;

	private EncodingType encodingType;

	Pattern pattern = Pattern.compile("\\d{3}");

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPixelId() {
		return pixelId;
	}

	public void setPixelId(Long pixelId) {
		this.pixelId = pixelId;
	}

	public EncodingType getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(EncodingType encodingType) {
		this.encodingType = encodingType;
	}

	public UserDataType getUserDataType() {
		return userDataType;
	}

	public void setUserDataType(UserDataType userDataType) {
		this.userDataType = userDataType;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public CompressionType getCompressionType() {
		return compressionType;
	}

	public void setCompressionType(CompressionType compressionType) {
		this.compressionType = compressionType;
	}

	@Override
	public String toString() {
		return "FetchConfig [id=" + id + ", pixelId=" + pixelId
				+ ", licenseeId=" + licenseeId + ", compressionType="
				+ compressionType + ", userDataType=" + userDataType
				+ ", encodingType=" + encodingType + ", protocol=" + protocol
				+ ", host=" + host + ", port=" + port + ", authMethod="
				+ authMethod + ", keyPath=" + keyPath + ", username="
				+ username + ", password="
				+ pathTemplate + "]";
	}

}
