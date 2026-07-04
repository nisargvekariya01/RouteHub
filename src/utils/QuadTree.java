package utils;

import models.Location;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_CAPACITY = 50;

    private final BoundingBox boundary;
    private final List<NodeEntry> nodes;

    private QuadTree northWest;
    private QuadTree northEast;
    private QuadTree southWest;
    private QuadTree southEast;

    private boolean divided = false;

    public static class BoundingBox {
        public double minLat, minLon, maxLat, maxLon;
        public BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
            this.minLat = minLat;
            this.minLon = minLon;
            this.maxLat = maxLat;
            this.maxLon = maxLon;
        }

        public boolean contains(Location loc) {
            return loc.getLatitude() >= minLat && loc.getLatitude() <= maxLat &&
                   loc.getLongitude() >= minLon && loc.getLongitude() <= maxLon;
        }
        
        public boolean intersects(BoundingBox range) {
            return !(range.minLon > maxLon || range.maxLon < minLon || 
                     range.minLat > maxLat || range.maxLat < minLat);
        }
    }

    public static class NodeEntry {
        public String id;
        public Location loc;
        public NodeEntry(String id, Location loc) {
            this.id = id;
            this.loc = loc;
        }
    }

    public QuadTree(BoundingBox boundary) {
        this.boundary = boundary;
        this.nodes = new ArrayList<>(MAX_CAPACITY);
    }

    public boolean insert(String id, Location loc) {
        if (!boundary.contains(loc)) {
            return false;
        }

        if (nodes.size() < MAX_CAPACITY && !divided) {
            nodes.add(new NodeEntry(id, loc));
            return true;
        }

        if (!divided) {
            subdivide();
        }

        if (northWest.insert(id, loc)) return true;
        if (northEast.insert(id, loc)) return true;
        if (southWest.insert(id, loc)) return true;
        if (southEast.insert(id, loc)) return true;

        return false;
    }

    private void subdivide() {
        double midLat = (boundary.minLat + boundary.maxLat) / 2.0;
        double midLon = (boundary.minLon + boundary.maxLon) / 2.0;

        northWest = new QuadTree(new BoundingBox(midLat, boundary.minLon, boundary.maxLat, midLon));
        northEast = new QuadTree(new BoundingBox(midLat, midLon, boundary.maxLat, boundary.maxLon));
        southWest = new QuadTree(new BoundingBox(boundary.minLat, boundary.minLon, midLat, midLon));
        southEast = new QuadTree(new BoundingBox(boundary.minLat, midLon, midLat, boundary.maxLon));

        divided = true;
        
        // Push existing nodes down to children to free up this root node and avoid scanning it
        for (NodeEntry entry : nodes) {
            if (northWest.insert(entry.id, entry.loc)) continue;
            if (northEast.insert(entry.id, entry.loc)) continue;
            if (southWest.insert(entry.id, entry.loc)) continue;
            southEast.insert(entry.id, entry.loc);
        }
        nodes.clear();
    }

    public NearestResult findNearest(Location target, NearestResult currentBest) {
        // Find distance from target to this bounding box
        double distToBox = distanceToBox(target, boundary);
        
        // Pruning step: if this entire quadrant is strictly further than our current best, ignore it
        if (distToBox >= currentBest.distance) {
            return currentBest;
        }

        // Check local nodes
        for (NodeEntry entry : nodes) {
            double dist = AccurateDistanceCalculator.calculateDistance(target, entry.loc);
            if (dist < currentBest.distance) {
                currentBest.distance = dist;
                currentBest.id = entry.id;
            }
        }

        if (divided) {
            // Optimization: visit the quadrant containing the target first, it has highest chance of having nearest neighbor
            QuadTree first = null, second = null, third = null, fourth = null;
            
            boolean targetInNorth = target.getLatitude() >= (boundary.minLat + boundary.maxLat) / 2.0;
            boolean targetInEast = target.getLongitude() >= (boundary.minLon + boundary.maxLon) / 2.0;
            
            if (targetInNorth && !targetInEast) { first = northWest; second = northEast; third = southWest; fourth = southEast; }
            else if (targetInNorth && targetInEast) { first = northEast; second = northWest; third = southEast; fourth = southWest; }
            else if (!targetInNorth && !targetInEast) { first = southWest; second = southEast; third = northWest; fourth = northEast; }
            else { first = southEast; second = southWest; third = northEast; fourth = northWest; }
            
            first.findNearest(target, currentBest);
            second.findNearest(target, currentBest);
            third.findNearest(target, currentBest);
            fourth.findNearest(target, currentBest);
        }

        return currentBest;
    }

    private double distanceToBox(Location target, BoundingBox box) {
        double closestLat = Math.max(box.minLat, Math.min(target.getLatitude(), box.maxLat));
        double closestLon = Math.max(box.minLon, Math.min(target.getLongitude(), box.maxLon));
        return AccurateDistanceCalculator.calculateDistance(target.getLatitude(), target.getLongitude(), closestLat, closestLon);
    }

    public static class NearestResult {
        public String id;
        public double distance;
        public NearestResult(String id, double distance) {
            this.id = id;
            this.distance = distance;
        }
    }
}
