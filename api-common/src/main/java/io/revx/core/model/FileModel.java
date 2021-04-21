package io.revx.core.model;

import io.revx.core.enums.CompressionType;
import io.revx.core.model.creative.FileType;

public class FileModel extends BaseModel{

  private static final long serialVersionUID = 1L;
  private String originFileName;
  private String localFileLocation;
  private String urlFileLocation;
  private FileType fileType;
  private String size;
  private String errorMsg;
  private CompressionType compressionType;
  
  public CompressionType getCompressionType() {
    return compressionType;
  }
  public void setCompressionType(CompressionType compressionType) {
    this.compressionType = compressionType;
  }
  public FileType getFileType() {
    return fileType;
  }
  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }
  public String getOriginFileName() {
    return originFileName;
  }
  public void setOriginFileName(String originFileName) {
    this.originFileName = originFileName;
  }
  public String getLocalFileLocation() {
    return localFileLocation;
  }
  public void setLocalFileLocation(String localFileLocation) {
    this.localFileLocation = localFileLocation;
  }
  public String getUrlFileLocation() {
    return urlFileLocation;
  }
  public void setUrlFileLocation(String urlFileLocation) {
    this.urlFileLocation = urlFileLocation;
  }
  public static long getSerialversionuid() {
    return serialVersionUID;
  }
  public String getSize() {
    return size;
  }
  public void setSize(String size) {
    this.size = size;
  }
  public String getErrorMsg() {
    return errorMsg;
  }
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
  
  
}
