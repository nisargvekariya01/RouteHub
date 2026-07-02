package tests;

import app.CityMap;
import models.Location;
import strategies.matching.AStarNavigationStrategy;
import strategies.matching.DijkstraNavigationStrategy;
import strategies.matching.NavigationStrategy;
import java.util.Map;
import java.util.Random;
import utils.AccurateDistanceCalculator;

public class AlgorithmBenchmark {
    public static void main(String[] args) {
        System.out.println("Initializing CityMap (Loading nodes and edges)...");
        long startInit = System.currentTimeMillis();
        CityMap map = CityMap.getInstance();
        long endInit = System.currentTimeMillis();
        System.out.println("Map loaded in " + (endInit - startInit) + " ms.");
        System.out.println("Total Nodes: " + map.getNodes().size());
        
        runSnappingBenchmark(map);

        // Delhi bounding box is roughly 28.54 77.14 28.7 77.3
        // Pick the absolute farthest corners to maximize algorithmic load
        Location pickup = new Location(28.54, 77.14);   // Bottom-left corner
        Location dropoff = new Location(28.7, 77.3);  // Top-right corner
        
        System.out.println("\nBenchmarking Route: ");
        System.out.println("From: " + pickup.getLatitude() + ", " + pickup.getLongitude());
        System.out.println("To:   " + dropoff.getLatitude() + ", " + dropoff.getLongitude());
        System.out.println("-----------------------------------------------------");

        int iterations = 20;

        // --- DIJKSTRA ALGORITHM ---
        NavigationStrategy dijkstra = new DijkstraNavigationStrategy(map);
        System.out.println("Starting Dijkstra's Algorithm (" + iterations + " iterations)...");
        
        double dijkstraDistance = 0;
        double totalDijkstraDurationMs = 0;
        
        for (int i = 0; i < iterations; i++) {
            long startDijkstra = System.nanoTime();
            dijkstraDistance = dijkstra.getShortestPathDistance(pickup, dropoff);
            long endDijkstra = System.nanoTime();
            totalDijkstraDurationMs += (endDijkstra - startDijkstra) / 1_000_000.0;
        }
        
        double avgDijkstraMs = totalDijkstraDurationMs / iterations;
        System.out.println("=> Dijkstra Result Distance: " + String.format("%.2f", dijkstraDistance) + " km");
        System.out.println("=> Dijkstra Avg Exec Time:   " + String.format("%.2f", avgDijkstraMs) + " ms");
        
        System.out.println("-----------------------------------------------------");

        // --- A* ALGORITHM ---
        NavigationStrategy aStar = new AStarNavigationStrategy();
        System.out.println("Starting A* Algorithm (" + iterations + " iterations)...");
        
        double aStarDistance = 0;
        double totalAStarDurationMs = 0;
        
        for (int i = 0; i < iterations; i++) {
            long startAStar = System.nanoTime();
            aStarDistance = aStar.getShortestPathDistance(pickup, dropoff);
            long endAStar = System.nanoTime();
            totalAStarDurationMs += (endAStar - startAStar) / 1_000_000.0;
        }
        
        double avgAStarMs = totalAStarDurationMs / iterations;
        System.out.println("=> A* Result Distance: " + String.format("%.2f", aStarDistance) + " km");
        System.out.println("=> A* Avg Exec Time:     " + String.format("%.2f", avgAStarMs) + " ms");
        
        System.out.println("-----------------------------------------------------");
        
        // Print comparison
        if (Math.abs(dijkstraDistance - aStarDistance) < 0.001) {
            System.out.println("Both algorithms correctly found the exact same optimal distance.");
        } else {
            System.out.println("WARNING: Results differ! Dijkstra=" + dijkstraDistance + " vs A*=" + aStarDistance);
        }
        
        System.out.println("On average, A* was " + String.format("%.2f", (avgDijkstraMs / avgAStarMs)) + "x faster than Dijkstra.");
    }

    private static void runSnappingBenchmark(CityMap map) {
        System.out.println("\n=====================================================");
        System.out.println("  QuadTree vs Linear Scan Benchmark (2,000 probes) ");
        System.out.println("=====================================================");
        
        int numProbes = 2000;
        Random random = new Random(42);
        
        double minLat = map.getMinLat();
        double maxLat = map.getMaxLat();
        double minLon = map.getMinLon();
        double maxLon = map.getMaxLon();

        Location[] probes = new Location[numProbes];
        for (int i = 0; i < numProbes; i++) {
            // Spawn probes within the strict bounding box of the map
            double lat = minLat + (maxLat - minLat) * random.nextDouble();
            double lon = minLon + (maxLon - minLon) * random.nextDouble();
            probes[i] = new Location(lat, lon);
        }

        // 1. Linear Scan
        System.out.println("Running Linear Scan...");
        long startLinear = System.nanoTime();
        for (Location probe : probes) {
            String nearestId = null;
            double minDistance = Double.MAX_VALUE;
            for (Map.Entry<String, Location> entry : map.getNodes().entrySet()) {
                double dist = AccurateDistanceCalculator.calculateDistance(probe, entry.getValue());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestId = entry.getKey();
                }
            }
        }
        long endLinear = System.nanoTime();
        double avgLinearNs = (endLinear - startLinear) / (double) numProbes;

        // 2. QuadTree Scan (now used internally by CityMap.snapToNearestNode)
        System.out.println("Running QuadTree Scan...");
        long startQuad = System.nanoTime();
        for (Location probe : probes) {
            map.snapToNearestNode(probe);
        }
        long endQuad = System.nanoTime();
        double avgQuadNs = (endQuad - startQuad) / (double) numProbes;

        System.out.println("-----------------------------------------------------");
        System.out.println("Method         Avg per probe       Complexity");
        System.out.println("Linear scan    " + String.format("%,.0f", avgLinearNs) + " ns       O(N)");
        System.out.println("QuadTree       " + String.format("%,.0f", avgQuadNs) + " ns       O(log N)");
        System.out.println("-----------------------------------------------------");
        System.out.println("At N = " + map.getNodes().size() + " nodes the QuadTree is " + 
                           String.format("%.1f", (avgLinearNs / avgQuadNs)) + "x faster than a linear scan.");
        System.out.println("=====================================================\n");
    }
}
