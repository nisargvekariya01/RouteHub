package app;

import factories.NotificationFactory;
import repositories.DriverRepository;
import repositories.InMemoryDriverRepository;
import repositories.InMemoryRideRepository;
import repositories.InMemoryUserRepository;
import repositories.RideRepository;
import repositories.UserRepository;
import services.DriverService;
import services.PaymentService;
import services.RideService;
import services.UserService;
import observers.NotificationService;
import strategies.matching.NearestDriverStrategy;
import strategies.pricing.StandardFareStrategy;

/**
 * Entry point of the RideShareEngine application.
 * 
 * CLEAN ARCHITECTURE (Composition Root):
 * This class acts entirely as the Composition Root. It manually wires up every Singleton, 
 * Factory, Strategy, and Service by explicitly relying on Dependency Inversion (DIP). 
 * 
 * Notice that Services are injected with the `UserRepository` interface, not the 
 * concrete `InMemoryUserRepository` singleton.
 * 
 * After wiring, it relinquishes execution control to the Presentation Layer (ConsoleDashboard).
 */
public class RideShareEngine {
    public static void main(String[] args) {
        // 1. Singleton Repositories (Stored as Interfaces for Dependency Inversion)
        UserRepository userRepository = InMemoryUserRepository.getInstance();
        DriverRepository driverRepository = InMemoryDriverRepository.getInstance();
        RideRepository rideRepository = InMemoryRideRepository.getInstance();

        // 2. Services Initialization
        UserService userService = new UserService(userRepository);
        DriverService driverService = new DriverService(driverRepository);
        PaymentService paymentService = new PaymentService();

        // 3. Strategy Implementations
        NearestDriverStrategy nearestDriverStrategy = new NearestDriverStrategy(driverRepository);
        StandardFareStrategy standardFareStrategy = new StandardFareStrategy();

        // 4. Injecting Dependencies into RideService
        RideService rideService = new RideService(rideRepository, nearestDriverStrategy, standardFareStrategy);

        // 5. Factory Pattern + Observer Pattern Wiring
        NotificationService consoleNotifier = NotificationFactory.createNotificationService(NotificationFactory.NotificationType.CONSOLE);
        rideService.addNotificationObserver(consoleNotifier);

        // 6. Launch Presentation Layer
        ConsoleDashboard dashboard = new ConsoleDashboard(userService, driverService, rideService, paymentService);
        dashboard.start();
    }
}
