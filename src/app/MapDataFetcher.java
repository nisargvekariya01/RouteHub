package app;

import models.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to fetch real road network data from the Overpass API (OpenStreetMap).
 * Features intelligent local caching to prevent rate-limiting, and parses OSM tags 
 * to calculate travel-time (seconds) instead of raw distance.
 */
public class MapDataFetcher {

    private static final String OVERPASS_URL = "https://overpass.kumi.systems/api/interpreter";
    private static final String CACHE_FILE = "delhi_map.json";

    public static void fetchAndSaveMapData() {
        try {
            String json;
            File cache = new File(CACHE_FILE);

            if (cache.exists()) {
                System.out.println("[Map API] Loading map data instantly from local cache (" + CACHE_FILE + ")...");
                json = new String(Files.readAllBytes(Paths.get(CACHE_FILE)), StandardCharsets.UTF_8);
            } else {
                System.out.println("[Map API] Fetching Delhi map data from Overpass API (This will only happen once)...");
                
                // Bounding box for Delhi (28.61,77.19,28.65,77.24)
                String bbox = "28.61,77.19,28.65,77.24";
                String query = "[out:json][timeout:90];way[\"highway\"](" + bbox + ");(._;>;);out body;";

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                URL url = new URL(OVERPASS_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "MiniUberBackend/1.0");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(120000); 

                connection.getOutputStream().write(("data=" + encodedQuery).getBytes(StandardCharsets.UTF_8));

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("[Map API] Failed to fetch map data. HTTP Code: " + responseCode);
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    errorReader.lines().forEach(System.out::println);
                    return;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                in.close();

                json = response.toString();
                
                // Cache the response
                BufferedWriter cacheWriter = new BufferedWriter(new FileWriter(CACHE_FILE));
                cacheWriter.write(json);
                cacheWriter.close();
                System.out.println("[Map API] SUCCESS! Map data cached to " + CACHE_FILE + ".");
            }

            System.out.println("[Map API] Parsing JSON to Graph...");

            // Parse Nodes
            Map<String, Location> nodes = new HashMap<>();
            Pattern nodePattern = Pattern.compile("\"type\":\\s*\"node\".*?\"id\":\\s*(\\d+).*?\"lat\":\\s*([\\d\\.-]+).*?\"lon\":\\s*([\\d\\.-]+)", Pattern.DOTALL);
            Matcher nodeMatcher = nodePattern.matcher(json);
            while (nodeMatcher.find()) {
                nodes.put(nodeMatcher.group(1), new Location(
                        Double.parseDouble(nodeMatcher.group(2)),
                        Double.parseDouble(nodeMatcher.group(3))
                ));
            }

            // Parse Ways (Edges)
            List<String> edges = new ArrayList<>();
            // Split the JSON into blocks representing each 'way' to avoid complex nested regex limits
            String[] ways = json.split("\"type\":\\s*\"way\"");
            
            for (int i = 1; i < ways.length; i++) {
                String wayBody = ways[i];
                
                // 1. Extract the nodes array for this road segment
                Matcher nodesMatcher = Pattern.compile("\"nodes\":\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(wayBody);
                if (!nodesMatcher.find()) continue;
                String[] wayNodes = nodesMatcher.group(1).replaceAll("\\s+", "").split(",");
                
                // 2. Extract the highway tag to determine speed
                String highwayType = "default";
                Matcher tagMatcher = Pattern.compile("\"highway\":\\s*\"([^\"]+)\"").matcher(wayBody);
                if (tagMatcher.find()) {
                    highwayType = tagMatcher.group(1);
                }
                
                double speedKmh = getSpeedProfileKmh(highwayType);
                double speedMs = speedKmh * (5.0 / 18.0); // Convert km/h to m/s

                // Create bidirectional edges weighted by TRAVEL TIME
                for (int j = 0; j < wayNodes.length - 1; j++) {
                    String u = wayNodes[j];
                    String v = wayNodes[j+1];
                    if (nodes.containsKey(u) && nodes.containsKey(v)) {
                        double distKm = calculateDistance(nodes.get(u), nodes.get(v));
                        double distMeters = distKm * 1000.0;
                        
                        // Edge weight is now TIME (seconds) instead of distance
                        double travelTimeSeconds = distMeters / speedMs;
                        
                        // We still store them as edge lengths, but the 'length' now represents time.
                        edges.add(u + "," + v + "," + travelTimeSeconds);
                        edges.add(v + "," + u + "," + travelTimeSeconds); 
                    }
                }
            }

            // Save to CSV
            BufferedWriter nodeWriter = new BufferedWriter(new FileWriter("map_nodes.csv"));
            for (Map.Entry<String, Location> entry : nodes.entrySet()) {
                nodeWriter.write(entry.getKey() + "," + entry.getValue().getLatitude() + "," + entry.getValue().getLongitude() + "\n");
            }
            nodeWriter.close();

            BufferedWriter edgeWriter = new BufferedWriter(new FileWriter("map_edges.csv"));
            for (String edge : edges) {
                edgeWriter.write(edge + "\n");
            }
            edgeWriter.close();

            System.out.println("[Map API] Successfully saved " + nodes.size() + " nodes and " + edges.size() + " travel-time weighted edges to CSV!");

        } catch (Exception e) {
            System.out.println("[Map API] Error fetching map data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static double getSpeedProfileKmh(String highway) {
        switch (highway.toLowerCase()) {
            case "motorway": return 70.0;
            case "trunk": return 60.0;
            case "primary": return 40.0;
            case "secondary": return 30.0;
            case "tertiary": return 25.0;
            case "residential": return 15.0;
            case "unclassified": return 15.0;
            default: return 20.0;
        }
    }

    private static double calculateDistance(Location loc1, Location loc2) {
        // Haversine formula for distance in kilometers
        double R = 6371.0;
        double dLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double dLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude())) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static void main(String[] args) {
        fetchAndSaveMapData();
    }
}
