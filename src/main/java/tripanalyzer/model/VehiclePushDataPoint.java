package tripanalyzer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiclePushDataPoint implements Comparable<VehiclePushDataPoint> {
  private Long timestamp;
  private Integer odometer;
  private Integer fuelLevel;
  private Float positionLat;
  private Float positionLong;

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
