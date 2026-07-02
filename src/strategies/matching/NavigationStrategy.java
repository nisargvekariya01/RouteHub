package strategies.matching;

import models.Location;

/**
 * Strategy interface for pathfinding and navigation across the City Map.
 * Decouples the routing algorithm (Dijkstra, A*, etc.) from the matching engine.
 */
public interface NavigationStrategy {
    /**
     * Calculates the shortest real-road distance between two raw coordinates.
     * @param start The raw starting location
     * @param end The raw destination location
     * @return The distance in miles, or -1 if no path exists.
     */
    double getShortestPathDistance(Location start, Location end);
}
