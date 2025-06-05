package tn.esprit.spring.connectn.Services.Implementation;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.connectn.Entities.Issue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class GeocodingService {
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=";
    private static final double TUNISIA_MIN_LAT = 30.230236;
    private static final double TUNISIA_MAX_LAT = 37.761205;
    private static final double TUNISIA_MIN_LON = 7.521980;
    private static final double TUNISIA_MAX_LON = 11.880625;

    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, GeoLocation> cache = new ConcurrentHashMap<>(1000);
    private final RateLimiter rateLimiter = RateLimiter.create(1.0); // 1 request per second

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
        // Clean cache every 24 hours
        scheduler.scheduleAtFixedRate(cache::clear, 24, 24, TimeUnit.HOURS);
    }

    public void geocodeIssue(Issue issue) {
        try {
            rateLimiter.acquire(); // Respect rate limit

            String fullAddress = buildFullAddress(issue);
            GeoLocation cached = cache.get(fullAddress);
            if (cached != null) {
                setCoordinates(issue, cached);
                return;
            }

            String url = NOMINATIM_API_URL + URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
            ResponseEntity<Map[]> response = makeRequest(url);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().length > 0) {
                Map<String, Object> result = response.getBody()[0];
                double lat = Double.parseDouble(result.get("lat").toString());
                double lon = Double.parseDouble(result.get("lon").toString());

                if (isInTunisia(lat, lon)) {
                    GeoLocation location = new GeoLocation(lat, lon);
                    setCoordinates(issue, location);
                    cache.put(fullAddress, location);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail
            System.err.println("Geocoding failed for issue " + issue.getId() + ": " + e.getMessage());
        }
    }

    private String buildFullAddress(Issue issue) {
        StringBuilder sb = new StringBuilder();
        if (issue.getAddress() != null) sb.append(issue.getAddress());
        if (issue.getCity() != null) sb.append(", ").append(issue.getCity());
        sb.append(", Tunisia");
        return sb.toString();
    }

    private ResponseEntity<Map[]> makeRequest(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TunisiaIssuesApp/1.0");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map[].class);
    }

    private boolean isInTunisia(double lat, double lon) {
        return lat >= TUNISIA_MIN_LAT && lat <= TUNISIA_MAX_LAT &&
                lon >= TUNISIA_MIN_LON && lon <= TUNISIA_MAX_LON;
    }

    private void setCoordinates(Issue issue, GeoLocation location) {
        issue.setLatitude(location.lat);
        issue.setLongitude(location.lon);
    }

    private static class GeoLocation {
        final double lat;
        final double lon;

        GeoLocation(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}