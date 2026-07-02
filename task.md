# Implement Spatial Gridding (Sector System)

- `[x]` Create `src/utils/SpatialGrid.java`
  - Implement a 2D grid index using a HashMap mapping string coordinates (e.g., `"12_15"`) to Lists of Drivers.
  - Implement fetching from the center grid and the 8 surrounding adjacent grids.
- `[x]` Update `src/services/DriverService.java`
  - Add `updateDriverLocation(driverId, location)` to keep `SpatialGrid` in sync.
- `[x]` Update `src/strategies/matching/NearestDriverStrategy.java`
  - Fetch candidates directly from `SpatialGrid.getDriversInAdjacentSectors()` instead of full driver repository scan.
- `[x]` Update `src/app/ConsoleDashboard.java`
  - Refactor `demo` and `goOnline` commands to use `driverService.updateDriverLocation()`.
- `[x]` Compile and verify via `demo` command.
