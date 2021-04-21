package io.revx.core.model.creative;

public class VideoUIPojo {
  
  private String videoPath;
  
  private String imagePath;

  public String getVideoPath() {
    return videoPath;
  }

  public void setVideoPath(String videoPath) {
    this.videoPath = videoPath;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  @Override
  public String toString() {
    return "VideoUIPojo [videoPath=" + videoPath + ", imagePath=" + imagePath + "]";
  }

}
