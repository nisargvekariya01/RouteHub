# Real-World Map Ingestion & Travel-Time Weights

The user has requested to pull real OpenStreetMap (OSM) data for Delhi and parse the road types (e.g., motorway, residential) to calculate edge weights as **travel time (seconds)** instead of raw geographical distance, mimicking a production-ready routing engine.

## Proposed Changes

### 1. Enhance `src/app/MapDataFetcher.java`
[MODIFY] Refactor `MapDataFetcher` to exactly mirror the advanced Node.js logic provided by the user, while strictly adhering to the "no frameworks/libraries" rule:
- **Delhi Bounding Box:** Update the Overpass query to `28.61,77.19,28.65,77.24`.
- **JSON Caching:** Implement local caching. The fetcher will save the raw API response to `delhi_map.json`. On subsequent runs, it will load this file instantly to prevent Overpass API rate limits and 504 timeouts.
- **Advanced Regex Parser:** I will write a robust two-pass regex parser in plain Java to extract both the `nodes` array and the `highway` tag from each OSM `way` object.
- **Speed Profiles:** Introduce a `SPEED_PROFILE_KMH` map (motorway=70, residential=15, etc.).
- **Travel Time Edges:** Instead of writing raw Haversine distances to `map_edges.csv`, it will calculate `travelTimeSeconds = (distance_km / speed_kmh) * 3600` and save *time* as the edge weight.

### 2. Update `src/app/ConsoleDashboard.java`
[MODIFY] Because the map will shift from Greater NYC to Delhi, the `demo` command's hardcoded GPS coordinates must be updated to valid Delhi coordinates (e.g., `28.62, 77.20`) so the drivers spawn inside the new graph bounds.

## Verification Plan
1. **Fetch Map:** Run `java -cp bin app.MapDataFetcher` to download the Delhi map, cache the JSON, and generate the time-weighted CSVs.
2. **Start Engine:** Run the engine via `compile.ps1` to load the new Delhi graph.
3. **Demo & Routing:** Execute the `demo` command to verify that Dijkstra successfully optimizes for *time* (seconds) across the Delhi road network.
