# RouteHub: Algorithmic Ride-Sharing Backend

![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Zero Dependencies](https://img.shields.io/badge/Dependencies-Zero-brightgreen?style=for-the-badge)
![Algorithms](https://img.shields.io/badge/Algorithms-Dijkstra%20%7C%20A*-blue?style=for-the-badge)
![Spatial Indexing](https://img.shields.io/badge/Data%20Structures-QuadTree-purple?style=for-the-badge)

RouteHub is a high-performance, pure-Java backend routing engine designed to process real-world geographic data, prioritizing mathematical precision over simple coordinate geometry. 

Built from the ground up with strict adherence to Clean Architecture and SOLID principles, the engine ingests geographical maps of New Delhi (99,000+ intersections), constructs a massive graph network, and executes advanced algorithms (A* and Dijkstra) to dispatch vehicles based on true road-network traversal times rather than straight-line estimates.

This repository serves as a showcase of raw algorithmic problem-solving, utilizing zero external frameworks or libraries.

---

## Core Technical Achievements

### 1. Real-World Graph Routing (A* + Dijkstra)
- **Map Ingestion:** The engine natively loads OpenStreetMap (Overpass API) data containing **~99,000 nodes** representing the sprawling streets of New Delhi.
- **Coordinate Snapping (QuadTree):** When a user requests a ride from a random GPS coordinate, the engine uses a custom-built **QuadTree Spatial Index** to aggressively prune the search space and snap their location to the nearest valid road intersection in `O(log N)` time.
- **Algorithmic Dispatching (SpatialGrid + Dijkstra):** Finding the closest driver isn't just about straight-line distance. The `NearestDriverStrategy` first uses a **SpatialGrid** to fetch drivers physically located in the local 6km zone in `O(1)` time. It then executes **Dijkstra's Shortest Path Algorithm** on those local drivers to calculate the actual road-time required to reach the passenger, guaranteeing the dispatch of the mathematically fastest car without wasting CPU cycles on distant drivers.

### 2. Enterprise-Grade System Architecture
The codebase strictly adheres to **SOLID Principles** and leverages industry-standard **Design Patterns** to ensure it is highly scalable and maintainable:
- **Strategy Pattern:** Pricing (`FareStrategy`) and routing algorithms (`NavigationStrategy`) are decoupled. Want to switch from Standard Pricing to Surge Pricing, or from Dijkstra to A* Search? Just swap the strategy object at runtime.
- **Repository Pattern:** All data access is abstracted behind `CrudRepository` interfaces, allowing the backend to seamlessly transition from in-memory lists to a real SQL database in the future without changing business logic.
- **Observer Pattern:** A `NotificationService` actively listens to the `RideService` to broadcast state changes (e.g., "Ride Started", "Payment Processed") via Console/Email/SMS without tightly coupling the systems.
- **Builder Pattern:** Complex `Ride` objects are safely constructed using immutable builders.

### 3. Interactive REPL Dashboard
RouteHub features a custom-built, interactive Command-Line Interface (CLI) that acts as an Admin Dashboard. It parses user input dynamically and provides a seamless "copy-paste" UX for stepping through a complete ride lifecycle (from request, to dispatch, to payment).

---

## Algorithmic Time Complexity
RouteHub is designed to be highly efficient, treating the city as a massive mathematical graph where $V$ is the number of intersections (Nodes) and $E$ is the number of road segments (Edges).

| Operation | Algorithm Used | Time Complexity | Description |
| :--- | :--- | :--- | :--- |
| **Graph Initialization** | Adjacency List Parsing | `O(V + E)` | Reads the raw CSV data into memory, constructs the HashMap-based graph, and populates the QuadTree. |
| **Coordinate Snapping** | QuadTree Search | `O(log V)` | Recursively searches the geographic QuadTree bounding boxes to snap a GPS ping to the nearest intersection. |
| **Estimate Routing** | A* Search / Dijkstra | `O((V + E) log V)` | Uses a `PriorityQueue` (Min-Heap) and Haversine heuristic to calculate the shortest mathematical path. |
| **Dispatch Nearest Driver**| SpatialGrid + Dijkstra | `O(1) + O(L * ((V+E)logV))`| Uses a 2km x 2km bucket grid `O(1)` to fetch local drivers ($L$), then calculates exact driving time to pick the absolute fastest driver. |

---

## Algorithm Benchmark (Dijkstra vs A*)

The routing engine contains a built-in benchmark script to run thousands of calculations across a large map spanning ~100,000 nodes. Here are the 20-iteration average benchmarking results comparing traditional Dijkstra against the optimized A* Search on a 21.48 km diagonal cross-city route:

```text
-----------------------------------------------------
Starting Dijkstra's Algorithm (20 iterations)...
=> Dijkstra Result Distance: 21.48 km
=> Dijkstra Avg Exec Time:   176.28 ms
-----------------------------------------------------
Starting A* Algorithm (20 iterations)...
=> A* Result Distance: 21.48 km
=> A* Avg Exec Time:     108.80 ms
-----------------------------------------------------
Both algorithms correctly found the exact same optimal distance.
On average, A* was 1.62x faster than Dijkstra.
```

---

## Spatial Index Benchmark (QuadTree vs Linear Scan)

Coordinate snapping (finding the nearest road to a GPS ping) is traditionally an `O(N)` operation. We built a custom `QuadTree` to recursively divide the map into geographic quadrants, aggressively pruning the search space to achieve `O(log N)` lookups. Here are the results of 2,000 random pings across the 99,000-node graph:

```text
=====================================================
  QuadTree vs Linear Scan Benchmark (2,000 probes) 
=====================================================
Running Linear Scan...
Running QuadTree Scan...
-----------------------------------------------------
Method         Avg per probe       Complexity
Linear scan    6,915,413 ns       O(N)
QuadTree       9,411 ns           O(log N)
-----------------------------------------------------
At N = 99088 nodes the QuadTree is 734.8x faster than a linear scan.
=====================================================
```

---

## System Architecture Diagram

RouteHub was built from the ground up using **SOLID Principles**. This diagram illustrates the strict decoupling between the Presentation Layer (CLI), the Core Services, and the highly modular Strategy/Repository implementations.

```mermaid
graph TD
    %% Core Styling
    classDef client fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef service fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef strategy fill:#fff3e0,stroke:#f57c00,stroke-width:2px,stroke-dasharray: 5 5;
    classDef repository fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef infrastructure fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px;

    %% Client Layer
    Dashboard["ConsoleDashboard REPL"]:::client

    %% Service Layer
    US[UserService]:::service
    DS[DriverService]:::service
    RS[RideService]:::service
    PS[PaymentService]:::service

    Dashboard --> US
    Dashboard --> DS
    Dashboard --> RS
    Dashboard --> PS

    %% Strategy Layer (Injected dependencies)
    MS["DriverMatchingStrategy<br/><i>(NearestDriver)</i>"]:::strategy
    NS["NavigationStrategy<br/><i>(Dijkstra)</i>"]:::strategy
    FS["FareStrategy<br/><i>(Standard/Luxury)</i>"]:::strategy

    RS --> MS
    RS --> FS
    RS --> NS
    
    MS -.->|Uses| NS

    %% Data & Map Layer
    CM[("CityMap Singleton<br/>99k Nodes / Edges")]:::infrastructure
    QT[{"QuadTree<br/>O(log N) Snapping"}]:::infrastructure
    SG[{"SpatialGrid<br/>O(1) Driver Locating"}]:::infrastructure
    
    NS --> CM
    CM --> QT
    MS --> SG
    MS -.->|Uses| NS

    Repo[("CrudRepositories<br/>InMemory / SQL")]:::repository
    US --> Repo
    DS --> Repo
    RS --> Repo
    
    %% Observer Layer
    Notify["NotificationService<br/><i>Observer Pattern</i>"]:::infrastructure
    RS -.->|Broadcasts Events| Notify
```

---

## Execution Guide

Since RouteHub relies on zero external dependencies, running it is incredibly easy.

### Prerequisites
- Java (JDK 8 or higher)
- A terminal (PowerShell, Bash, Command Prompt)

### Booting the Engine
1. Clone the repository and navigate to the root directory.
2. Compile and launch the application using the included script:
   ```bash
   ./compile.ps1
   ```
3. You will be greeted by the `Admin>` prompt and a confirmation that the 100,000-node graph was successfully loaded into memory.

### Automated Integration Testing
To bypass manual UUID entry and rapidly test the routing engine's lifecycle, invoke the integrated demo script:

```text
Admin> demo
```

The sequence executes the following automated bootstrapping:
1. Registers a mock Passenger profile.
2. Initializes three distinct Driver entities (Economy, SUV, Premium).
3. Distributes the drivers across varying coordinates within the New Delhi bounding box, transitioning their statuses to `ONLINE`.
4. Outputs the precise `estimateRide` command required to invoke the A* routing engine.

Follow the subsequent on-screen terminal instructions to manually step through the state machine transitions (`estimateRide` -> `confirmRide` -> `startRide` -> `completeRide` -> `rateAndPay`).



---

## Project Structure

```text
src/
├── app/                  # Main entry point and Dashboard REPL
├── exceptions/           # Custom domain exceptions (e.g., RideNotFoundException)
├── models/               # Core entities (Passenger, Driver, Ride, Location)
├── observers/            # Notification system implementations
├── repositories/         # Data persistence layer
├── services/             # Core business logic orchestrators
├── strategies/           # Interchangeable algorithms (Pricing, Matching, Routing)
└── utils/                # SpatialGrid, QuadTree, Math Utilities
map_nodes.csv             # ~99,000 New Delhi road intersections (Latitude/Longitude)
map_edges.csv             # Road connections
```

---

## Future Technical Roadmap
- **Multithreading:** Implement a concurrency model to simulate 100+ passengers booking simultaneously, demonstrating thread safety with `ReentrantLocks`.
- **Database Integration:** Swap the `InMemoryRepositories` with `SQLRepositories` using JDBC.
- **Path Divergence Analysis:** Configure A* to optimize for Time (speed limits) and Dijkstra to optimize for Distance (km) to benchmark how often they diverge in a real city.

*Designed and engineered as a showcase of algorithmic efficiency and software design.*
