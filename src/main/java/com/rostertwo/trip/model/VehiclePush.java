package com.rostertwo.trip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * VehiclePush
 */
public class VehiclePush {
  private String vin;
  private Integer breakThreshold;
  private Integer gasTankSize ;
  private ArrayList<VehiclePushDataPoint> data;
  
  public String getVin() {
    return vin;
  }
  public void setVin(String vin) {
    this.vin = vin;
  }
  
  public Integer getBreakThreshold() {
    return breakThreshold;
  }
  public void setBreakThreshold(Integer breakThreshold) {
    this.breakThreshold = breakThreshold;
  }
  
  public Integer getGasTankSize() {
    return gasTankSize;
  }
  public void setGasTankSize(Integer gasTankSize) {
    this.gasTankSize = gasTankSize;
  }
  
  public VehiclePush addDataItem(VehiclePushDataPoint dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<VehiclePushDataPoint>();
    }
    this.data.add(dataItem);
    return this;
  }
  
  public ArrayList<VehiclePushDataPoint> getData() {
    return data;
  }
  public void setData(ArrayList<VehiclePushDataPoint> data) {
    this.data = data;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehiclePush {\n");
    
    sb.append("    vin: ").append(toIndentedString(vin)).append("\n");
    sb.append("    breakThreshold: ").append(toIndentedString(breakThreshold)).append("\n");
    sb.append("    gasTankSize: ").append(toIndentedString(gasTankSize)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
