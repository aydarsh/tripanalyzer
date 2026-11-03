package tripanalyzer.geocoding;

import com.google.maps.model.GeocodingResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoogleMapsGeocoderTest {
    @Test
    void handlesMissingGeocodingData() {
        var failure = assertThrows(ResponseStatusException.class,
                () -> GoogleMapsGeocoder.cityFrom(new GeocodingResult[]{new GeocodingResult()}));
        assertEquals(502, failure.getStatusCode().value());
    }
}
