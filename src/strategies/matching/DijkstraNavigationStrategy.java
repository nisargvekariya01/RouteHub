package strategies.matching;

import app.CityMap;
import models.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Advanced Navigation Strategy.
 * Calculates the shortest road travel time between two snapped points on the CityMap graph.
 */
public class DijkstraNavigationStrategy implements NavigationStrategy {

    private final CityMap cityMap;

    public DijkstraNavigationStrategy(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    @Override
    public double getShortestPathTravelTime(Location start, Location end) {
        String startNode = cityMap.snapToNearestNode(start);
        String endNode = cityMap.snapToNearestNode(end);

        if (startNode == null || endNode == null) {
            return -1; // Snap failed (out of bounds)
        }

        if (startNode.equals(endNode)) {
            return 0.0;
        }

        // Dijkstra's Algorithm Structures
        Map<String, Double> times = new HashMap<>();
        PriorityQueue<NodeTime> pq = new PriorityQueue<>(Comparator.comparingDouble(nt -> nt.time));

        for (String node : cityMap.getAdjacencyList().keySet()) {
            times.put(node, Double.MAX_VALUE);
        }
        times.put(startNode, 0.0);
        pq.add(new NodeTime(startNode, 0.0));

        while (!pq.isEmpty()) {
            NodeTime current = pq.poll();

            if (current.nodeId.equals(endNode)) {
                return current.time;
            }

            if (current.time > times.get(current.nodeId)) {
                continue;
            }

            for (CityMap.Edge edge : cityMap.getAdjacencyList().getOrDefault(current.nodeId, new ArrayList<>())) {
                double newTime = times.get(current.nodeId) + edge.travelTimeSeconds;
                if (newTime < times.get(edge.targetNode)) {
                    times.put(edge.targetNode, newTime);
                    pq.add(new NodeTime(edge.targetNode, newTime));
                }
            }
        }

        return -1; // No path exists (e.g. Island node)
    }

    private static class NodeTime {
        String nodeId;
        double time;

        NodeTime(String nodeId, double time) {
            this.nodeId = nodeId;
            this.time = time;
        }
    }
}
