package app;

import models.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to fetch real road network data from the Overpass API (OpenStreetMap).
 * Downloads raw JSON, parses it using plain Java Regex (no frameworks), and saves 
 * cleaned graph data to CSV files for the CityMap to consume.
 */
public class MapDataFetcher {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

    public static void fetchAndSaveMapData() {
        System.out.println("[Map API] Fetching real street network from Overpass API (Manhattan bounding box)...");
        try {
            // Expanded bounding box for Manhattan to fetch ~10,000+ intersections
            String query = "[out:json][timeout:90];" +
                           "way[\"highway\"~\"primary|secondary|tertiary|residential\"](40.72,-74.01,40.78,-73.95);" +
                           "out body;" +
                           ">;" +
                           "out skel qt;";

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            URL url = new URL(OVERPASS_URL + "?data=" + encodedQuery);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "MiniUberBackend/1.0");
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(120000); // 120 seconds read timeout

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("[Map API] Failed to fetch map data. HTTP Code: " + responseCode);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();

            String json = response.toString();
            
            BufferedWriter debugWriter = new BufferedWriter(new FileWriter("overpass_debug.json"));
            debugWriter.write(json);
            debugWriter.close();

            System.out.println("[Map API] Data downloaded. Parsing JSON to Graph...");

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
            Pattern wayPattern = Pattern.compile("\"type\":\\s*\"way\".*?\"nodes\":\\s*\\[(.*?)\\]", Pattern.DOTALL);
            Matcher wayMatcher = wayPattern.matcher(json);
            while (wayMatcher.find()) {
                String[] wayNodes = wayMatcher.group(1).replaceAll("\\s+", "").split(",");
                for (int i = 0; i < wayNodes.length - 1; i++) {
                    String u = wayNodes[i];
                    String v = wayNodes[i+1];
                    if (nodes.containsKey(u) && nodes.containsKey(v)) {
                        double dist = calculateDistance(nodes.get(u), nodes.get(v));
                        edges.add(u + "," + v + "," + dist);
                        edges.add(v + "," + u + "," + dist); // Undirected graph for simplicity
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

            System.out.println("[Map API] Successfully saved " + nodes.size() + " nodes and " + edges.size() + " edges to CSV!");

        } catch (Exception e) {
            System.out.println("[Map API] Error fetching map data: " + e.getMessage());
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
