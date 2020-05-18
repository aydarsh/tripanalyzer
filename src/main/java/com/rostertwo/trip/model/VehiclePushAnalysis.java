package com.rostertwo.trip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * VehiclePushAnalysis
 */

public class VehiclePushAnalysis {
  private String vin;
  private String departure;
  private String destination;
  private ArrayList<ModelBreak> refuelStops = null;
  private Float consumption;
  private ArrayList<ModelBreak> breaks = null;

  public String getVin() {
    return vin;
  }
  public void setVin(String vin) {
    this.vin = vin;
  }

  public String getDeparture() {
    return departure;
  }
  public void setDeparture(String departure) {
    this.departure = departure;
  }

  public String getDestination() {
    return destination;
  }
  public void setDestination(String destination) {
    this.destination = destination;
  }

  public VehiclePushAnalysis addRefuelStopsItem(ModelBreak refuelStopsItem) {
    if (this.refuelStops == null) {
      this.refuelStops = new ArrayList<ModelBreak>();
    }
    this.refuelStops.add(refuelStopsItem);
    return this;
  }

  public List<ModelBreak> getRefuelStops() {
    return refuelStops;
  }
  public void setRefuelStops(ArrayList<ModelBreak> refuelStops) {
    this.refuelStops = refuelStops;
  }

  public Float getConsumption() {
    return consumption;
  }
  public void setConsumption(Float consumption) {
    this.consumption = consumption;
  }

   public VehiclePushAnalysis addBreaksItem(ModelBreak breaksItem) {
    if (this.breaks == null) {
      this.breaks = new ArrayList<ModelBreak>();
    }
    this.breaks.add(breaksItem);
    return this;
  }

  public List<ModelBreak> getBreaks() {
    return breaks;
  }
  public void setBreaks(ArrayList<ModelBreak> breaks) {
    this.breaks = breaks;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehiclePushAnalysis {\n");
    
    sb.append("    vin: ").append(toIndentedString(vin)).append("\n");
    sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
    sb.append("    destination: ").append(toIndentedString(destination)).append("\n");
    sb.append("    refuelStops: ").append(toIndentedString(refuelStops)).append("\n");
    sb.append("    consumption: ").append(toIndentedString(consumption)).append("\n");
    sb.append("    breaks: ").append(toIndentedString(breaks)).append("\n");
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

