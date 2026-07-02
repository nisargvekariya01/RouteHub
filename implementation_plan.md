# Implement Spatial Gridding (Sector System)

This plan outlines the architecture for introducing a **Spatial Indexing** system to optimize the `NearestDriverStrategy`, moving away from $O(D)$ global linear searches and mimicking how real-world ride-sharing apps (like Uber) use Geohashing/Sectors to achieve lightning-fast matching.

## Open Questions
- Do we want to limit the search radius? (e.g., Only search sectors within a 5km radius. If no driver is found, the app says "No drivers available", instead of searching the entire city). **I will set a default 5km search radius unless you prefer otherwise.**

## Proposed Changes

### 1. `src/utils/SpatialGrid.java`
[NEW] Create a `SpatialGrid` class to manage the spatial index.
- Divides the map into mathematical grid "Sectors" (e.g., 2km x 2km).
- Maintains a `Map<String, Set<Driver>>` where the key is the unique Sector ID (`"latIndex_lonIndex"`).
- Implements `updateDriverPosition(Driver driver)` to move a driver from an old sector to a new one in $O(1)$ time.
- Implements `getDriversInRadius(Location center, double radiusKm)` which instantly fetches drivers from the passenger's sector and immediately adjacent sectors, completely bypassing the global driver list.

### 2. `src/services/DriverService.java`
[MODIFY] Refactor to act as the single source of truth for location updates.
- Add `updateDriverLocation(String driverId, Location newLocation)`.
- When called, it updates the `Driver` model's state and simultaneously updates the `SpatialGrid` index.

### 3. `src/strategies/matching/NearestDriverStrategy.java`
[MODIFY] Refactor to use the Spatial Index.
- Inject `SpatialGrid` into the strategy.
- Instead of iterating over `driverRepository.findAll()`, the strategy will query `spatialGrid.getDriversInRadius(pickupLocation, 5.0)`.
- It will then run the standard Dijkstra routing *only* on this massively reduced subset of local drivers, dropping the algorithm time from $O(D \cdot (V+E)\log V)$ to $O(K \cdot (V+E)\log V)$ where $K$ is a small constant of nearby drivers.

### 4. `src/app/ConsoleDashboard.java`
[MODIFY] Refactor the `demo` command to use `driverService.updateDriverLocation()` instead of manually mutating `d1.setCurrentLocation()`, ensuring the spatial grid is properly populated during the demo.

## Verification Plan
1. **Compile**: Run `compile.ps1` to ensure no syntax errors.
2. **Demo Test**: Run the `demo` command and then execute the suggested `estimateRide` and `confirmRide` commands.
3. **Verify Optimization**: Ensure the console logs output that the Spatial Grid successfully filtered out distant drivers before running Dijkstra.
