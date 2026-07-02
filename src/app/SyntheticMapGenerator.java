package app;

import models.Location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import utils.AccurateDistanceCalculator;

/**
 * Generates a massive synthetic map of 100,000+ nodes to benchmark the Dijkstra routing engine.
 * We create a 317x317 grid covering the Greater NYC area, ensuring demo points still fall inside.
 */
public class SyntheticMapGenerator {

    private static final int GRID_SIZE = 317; // 317 * 317 = 100,489 nodes
    private static final double START_LAT = 40.60;
    private static final double START_LON = -74.15;
    private static final double STEP_SIZE = 0.001; // Roughly 111 meters between nodes

    public static void generate() {
        System.out.println("Generating synthetic benchmark map with " + (GRID_SIZE * GRID_SIZE) + " nodes...");

        try (BufferedWriter nodeWriter = new BufferedWriter(new FileWriter("map_nodes.csv"));
             BufferedWriter edgeWriter = new BufferedWriter(new FileWriter("map_edges.csv"))) {

            // 1. Generate Nodes
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) {
                    String nodeId = "node_" + r + "_" + c;
                    double lat = START_LAT + (r * STEP_SIZE);
                    double lon = START_LON + (c * STEP_SIZE);
                    nodeWriter.write(nodeId + "," + lat + "," + lon + "\n");
                }
            }
            System.out.println("Nodes written to map_nodes.csv");

            // 2. Generate Edges (Horizontal and Vertical connections)
            int edgesCount = 0;
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) {
                    String u = "node_" + r + "_" + c;
                    Location locU = new Location(START_LAT + (r * STEP_SIZE), START_LON + (c * STEP_SIZE));

                    // Connect to Right neighbor (c+1)
                    if (c + 1 < GRID_SIZE) {
                        String v = "node_" + r + "_" + (c + 1);
                        Location locV = new Location(START_LAT + (r * STEP_SIZE), START_LON + ((c + 1) * STEP_SIZE));
                        double dist = AccurateDistanceCalculator.calculateDistance(locU, locV);
                        edgeWriter.write(u + "," + v + "," + dist + "\n");
                        edgeWriter.write(v + "," + u + "," + dist + "\n");
                        edgesCount += 2;
                    }

                    // Connect to Bottom neighbor (r+1)
                    if (r + 1 < GRID_SIZE) {
                        String v = "node_" + (r + 1) + "_" + c;
                        Location locV = new Location(START_LAT + ((r + 1) * STEP_SIZE), START_LON + (c * STEP_SIZE));
                        double dist = AccurateDistanceCalculator.calculateDistance(locU, locV);
                        edgeWriter.write(u + "," + v + "," + dist + "\n");
                        edgeWriter.write(v + "," + u + "," + dist + "\n");
                        edgesCount += 2;
                    }
                }
            }
            System.out.println("Edges written to map_edges.csv. Total directed edges: " + edgesCount);
            System.out.println("Successfully generated 100k Benchmark Map!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        generate();
    }
}
