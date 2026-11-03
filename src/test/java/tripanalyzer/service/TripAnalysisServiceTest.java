package tripanalyzer.service;

import tripanalyzer.model.VehiclePush;
import tripanalyzer.model.VehiclePushDataPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TripAnalysisServiceTest {
    @Test
    void analyzesSortedTelemetryWithoutReorderingTheInput() {
        var start = point(100, 1000, 70);
        var stopped = point(160, 1000, 80);
        var end = point(220, 1100, 60);
        var trip = new VehiclePush().addDataItem(end).addDataItem(start).addDataItem(stopped);
        trip.setBreakThreshold(60);
        trip.setGasTankSize(50);

        var service = new TripAnalysisService((latitude, longitude) -> "City");
        var result = service.analyze(trip);

        assertEquals(15f, result.getConsumption());
        assertEquals(1, result.getBreaks().size());
        assertEquals(1, result.getRefuelStops().size());
        assertEquals(100L, result.getBreaks().getFirst().getStartTimestamp());
        assertEquals(160L, result.getRefuelStops().getFirst().getEndTimestamp());
        assertEquals(List.of(end, start, stopped), trip.getData());
    }

    private static VehiclePushDataPoint point(long timestamp, int odometer, int fuelLevel) {
        var point = new VehiclePushDataPoint();
        point.setTimestamp(timestamp);
        point.setOdometer(odometer);
        point.setFuelLevel(fuelLevel);
        point.setPositionLat(50f);
        point.setPositionLong(40f);
        return point;
    }
}
