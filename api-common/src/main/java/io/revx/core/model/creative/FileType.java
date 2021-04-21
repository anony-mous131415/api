package io.revx.core.model.creative;

import org.apache.commons.lang3.StringUtils;

public enum FileType {


  ZIP("application/zip", ".zip"), JPG("image/jpg", ".jpg"), JPEG("image/jpeg", ".jpeg"), PNG(
      "image/png", ".png"), GIF("image/gif", ".gif"), FLV("video/x-flv", ".flv"), MP4("video/mp4",
          ".mp4"), THREE_GPP("video/3gpp", ".3gp"), MOV("video/quicktime", ".mov"), WEBM(
              "video/webm", ".webm"), DASH("video/dash", ".dash"), HLS("video/hls", ".hls"), GZIP(
                  "application/gzip", ".gz"), TEXT("text/plain", ".txt"), CSV("text/csv", ".csv"), UNKNOWN("unknown","unknown");

  private String type;

  private String extension;

  private FileType(String type, String extension) {
    this.type = type;
    this.extension = extension;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public static FileType getFileType(String type) {
    if(StringUtils.isBlank(type))
      return FileType.UNKNOWN;
    for(FileType ft : FileType.values()) {
      if(ft.type.equals(type))
        return ft;
    }
    
    return FileType.UNKNOWN;
  }

}
