package com.rostertwo.trip;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.rostertwo.trip.model.VehiclePush;
import com.rostertwo.trip.model.VehiclePushAnalysis;
import com.rostertwo.trip.model.VehiclePushDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


@RestController
@Component
@RequestMapping("v1")
public class TripApiController {
    
    private static final Logger log = LoggerFactory.getLogger(TripApiController.class);
    private final VehiclePushAnalysis output = new VehiclePushAnalysis();
    @Value("${APIKEY:defaultkey}")
    private String APIKEY;
    
    @PostMapping("/trip")
    public VehiclePushAnalysis analyze(@RequestBody VehiclePush input) throws Exception {
        log.info("Value from @Value: {}", APIKEY);
        ArrayList<VehiclePushDataPoint> dataPoints = input.getData();
        Collections.sort(dataPoints);
        LatLng departureLatLng = new LatLng(dataPoints.get(0).getPositionLat(), dataPoints.get(0).getPositionLong());
        LatLng destinationLatLng = new LatLng(dataPoints.get(dataPoints.size()-1).getPositionLat(), dataPoints.get(dataPoints.size()-1).getPositionLong());
        String departure, destination, compoundCode;
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(APIKEY).build();
            GeocodingResult[] departureResults = GeocodingApi.reverseGeocode(context, departureLatLng).await();
            compoundCode = departureResults[0].plusCode.compoundCode;
            departure = compoundCode.substring(compoundCode.indexOf(" ") + 1, compoundCode.indexOf(","));
            GeocodingResult[] destinationResults = GeocodingApi.reverseGeocode(context, destinationLatLng).await();
            compoundCode = destinationResults[0].plusCode.compoundCode;
            destination = compoundCode.substring(compoundCode.indexOf(" ") + 1, compoundCode.indexOf(","));
            output.setDeparture(departure);
            output.setDestination(destination);
            output.setVin(input.getVin());
            float totalDistance = dataPoints.get(dataPoints.size() - 1).getOdometer() - dataPoints.get(0).getOdometer();
            float totalFuelConsumption = dataPoints.get(0).getFuelLevel() - dataPoints.get(dataPoints.size() - 1).getFuelLevel();
            float avgFuelConsumption = 100 * totalFuelConsumption / totalDistance;
            BigDecimal bd = new BigDecimal(avgFuelConsumption).setScale(1, RoundingMode.HALF_EVEN);
            output.setConsumption(bd.floatValue());
            
        } catch (Exception e) {
            throw new Exception("Error in Reverse Geocoding");
        }

        return output;
    }

}
