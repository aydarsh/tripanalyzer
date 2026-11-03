package tripanalyzer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class VehiclePush {
  private String vin;
  private Integer breakThreshold;
  private Integer gasTankSize;
  private ArrayList<VehiclePushDataPoint> data;

  public VehiclePush addDataItem(VehiclePushDataPoint dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<VehiclePushDataPoint>();
    }
    this.data.add(dataItem);
    return this;
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
