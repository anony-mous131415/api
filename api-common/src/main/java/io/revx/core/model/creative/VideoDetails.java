package io.revx.core.model.creative;

public class VideoDetails {

  private String videoLink;

  private Integer duration;

  private Size size;

  private Integer bitrate;

  private CreativeVideoFormat videoFormat;

  private String delivery;

  public VideoDetails() {}

  public VideoDetails(String videoLink, Integer duration, Size size, Integer bitrate,
      CreativeVideoFormat videoFormat, String delivery) {
    this.videoLink = videoLink;
    this.duration = duration;
    this.size = size;
    this.bitrate = bitrate;
    this.videoFormat = videoFormat;
    this.delivery = delivery;
  }

  public String getVideoLink() {
    return videoLink;
  }

  public void setVideoLink(String videoLink) {
    this.videoLink = videoLink;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public Integer getBitrate() {
    return bitrate;
  }

  public void setBitrate(Integer bitrate) {
    this.bitrate = bitrate;
  }

  public CreativeVideoFormat getVideoFormat() {
    return videoFormat;
  }

  public void setVideoFormat(CreativeVideoFormat videoFormat) {
    this.videoFormat = videoFormat;
  }

  public String getDelivery() {
    return delivery;
  }

  public void setDelivery(String delivery) {
    this.delivery = delivery;
  }

  @Override
  public String toString() {
    return "VideoDetails [videoLink=" + videoLink + ", duration=" + duration + ", size=" + size
        + ", bitrate=" + bitrate + ", videoFormat=" + videoFormat + ", delivery=" + delivery + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bitrate == null) ? 0 : bitrate.hashCode());
    result = prime * result + ((delivery == null) ? 0 : delivery.hashCode());
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result + ((videoFormat == null) ? 0 : videoFormat.hashCode());
    result = prime * result + ((videoLink == null) ? 0 : videoLink.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VideoDetails other = (VideoDetails) obj;
    if (bitrate == null) {
      if (other.bitrate != null)
        return false;
    } else if (!bitrate.equals(other.bitrate))
      return false;
    if (delivery == null) {
      if (other.delivery != null)
        return false;
    } else if (!delivery.equals(other.delivery))
      return false;
    if (duration == null) {
      if (other.duration != null)
        return false;
    } else if (!duration.equals(other.duration))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    } else if (!size.equals(other.size))
      return false;
    if (videoFormat == null) {
      if (other.videoFormat != null)
        return false;
    } else if (!videoFormat.equals(other.videoFormat))
      return false;
    if (videoLink == null) {
      if (other.videoLink != null)
        return false;
    } else if (!videoLink.equals(other.videoLink))
      return false;
    return true;
  }

}
