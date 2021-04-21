package io.revx.core.model.creative;

import java.util.Set;

public class VideoCreativeVastXmlTemplate {

  private String creativeName;

  private Long duration;

  private Set<VideoDetails> videos;

  private Set<CompanionAdDetails> companionAds;

  public Set<VideoDetails> getVideos() {
    return videos;
  }

  public void setVideos(Set<VideoDetails> videos) {
    this.videos = videos;
  }

  public Set<CompanionAdDetails> getCompanionAds() {
    return companionAds;
  }

  public void setCompanionAds(Set<CompanionAdDetails> companionAds) {
    this.companionAds = companionAds;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public String getCreativeName() {
    return creativeName;
  }

  public void setCreativeName(String creativeName) {
    this.creativeName = creativeName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("VideoCreativeVastXmlTemplate [creativeName=").append(creativeName)
        .append(", duration=").append(duration).append(", videos=").append(videos)
        .append(", companionAds=").append(companionAds).append("]");
    return builder.toString();
  }

}
