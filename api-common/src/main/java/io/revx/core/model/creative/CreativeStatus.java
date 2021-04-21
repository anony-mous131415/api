/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.core.model.creative;

import java.io.Serializable;

/**
 * The Enum CreativeStatus.
 */
public enum CreativeStatus implements Serializable {

  /** The active. */
  active(1),
  /** The inactive. */
  inactive(0);

  /** The value. */
  private int value;

  /**
   * Instantiates a new creative status.
   *
   * @param value the value
   */
  private CreativeStatus(int value) {
    this.value = value;
  }

  /**
   * Gets the.
   *
   * @param value the value
   * @return the creative status
   */
  public static CreativeStatus get(int value) {
    switch (value) {
      case 1:
        return CreativeStatus.active;
      case 0:
        return CreativeStatus.inactive;
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
