package strategies.matching;

import app.CityMap;
import models.Location;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Concrete strategy implementing Dijkstra's algorithm to find the absolute 
 * shortest road distance between two snapped points on the CityMap graph.
 */
public class DijkstraNavigationStrategy implements NavigationStrategy {

    private final CityMap cityMap;

    public DijkstraNavigationStrategy(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    @Override
    public double getShortestPathDistance(Location start, Location end) {
        String startNode = cityMap.snapToNearestNode(start);
        String endNode = cityMap.snapToNearestNode(end);

        if (startNode == null || endNode == null) {
            return -1; // Graph is completely empty
        }

        if (startNode.equals(endNode)) {
            return 0.0;
        }

        // Dijkstra's Algorithm Structures
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));

        for (String node : cityMap.getNodes().keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(startNode, 0.0);
        pq.add(new NodeDistance(startNode, 0.0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();

            if (current.nodeId.equals(endNode)) {
                return current.distance;
            }

            if (current.distance > distances.get(current.nodeId)) {
                continue; // Stale path
            }

            for (CityMap.Edge edge : cityMap.getAdjacencyList().get(current.nodeId)) {
                double newDist = distances.get(current.nodeId) + edge.distance;
                if (newDist < distances.get(edge.targetNode)) {
                    distances.put(edge.targetNode, newDist);
                    pq.add(new NodeDistance(edge.targetNode, newDist));
                }
            }
        }

        return -1; // No path exists (e.g. Island node)
    }

    private static class NodeDistance {
        String nodeId;
        double distance;
        NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
