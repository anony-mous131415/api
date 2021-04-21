package io.revx.core.event;

import java.io.Serializable;

public class MessageObject implements Serializable {
  private static final long serialVersionUID = 2331509626951597826L;

  private Serializable prevValue = null;
  private Serializable newValue = null;
  public Long userId;

  public MessageObject(Long userId) {
    this.userId = userId;
  }

  public Serializable getNewValue() {
    return newValue;
  }

  public void setNewValue(Serializable newValue) {
    this.newValue = newValue;
  }

  public Serializable getPrevValue() {
    return prevValue;
  }

  public void setPrevValue(Serializable prevValue) {
    this.prevValue = prevValue;
  }
}
