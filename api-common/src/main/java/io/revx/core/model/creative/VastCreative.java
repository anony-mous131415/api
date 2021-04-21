/*
 * @author: ranjan-pritesh
 * 
 * @date: 30dec2019
 */
package io.revx.core.model.creative;

/**
 * The Class VastCreative.
 */
public class VastCreative {

  /** The video link. */
  private String videoLink;

  /** The duration. */
  private Long duration;

  /** The size. */
  private Size size;

  /** The video format. */
  private CreativeVideoFormat videoFormat;

  /** The has end card. */
  private Boolean hasEndCard;

  /**
   * Gets the video link.
   *
   * @return the video link
   */
  public String getVideoLink() {
    return videoLink;
  }

  /**
   * Sets the video link.
   *
   * @param videoLink the new video link
   */
  public void setVideoLink(String videoLink) {
    this.videoLink = videoLink;
  }



  /**
   * Gets the duration.
   *
   * @return the duration
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Sets the duration.
   *
   * @param duration the new duration
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Gets the size.
   *
   * @return the size
   */
  public Size getSize() {
    return size;
  }

  /**
   * Sets the size.
   *
   * @param size the new size
   */
  public void setSize(Size size) {
    this.size = size;
  }

  
  
  
  public CreativeVideoFormat getVideoFormat() {
    return videoFormat;
  }

  public void setVideoFormat(CreativeVideoFormat videoFormat) {
    this.videoFormat = videoFormat;
  }

  /**
   * Gets the checks for end card.
   *
   * @return the checks for end card
   */
  public Boolean getHasEndCard() {
    return hasEndCard;
  }

  /**
   * Sets the checks for end card.
   *
   * @param hasEndCard the new checks for end card
   */
  public void setHasEndCard(Boolean hasEndCard) {
    this.hasEndCard = hasEndCard;
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "VastCreative [videoLink=" + videoLink + ", duration=" + duration + ", size=" + size
        + ", videoFormat=" + videoFormat + ", hasEndCard=" + hasEndCard + "]";
  }


}
