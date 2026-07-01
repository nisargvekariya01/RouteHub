package app;

import factories.NotificationFactory;
import models.Location;
import models.Passenger;
import models.Vehicle;
import models.VehicleType;
import repositories.DriverRepository;
import repositories.RideRepository;
import repositories.UserRepository;
import services.DriverService;
import services.PaymentService;
import services.RideService;
import services.UserService;
import services.notifications.NotificationService;
import strategies.NearestDriverStrategy;
import strategies.StandardFareStrategy;
import strategies.UpiPayment;

/**
 * Main class for the RideShareEngine application.
 * 
 * DEPENDENCY INJECTION (MANUAL) & COMPOSITION ROOT:
 * This acts as the Application "Composition Root". Rather than services instantiating their own 
 * dependencies (which creates rigid, untestable coupling), we instantiate ALL dependencies 
 * here at the very top level and inject them downwards via constructors. 
 * 
 * This explicit wiring beautifully illustrates how the patterns seamlessly integrate:
 * 1. Grab Singleton Repositories.
 * 2. Inject Repositories into Strategy Implementations.
 * 3. Inject Strategies and Repositories into Services.
 * 4. Use a Factory to build an Observer, and inject that Observer into the RideService.
 */
public class RideShareEngine {
    public static void main(String[] args) {
        System.out.println("Starting RideShare Engine (Initialization via Manual DI)...\n");

        // 1. Singleton Repositories
        UserRepository userRepository = UserRepository.getInstance();
        DriverRepository driverRepository = DriverRepository.getInstance();
        RideRepository rideRepository = RideRepository.getInstance();

        // 2. Services Initialization (Injecting Repositories)
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

        System.out.println("All services successfully wired using Dependency Injection!\n");

        // --- SIMULATING A REAL WORLD SCENARIO ---
        
        System.out.println("--- 1. Registering Users ---");
        Passenger p1 = userService.registerPassenger("Nisarg", "555-0100");
        models.Driver d1 = driverService.registerDriver("Alice", "555-0200", new Vehicle("ABC-123", VehicleType.SEDAN));
        
        // Setup Driver State
        d1.goOnline();
        d1.setCurrentLocation(new Location(10.0, 10.0)); 

        System.out.println("\n--- 2. Requesting a Ride (Uses Builder Pattern natively) ---");
        Location pickup = new Location(10.1, 10.1);
        Location dropoff = new Location(20.0, 20.0);
        
        // Triggers the Strategy matching and Observer notification
        models.Ride ride = rideService.requestRide(p1, pickup, dropoff);

        System.out.println("\n--- 3. Starting the Ride ---");
        rideService.startRide(ride.getId(), d1.getId());

        System.out.println("\n--- 4. Completing the Ride ---");
        rideService.completeRide(ride.getId(), false);

        System.out.println("\n--- 5. Processing Payment (Uses Strategy Pattern) ---");
        models.Payment payment = paymentService.createPayment(ride.getId(), ride.getFare());
        paymentService.processPayment(payment, new UpiPayment());

        System.out.println("\n--- 6. Generating Ratings & History ---");
        driverService.rateDriver(d1.getId(), 5);
        userService.ratePassenger(p1.getId(), 5);
        
        p1.displayHistory();
        System.out.println();
        d1.displayHistory();
    }
}
