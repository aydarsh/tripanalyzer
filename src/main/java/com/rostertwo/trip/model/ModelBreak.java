package com.rostertwo.trip.model;

/**
 * ModelBreak
 */

public class ModelBreak   {
  private Long startTimestamp;
  private Long endTimestamp;
  private Float positionLat;
  private Float positionLong;
  
  public ModelBreak(Long startTimestamp, Long endTimestamp, Float positionLat, Float positionLong) {
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
    this.positionLat = positionLat;
    this.positionLong = positionLong;
  }
  
  public Long getStartTimestamp() {
    return startTimestamp;
  }
  public void setStartTimestamp(Long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Long getEndTimestamp() {
    return endTimestamp;
  }
  public void setEndTimestamp(Long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public Float getPositionLat() {
    return positionLat;
  }
  public void setPositionLat(Float positionLat) {
    this.positionLat = positionLat;
  }

  public Float getPositionLong() {
    return positionLong;
  }
  public void setPositionLong(Float positionLong) {
    this.positionLong = positionLong;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelBreak {\n");
    
    sb.append("    startTimestamp: ").append(toIndentedString(startTimestamp)).append("\n");
    sb.append("    endTimestamp: ").append(toIndentedString(endTimestamp)).append("\n");
    sb.append("    positionLat: ").append(toIndentedString(positionLat)).append("\n");
    sb.append("    positionLong: ").append(toIndentedString(positionLong)).append("\n");
    sb.append("}");
    return sb.toString();
  }
  
  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

