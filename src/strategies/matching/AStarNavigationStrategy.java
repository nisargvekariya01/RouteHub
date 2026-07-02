package strategies.matching;

import app.CityMap;
import models.Location;
import utils.AccurateDistanceCalculator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A* Navigation Strategy.
 * Calculates the shortest road distance between two snapped points on the CityMap graph.
 * Uses a geographical heuristic (straight-line distance) to ensure admissibility.
 */
public class AStarNavigationStrategy implements NavigationStrategy {

    private final CityMap cityMap;

    public AStarNavigationStrategy() {
        this.cityMap = CityMap.getInstance();
    }

    @Override
    public double getShortestPathDistance(Location start, Location end) {
        String startNode = cityMap.snapToNearestNode(start);
        String endNode = cityMap.snapToNearestNode(end);

        if (startNode == null || endNode == null) {
            return -1; // Snap failed (out of bounds)
        }

        if (startNode.equals(endNode)) {
            return 0.0; // Already at destination
        }

        Location destinationLocation = cityMap.getNodes().get(endNode);

        // A* Algorithm Structures
        Map<String, Double> gScores = new HashMap<>(); // Exact travel time from start
        PriorityQueue<NodeAStar> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));

        for (String node : cityMap.getAdjacencyList().keySet()) {
            gScores.put(node, Double.MAX_VALUE);
        }
        
        gScores.put(startNode, 0.0);
        double initialH = heuristic(cityMap.getNodes().get(startNode), destinationLocation);
        pq.add(new NodeAStar(startNode, 0.0, initialH));

        while (!pq.isEmpty()) {
            NodeAStar current = pq.poll();

            if (current.nodeId.equals(endNode)) {
                return current.gScore;
            }

            // If we found a strictly better path to this node before processing this element, skip it
            if (current.gScore > gScores.get(current.nodeId)) {
                continue;
            }

            for (CityMap.Edge edge : cityMap.getAdjacencyList().getOrDefault(current.nodeId, new ArrayList<>())) {
                double tentativeGScore = gScores.get(current.nodeId) + edge.distance;
                
                if (tentativeGScore < gScores.get(edge.targetNode)) {
                    gScores.put(edge.targetNode, tentativeGScore);
                    double hScore = heuristic(cityMap.getNodes().get(edge.targetNode), destinationLocation);
                    double fScore = tentativeGScore + hScore;
                    pq.add(new NodeAStar(edge.targetNode, tentativeGScore, fScore));
                }
            }
        }

        return -1; // No path exists
    }

    /**
     * Calculates an admissible heuristic (h-score).
     * It computes the straight-line Haversine distance to the target in kilometers.
     */
    private double heuristic(Location current, Location target) {
        if (current == null || target == null) return 0.0;
        
        // Distance in kilometers
        return AccurateDistanceCalculator.calculateDistance(
            current.getLatitude(), current.getLongitude(), 
            target.getLatitude(), target.getLongitude()
        );
    }

    private static class NodeAStar {
        String nodeId;
        double gScore; // Exact cost from start to this node
        double fScore; // Estimated total cost from start to end through this node

        NodeAStar(String nodeId, double gScore, double fScore) {
            this.nodeId = nodeId;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
}
