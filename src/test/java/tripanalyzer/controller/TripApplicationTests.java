package tripanalyzer.controller;

import tripanalyzer.geocoding.TripGeocoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"BASICAUTHKEY=test-password", "APIKEY=test-key"})
class TripApplicationTests {
    @TestConfiguration
    static class StubConfig {
        @Bean @Primary
        TripGeocoder testGeocoder() {
            return (latitude, longitude) -> Long.toString(Math.round(latitude));
        }
    }

    @Value("${local.server.port}")
    int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    private CompletableFuture<HttpResponse<String>> post(String input, boolean authenticated) {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v1/trip"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(input));
        if (authenticated) {
            request.header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:test-password".getBytes()));
        }
        return client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"example1", "example2"})
    void matchesExample(String name) throws Exception {
        var input = Files.readString(Path.of("examples", name + ".json"));
        var expected = Files.readString(Path.of("src/test/resources", name + "-expected.json"));
        var response = post(input, true).join();
        assertEquals(200, response.statusCode(), response.body());
        assertEquals(json.readTree(expected), json.readTree(response.body()));
    }

    @Test
    void rejectsUnauthenticatedRequest() {
        assertEquals(401, post("{}", false).join().statusCode());
    }

    @Test
    void sortsPointsAndIncludesBreakAtThreshold() throws Exception {
        var input = (ObjectNode) json.readTree(Files.readString(Path.of("examples/example2.json")));
        input.put("breakThreshold", 600);
        var points = input.withArray("data");
        var first = points.remove(0);
        points.add(first);
        var result = json.readTree(post(input.toString(), true).join().body());
        assertEquals("45", result.get("departure").asString());
        assertEquals(1561119600L, result.get("breaks").get(0).get("endTimestamp").asLong());
    }

    @Test
    void isolatesConcurrentTrips() throws Exception {
        var first = Files.readString(Path.of("examples/example1.json"));
        var second = Files.readString(Path.of("examples/example2.json")).replace("WDD1671591Z000999", "SECOND");
        var a = post(first, true);
        var b = post(second, true);
        var resultA = json.readTree(a.join().body());
        var resultB = json.readTree(b.join().body());
        assertEquals("WDD1671591Z000999", resultA.get("vin").asString());
        assertEquals("SECOND", resultB.get("vin").asString());
        assertEquals(0, resultA.get("breaks").size());
        assertEquals(4, resultB.get("breaks").size());
    }
}
