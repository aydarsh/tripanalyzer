package tripanalyzer.geocoding;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GoogleMapsGeocoder implements TripGeocoder {
    private final GeoApiContext context;

    @Override
    public String city(double latitude, double longitude) {
        try {
            return cityFrom(GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude)).await());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Geocoding interrupted", e);
        } catch (ApiException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Geocoding failed", e);
        }
    }

    static String cityFrom(GeocodingResult[] results) {
        for (var result : results) {
            if (result.plusCode == null || result.plusCode.compoundCode == null) continue;
            String code = result.plusCode.compoundCode;
            int space = code.indexOf(' ');
            int comma = code.indexOf(',');
            if (space >= 0 && comma > space + 1) return code.substring(space + 1, comma);
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Geocoder returned no city");
    }
}
