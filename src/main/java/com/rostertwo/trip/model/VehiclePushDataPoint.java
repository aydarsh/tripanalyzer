package com.rostertwo.trip.model;

/**
 * VehiclePushDataPoint
 */

public class VehiclePushDataPoint implements Comparable<VehiclePushDataPoint>   {
  private Long timestamp;
  private Integer odometer;
  private Integer fuelLevel;
  private Float positionLat;
  private Float positionLong;

  public Long getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Integer getOdometer() {
    return odometer;
  }
  public void setOdometer(Integer odometer) {
    this.odometer = odometer;
  }

  public Integer getFuelLevel() {
    return fuelLevel;
  }
  public void setFuelLevel(Integer fuelLevel) {
    this.fuelLevel = fuelLevel;
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
  public int compareTo(VehiclePushDataPoint dataPoint) {
    return this.getTimestamp().compareTo(dataPoint.getTimestamp());
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehiclePushDataPoint {\n");
    
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    odometer: ").append(toIndentedString(odometer)).append("\n");
    sb.append("    fuelLevel: ").append(toIndentedString(fuelLevel)).append("\n");
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

