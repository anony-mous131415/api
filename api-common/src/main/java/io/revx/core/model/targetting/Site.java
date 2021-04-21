/*
 * Copyright 2012 Komli Media Inc. All Rights Reserved. KOMLI MEDIA PROPRIETARY/CONFIDENTIAL. Use is
 * subject to license terms.
 * 
 * @version 1.0, 14-Aug-2012
 * 
 * @author Rajat Bhushan
 */
package io.revx.core.model.targetting;

import io.revx.core.model.BaseModel;

public class Site extends BaseModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public String siteUrl;

  public Site() {
    id = 0l;
    siteUrl = "";
  }

  public Site(long id, String siteUrl) {
    this.id = id;
    this.siteUrl = siteUrl;
  }
}
