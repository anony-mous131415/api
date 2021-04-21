package io.revx.core.model.audience;

import io.revx.core.enums.CompressionType;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.EncodingType;

public class PixelDataFileDTO {


  private Long id;
  private String name;
  private Long createdAt;
  private String filePath;
  private Long lastUpdatedAt;
  private Long pixelId;
  private UserDataType userDataType;
  private DataSourceType sourceType;
  private EncodingType encodingType;
  private CompressionType compressionType;
  private CrmStatus status;
  private Long updatedAt;
  private Long licenseeId;
  private String md5sum;
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
  public Long getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }
  public String getFilePath() {
    return filePath;
  }
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
  public Long getLastUpdatedAt() {
    return lastUpdatedAt;
  }
  public void setLastUpdatedAt(Long lastUpdatedAt) {
    this.lastUpdatedAt = lastUpdatedAt;
  }
  public Long getPixelId() {
    return pixelId;
  }
  public void setPixelId(Long pixelId) {
    this.pixelId = pixelId;
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
  public CrmStatus getStatus() {
    return status;
  }
  public void setStatus(CrmStatus status) {
    this.status = status;
  }
  public Long getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(Long updatedAt) {
    this.updatedAt = updatedAt;
  }
  public Long getLicenseeId() {
    return licenseeId;
  }
  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }
  public String getMd5sum() {
    return md5sum;
  }
  public void setMd5sum(String md5sum) {
    this.md5sum = md5sum;
  }
  public Long getLastModifiedAtServer() {
    return lastModifiedAtServer;
  }
  public void setLastModifiedAtServer(Long lastModifiedAtServer) {
    this.lastModifiedAtServer = lastModifiedAtServer;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PixelDataFileDTO [id=").append(id).append(", name=").append(name)
        .append(", createdAt=").append(createdAt).append(", filePath=").append(filePath)
        .append(", lastUpdatedAt=").append(lastUpdatedAt).append(", pixelId=").append(pixelId)
        .append(", userDataType=").append(userDataType).append(", sourceType=").append(sourceType)
        .append(", encodingType=").append(encodingType).append(", compressionType=")
        .append(compressionType).append(", status=").append(status).append(", updatedAt=")
        .append(updatedAt).append(", licenseeId=").append(licenseeId).append(", md5sum=")
        .append(md5sum).append(", lastModifiedAtServer=").append(lastModifiedAtServer).append("]");
    return builder.toString();
  }

  
}
