package com.rostertwo.trip;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.rostertwo.trip.model.ModelBreak;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.abs;


@RestController
@Component
@RequestMapping("v1")
public class TripApiController {
    
    public static final long EPS = 1;
    private static final Logger log = LoggerFactory.getLogger(TripApiController.class);
    private final VehiclePushAnalysis output = new VehiclePushAnalysis();
    @Value("${APIKEY:defaultValue}")
    private String APIKEY;
    

    @PostMapping("/trip")
    public VehiclePushAnalysis analyze(@RequestBody VehiclePush input) throws Exception {
        ArrayList<VehiclePushDataPoint> dataPoints = input.getData();
        Collections.sort(dataPoints);
        LatLng departureLatLng = new LatLng(dataPoints.get(0).getPositionLat(), dataPoints.get(0).getPositionLong());
        log.debug("Start city/location geoposition: {}", departureLatLng.toString());
        LatLng destinationLatLng = new LatLng(dataPoints.get(dataPoints.size()-1).getPositionLat(), dataPoints.get(dataPoints.size()-1).getPositionLong());
        log.debug("End city/location geoposition: {}", destinationLatLng.toString());
        String departure, destination, compoundCode;
        String vin = input.getVin();
        int breakThreshold = input.getBreakThreshold();
        int gasTankSize = input.getGasTankSize();
        log.info("VIN: {}", vin);
        log.info("Break threshold: {}", breakThreshold);
        log.info("Gas tank size: {}", gasTankSize);
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(APIKEY).build();
            log.info("GeoApiContext build complete");
            GeocodingResult[] departureResults = GeocodingApi.reverseGeocode(context, departureLatLng).await();
            compoundCode = departureResults[0].plusCode.compoundCode;
            departure = compoundCode.substring(compoundCode.indexOf(" ")+1, compoundCode.indexOf(","));
            log.info("Start city/location: {}", departure);
            GeocodingResult[] destinationResults = GeocodingApi.reverseGeocode(context, destinationLatLng).await();
            compoundCode = destinationResults[0].plusCode.compoundCode;
            destination = compoundCode.substring(compoundCode.indexOf(" ")+1, compoundCode.indexOf(","));
            log.info("End city/location: {}", destination);
            output.setDeparture(departure);
            output.setDestination(destination);
            output.setVin(vin);
            float totalDistance = dataPoints.get(dataPoints.size()-1).getOdometer() - dataPoints.get(0).getOdometer();
            log.info("Total distance: {}", totalDistance);
    
            ArrayList<ModelBreak> breaks = new ArrayList<>();
            ArrayList<ModelBreak> refuelStops = new ArrayList<>();
            VehiclePushDataPoint prevDataPoint = dataPoints.get(0);
            float totalFuelConsumption = 0;
            for (VehiclePushDataPoint dataPoint: dataPoints) {
                ModelBreak tempModelBreak = new ModelBreak(prevDataPoint.getTimestamp(), dataPoint.getTimestamp(), dataPoint.getPositionLat(), dataPoint.getPositionLong());
                if (abs(dataPoint.getFuelLevel() - prevDataPoint.getFuelLevel()) >= EPS &&
                    abs(dataPoint.getOdometer() - prevDataPoint.getOdometer()) < EPS &&
                    (dataPoint.getTimestamp() - prevDataPoint.getTimestamp() >= breakThreshold)) {
                    breaks.add(tempModelBreak);
                    refuelStops.add(tempModelBreak);
                    log.debug("Refuel stop and Break: {}", tempModelBreak.toString());
                } else if (abs(dataPoint.getFuelLevel() - prevDataPoint.getFuelLevel()) >= EPS &&
                    abs(dataPoint.getOdometer() - prevDataPoint.getOdometer()) < EPS &&
                    (dataPoint.getTimestamp() - prevDataPoint.getTimestamp() < breakThreshold)) {
                    refuelStops.add(tempModelBreak);
                    log.debug("Refuel stop: {}", tempModelBreak.toString());
                } else if (abs(dataPoint.getFuelLevel() - prevDataPoint.getFuelLevel()) < EPS &&
                    abs(dataPoint.getOdometer() - prevDataPoint.getOdometer()) < EPS &&
                    (dataPoint.getTimestamp() - prevDataPoint.getTimestamp() >= breakThreshold)) {
                    breaks.add(tempModelBreak);
                    log.debug("Break: {}", tempModelBreak.toString());
                }
                totalFuelConsumption += ((float) gasTankSize * abs(dataPoint.getFuelLevel() - prevDataPoint.getFuelLevel())) / 100;
                prevDataPoint = dataPoint;
            }
            output.setBreaks(breaks);
            output.setRefuelStops(refuelStops);
            
            log.info("Total fuel consumption: {}", totalFuelConsumption);
            float avgFuelConsumption = 100 * totalFuelConsumption / totalDistance;
            BigDecimal bd = new BigDecimal(avgFuelConsumption).setScale(1, RoundingMode.HALF_EVEN);
            log.info("Average fuel consumption: {}", bd.floatValue());
            output.setConsumption(bd.floatValue());
            
        } catch (Exception e) {
            throw new Exception("Error in reverse geocoding", e);
        }

        return output;
    }

}
