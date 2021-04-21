package io.revx.api.mysql.crmdb.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.enums.CompressionType;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.EncodingType;
import io.revx.core.model.audience.UserDataType;

@Entity
@Table(name = "PixelDataFile")
public class PixelDataFileEntity implements IDto, IStatus, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7505788372353701L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "pdf_id", nullable = false)
	private Long id;

	@Column(name = "pdf_name", nullable = false)
	private String name;

	@Column(name = "pdf_licensee_id", nullable = false)
	private Long licenseeId;

	@Column(name = "pdf_created_at", nullable = false)
	private Long createdAt;

	@Column(name = "pdf_updated_at")
	private Long updatedAt;

	@Column(name = "pdf_file_path", nullable = false)
	private String filePath;

	@Column(name = "pdf_status", nullable = false)
	private Integer status;

	@Column(name = "pdf_is_new", nullable = false)
	private Boolean newFile;

	@Column(name = "pdf_pixel_id", nullable = false)
	private Long pixelId;

	@Enumerated(EnumType.STRING)
	@Column(name = "pdf_user_type", nullable = false)
	private UserDataType userDataType;

	@Enumerated(EnumType.STRING)
	@Column(name = "pdf_source_type", nullable = false)
	private DataSourceType sourceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "pdf_encoding_type", nullable = false)
	private EncodingType encodingType;

	@Enumerated(EnumType.STRING)
	@Column(name = "pdf_file_compression_type", nullable = false)
	private CompressionType compressionType;

	@Column(name = "pdf_md5sum", nullable = true)
	private String md5sum;

	@Column(name = "pdf_last_modified_at_server", nullable = true)
	private Long lastModifiedAtServer;

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

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CrmStatus getStatus() {
		return CrmStatus.getById(status);
	}

	public void setStatus(CrmStatus status) {
		this.status = status == null ? null : status.id;
	}

	public Long getPixelId() {
		return pixelId;
	}

	public PixelDataFileEntity setPixelId(Long pixelId) {
		this.pixelId = pixelId;
		return this;
	}

	public UserDataType getUserDataType() {
		return userDataType;
	}

	public void setUserDataType(UserDataType userDataType) {
		this.userDataType = userDataType;
	}

	public DataSourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(DataSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public EncodingType getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(EncodingType encodingType) {
		this.encodingType = encodingType;
	}

	public CompressionType getCompressionType() {
		return compressionType;
	}

	public void setCompressionType(CompressionType compressionType) {
		this.compressionType = compressionType;
	}

	public Boolean getNewFile() {
		return newFile;
	}

	public PixelDataFileEntity setNewFile(Boolean newFile) {
		this.newFile = newFile;
		return this;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public Long getLastModifiedAtServer() {
		return lastModifiedAtServer == null ? 0 : lastModifiedAtServer;
	}

	public void setLastModifiedAtServer(Long lastModifiedAtServer) {
		this.lastModifiedAtServer = lastModifiedAtServer;
	}

	@Override
	public String toString() {
		return "PixelDataFileDto [id=" + id + ", name=" + name
				+ ", licenseeId=" + licenseeId + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", filePath=" + filePath
				+ ", status=" + status + ", newFile=" + newFile + ", pixelId="
				+ pixelId + ", userDataType=" + userDataType + ", sourceType="
				+ sourceType + ", encodingType=" + encodingType
				+ ", compressionType=" + compressionType + ", md5sum=" + md5sum
				+ ", lastModifiedAtServer=" + lastModifiedAtServer + "]";
	}

}