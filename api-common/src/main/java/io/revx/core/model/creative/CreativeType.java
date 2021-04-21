/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.core.model.creative;

import java.io.Serializable;



/**
 * The Enum CreativeType.
 */
public enum CreativeType implements Serializable {

  /** The image. */
  image(1),

  /** The html. */
  html(2),

  /** The zipped HTML. */
  zippedHTML(3),

  /** The video. */
  video(4),

  /** The native ad. */
  nativeAd(5),

  /** The native display. */
  nativeVideo(6),

  fbxImage(7), flash(8), fbNewsFeed(9), fbRightColumn(10), fbAppInstall(11), fbAppEngagement(
      12), clickTracker(13), fbxImageNewsFeed(14);


  /** The value. */
  private int value;

  /**
   * Instantiates a new creative type.
   *
   * @param value the value
   */
  private CreativeType(int value) {
    this.value = value;
  }


  /**
   * Gets the.
   *
   * @param value the value
   * @return the creative type
   */
  public static CreativeType get(int value) {
    switch (value) {
      case 1:
        return CreativeType.image;
      case 2:
        return CreativeType.html;
      case 3:
        return CreativeType.zippedHTML;
      case 4:
        return CreativeType.video;
      case 5:
        return CreativeType.nativeAd;
      case 6:
        return CreativeType.nativeVideo;
      case 7:
        return CreativeType.fbxImage;
      case 8:
        return CreativeType.flash;
      case 9:
        return CreativeType.fbNewsFeed;
      case 10:
        return CreativeType.fbRightColumn;
      case 11:
        return CreativeType.fbAppInstall;
      case 12:
        return CreativeType.fbAppEngagement;
      case 13:
        return CreativeType.clickTracker;
      case 14:
        return CreativeType.fbxImageNewsFeed;
      default:
        return null;
    }
  }



  /**
   * Gets the value.
   *
   * @return the value
   */
  public int getValue() {
    return value;
  }
}
