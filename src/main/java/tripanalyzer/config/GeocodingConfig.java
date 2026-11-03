package tripanalyzer.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
class GeocodingConfig {

    @Bean(destroyMethod = "shutdown")
    GeoApiContext mapsContext(@Value("${APIKEY}") String apiKey) {
        Assert.hasText(apiKey, "APIKEY is required");
        return new GeoApiContext.Builder().apiKey(apiKey).build();
    }
}
