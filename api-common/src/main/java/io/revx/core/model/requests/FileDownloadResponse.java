package io.revx.core.model.requests;

public class FileDownloadResponse {

  private String fileName;

  private String fileDownloadUrl;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileDownloadUrl() {
    return fileDownloadUrl;
  }

  public void setFileDownloadUrl(String fileDownloadUrl) {
    this.fileDownloadUrl = fileDownloadUrl;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FileDownloadResponse [fileName=");
    builder.append(fileName);
    builder.append(", fileDownloadUrl=");
    builder.append(fileDownloadUrl);
    builder.append("]");
    return builder.toString();
  }


}
