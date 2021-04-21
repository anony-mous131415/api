package io.revx.api.service.strategy;

import io.revx.api.mysql.entity.strategy.TargetingComponent;

public class PlacementExpressionObject {
  String expr;
  TargetingComponent dpTC, mwTC, mapTC, fanTC;
  String dsTEx, mwTEx, mapTEx, fanTEx, mComTEx;
  String displayExpr, mobileExpr;

  public PlacementExpressionObject() {
    expr = null;
    dpTC = null;
    mwTC = null;
    mapTC = null;
    fanTC = null;
    dsTEx = null;
    mwTEx = null;
    mapTEx = null;
    fanTEx = null;
    displayExpr = null;
    mobileExpr = null;
  }

  public String getExpr() {
    return expr;
  }

  public void setExpr(String expr) {
    this.expr = expr;
  }

  public TargetingComponent getDpTC() {
    return dpTC;
  }

  public void setDpTC(TargetingComponent dpTC) {
    this.dpTC = dpTC;
  }

  public TargetingComponent getMwTC() {
    return mwTC;
  }

  public void setMwTC(TargetingComponent mwTC) {
    this.mwTC = mwTC;
  }

  public TargetingComponent getMapTC() {
    return mapTC;
  }

  public void setMapTC(TargetingComponent mapTC) {
    this.mapTC = mapTC;
  }

  public TargetingComponent getFanTC() {
    return fanTC;
  }

  public void setFanTC(TargetingComponent fanTC) {
    this.fanTC = fanTC;
  }

  public String getDsTEx() {
    return dsTEx;
  }

  public void setDsTEx(String dsTEx) {
    this.dsTEx = dsTEx;
  }

  public String getMwTEx() {
    return mwTEx;
  }

  public void setMwTEx(String mwTEx) {
    this.mwTEx = mwTEx;
  }

  public String getMapTEx() {
    return mapTEx;
  }

  public void setMapTEx(String mapTEx) {
    this.mapTEx = mapTEx;
  }

  public String getFanTEx() {
    return fanTEx;
  }

  public void setFanTEx(String fanTEx) {
    this.fanTEx = fanTEx;
  }


  public String getmComTEx() {
    return mComTEx;
  }

  public void setmComTEx(String mComTEx) {
    this.mComTEx = mComTEx;
  }

  public String getDisplayExpr() {
    return displayExpr;
  }

  public void setDisplayExpr(String displayExpr) {
    this.displayExpr = displayExpr;
  }

  public String getMobileExpr() {
    return mobileExpr;
  }

  public void setMobileExpr(String mobileExpr) {
    this.mobileExpr = mobileExpr;
  }
}
