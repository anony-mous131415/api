package io.revx.core.model.creative;

import java.util.List;

public class VideoCreativeDetails {

  private List<VideoDetails> videos;

  private List<CompanionAdDetails> companionAds;

  public List<VideoDetails> getVideos() {
    return videos;
  }

  public void setVideos(List<VideoDetails> videos) {
    this.videos = videos;
  }

  public List<CompanionAdDetails> getCompanionAds() {
    return companionAds;
  }

  public void setCompanionAds(List<CompanionAdDetails> companionAds) {
    this.companionAds = companionAds;
  }

  @Override
  public String toString() {
    return "VideoCreativeDetails [videos=" + videos + ", companionAds=" + companionAds + "]";
  }

}
