package tripanalyzer.service;

import tripanalyzer.geocoding.TripGeocoder;
import tripanalyzer.model.ModelBreak;
import tripanalyzer.model.VehiclePush;
import tripanalyzer.model.VehiclePushAnalysis;
import tripanalyzer.model.VehiclePushDataPoint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TripAnalysisService {
    private final TripGeocoder geocoder;

    public VehiclePushAnalysis analyze(VehiclePush trip) {
        var points = trip.getData().stream().sorted().toList();
        var first = points.getFirst();
        var last = points.getLast();
        var result = new VehiclePushAnalysis();
        result.setVin(trip.getVin());
        result.setDeparture(geocoder.city(first.getPositionLat(), first.getPositionLong()));
        result.setDestination(geocoder.city(last.getPositionLat(), last.getPositionLong()));

        int tankSize = trip.getGasTankSize();
        int breakThreshold = trip.getBreakThreshold();
        float fuelUsed = 0;
        var previous = first;
        for (var point : points) {
            int fuelChange = Math.abs(point.getFuelLevel() - previous.getFuelLevel());
            fuelUsed += (float) tankSize * fuelChange / 100;
            if (point.getOdometer().equals(previous.getOdometer())) {
                recordStop(result, previous, point, fuelChange, breakThreshold);
            }
            previous = point;
        }

        float distance = last.getOdometer() - first.getOdometer();
        float averageConsumption = 100 * fuelUsed / distance;
        result.setConsumption(new BigDecimal(averageConsumption).setScale(1, RoundingMode.HALF_EVEN).floatValue());
        return result;
    }

    private static void recordStop(VehiclePushAnalysis result, VehiclePushDataPoint previous,
                                   VehiclePushDataPoint point, int fuelChange, int breakThreshold) {
        var stop = new ModelBreak(previous.getTimestamp(), point.getTimestamp(),
                point.getPositionLat(), point.getPositionLong());
        if (fuelChange >= 1) {
            result.addRefuelStopsItem(stop);
        }
        if (point.getTimestamp() - previous.getTimestamp() >= breakThreshold) {
            result.addBreaksItem(stop);
        }
    }
}
