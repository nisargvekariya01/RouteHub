# Real-World Map Ingestion & Travel-Time Weights

- `[x]` Refactor `src/app/MapDataFetcher.java`
  - Implement intelligent caching to `delhi_map.json`.
  - Update Overpass API query with Delhi bounding box (`28.61,77.19,28.65,77.24`).
  - Implement advanced Regex chunking to parse `"highway"` tags alongside nodes.
  - Apply `SPEED_PROFILE_KMH` mapping to convert geographical distance to travel time in seconds.
- `[x]` Update `src/app/ConsoleDashboard.java`
  - Adjust `demo` coordinates to fall within the new Delhi bounding box.
- `[x]` Verify Engine
  - Run `MapDataFetcher` to fetch and parse Delhi.
  - Compile and run dashboard `demo` to verify Dijkstra solves for time.
