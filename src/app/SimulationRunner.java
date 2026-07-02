package app;

import models.Location;
import models.Passenger;
import models.Vehicle;
import models.enums.VehicleType;
import services.DriverService;
import services.PaymentService;
import services.RideService;
import services.UserService;
import strategies.payment.UpiPayment;

/**
 * Executes a simulated real-world ride-sharing scenario.
 * 
 * CLEAN ARCHITECTURE (Separation of Concerns):
 * Previously, the entire simulation ran directly inside the Main function (RideShareEngine), 
 * muddling application initialization with business flow. By extracting the execution 
 * logic here, we ensure the Main class strictly acts as the Composition Root (wiring only), 
 * while this class exclusively handles orchestrating a simulation.
 */
public class SimulationRunner {

    private final UserService userService;
    private final DriverService driverService;
    private final RideService rideService;
    private final PaymentService paymentService;

    public SimulationRunner(UserService userService, DriverService driverService, 
                            RideService rideService, PaymentService paymentService) {
        this.userService = userService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.paymentService = paymentService;
    }

    public void start() {
        System.out.println("--- 1. Registering Users ---");
        Passenger p1 = userService.registerPassenger("Nisarg", "555-0100");
        models.Driver d1 = driverService.registerDriver("Alice", "555-0200", new Vehicle("ABC-123", "Toyota", "Camry", VehicleType.ECONOMY));
        
        d1.goOnline();
        d1.setCurrentLocation(new Location(10.0, 10.0)); 

        System.out.println("\n--- 2. Requesting a Ride ---");
        Location pickup = new Location(10.1, 10.1);
        Location dropoff = new Location(20.0, 20.0);
        
        models.Ride ride = rideService.confirmRide(p1, pickup, dropoff);

        System.out.println("\n--- 3. Starting the Ride ---");
        rideService.startRide(ride.getId(), d1.getId());

        System.out.println("\n--- 4. Completing the Ride ---");
        rideService.completeRide(ride.getId(), false);

        System.out.println("\n--- 5. Processing Payment ---");
        models.Payment payment = paymentService.createPayment(ride.getId(), ride.getFare());
        paymentService.processPayment(payment, new UpiPayment());

        System.out.println("\n--- 6. Generating Ratings & History ---");
        driverService.rateDriver(d1.getId(), 5);
        
        p1.displayHistory();
        System.out.println();
        d1.displayHistory();
    }
}
