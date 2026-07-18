package app;

import exceptions.RideShareException;
import java.util.Scanner;
import models.Driver;
import models.Location;
import models.Passenger;
import models.Payment;
import models.Ride;
import models.User;
import models.Vehicle;
import models.enums.VehicleType;
import services.DriverService;
import services.PaymentService;
import services.RideService;
import services.UserService;
import strategies.payment.CardPayment;
import strategies.payment.CashPayment;
import strategies.payment.PaymentMethod;
import strategies.payment.UpiPayment;
import utils.AccurateDistanceCalculator;

/**
 * Interactive Command-Line Dashboard for the RideShare Engine.
 * 
 * CLEAN ARCHITECTURE (Presentation Layer):
 * This class isolates all user interaction and CLI parsing from the underlying business logic.
 * It strictly relies on the Service Layer (UserService, DriverService, RideService) to execute commands.
 * If we ever decide to build a REST API or a Mobile App, the underlying services remain untouched,
 * adhering to the Single Responsibility Principle and Separation of Concerns.
 */
public class ConsoleDashboard {

    private final UserService userService;
    private final DriverService driverService;
    private final RideService rideService;
    private final PaymentService paymentService;

    public ConsoleDashboard(UserService userService, DriverService driverService, 
                            RideService rideService, PaymentService paymentService) {
        this.userService = userService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.paymentService = paymentService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=====================================================");
        System.out.println("        MINI UBER BACKEND - ADMIN DASHBOARD          ");
        System.out.println("=====================================================");
        
        app.CityMap map = app.CityMap.getInstance();
        
        double widthKm = AccurateDistanceCalculator.calculateDistance(map.getMinLat(), map.getMinLon(), map.getMinLat(), map.getMaxLon());
        double heightKm = AccurateDistanceCalculator.calculateDistance(map.getMinLat(), map.getMinLon(), map.getMaxLat(), map.getMinLon());
        double areaKm2 = widthKm * heightKm;

        System.out.println("Map Bounds Loaded:");
        System.out.println("  Bottom-Left (Min): " + map.getMinLat() + ", " + map.getMinLon());
        System.out.println("  Top-Right   (Max): " + map.getMaxLat() + ", " + map.getMaxLon());
        System.out.println("  Total Coverage Area: " + String.format("%.2f", areaKm2) + " km²");
        System.out.println("  (Coordinates inside this box will snap perfectly to the road network)");
        System.out.println("-----------------------------------------------------");
        printHelp();

        while (true) {
            System.out.print("\nAdmin> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] args = input.split("\\s+");
            String command = args[0].toLowerCase();

            try {
                switch (command) {
                    case "registerpassenger":
                        handleRegisterPassenger(args);
                        break;
                    case "registerdriver":
                        handleRegisterDriver(args);
                        break;
                    case "goonline":
                        handleGoOnline(args);
                        break;
                    case "estimateride":
                        handleEstimateRide(args);
                        break;
                    case "confirmride":
                        handleConfirmRide(args);
                        break;
                    case "startride":
                        handleStartRide(args);
                        break;
                    case "completeride":
                        handleCompleteRide(args);
                        break;
                    case "rateandpay":
                        handleRateAndPay(args);
                        break;
                    case "history":
                        handleHistory(args);
                        break;
                    case "demo":
                        handleDemo();
                        break;
                    case "benchmark":
                        handleBenchmark();
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "exit":
                        System.out.println("Shutting down Mini Uber Backend...");
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (RideShareException e) {
                // Safely catching domain exceptions without crashing the REPL loop
                System.out.println("[ERROR] Domain Rule Violation: " + e.getMessage());
            } catch (Exception e) {
                // Catching parsing/array out of bound errors dynamically
                System.out.println("[ERROR] Invalid command arguments or system error: " + e.getMessage());
                System.out.println("Type 'help' to see correct command syntax.");
            }
        }
    }

    private void printHelp() {
        System.out.println("Available Commands:");
        System.out.println("  registerPassenger <name> <phoneNumber>");
        System.out.println("  registerDriver <name> <phoneNumber> <licensePlate> <make> <model> <ECONOMY|PREMIUM|SUV>");
        System.out.println("  goOnline <driverId> <lat> <lon>");
        System.out.println("  estimateRide <passengerId> <pickupLat> <pickupLon> <dropLat> <dropLon>");
        System.out.println("  confirmRide <passengerId> <pickupLat> <pickupLon> <dropLat> <dropLon>");
        System.out.println("  startRide <rideId> <driverId>");
        System.out.println("  completeRide <rideId>");
        System.out.println("  rateAndPay <rideId> <rating(1-5)> <CASH|CARD|UPI>");
        System.out.println("  history <userId>");
        System.out.println("  demo");
        System.out.println("  benchmark");
        System.out.println("  exit");
    }

    private void handleRegisterPassenger(String[] args) {
        Passenger p = userService.registerPassenger(args[1], args[2]);
        System.out.println("Successfully registered Passenger: " + p.getName() + " [ID: " + p.getId() + "]");
    }

    private void handleRegisterDriver(String[] args) {
        VehicleType type = VehicleType.valueOf(args[6].toUpperCase());
        Vehicle vehicle = new Vehicle(args[3], args[4], args[5], type);
        Driver d = driverService.registerDriver(args[1], args[2], vehicle);
        System.out.println("Successfully registered Driver: " + d.getName() + " [ID: " + d.getId() + "]");
    }

    private void handleGoOnline(String[] args) {
        Driver d = driverService.viewAllDrivers().stream()
                .filter(driver -> driver.getId().equals(args[1]))
                .findFirst()
                .orElseThrow(() -> new RideShareException("Driver not found."));
        d.goOnline();
        driverService.updateDriverLocation(d.getId(), new Location(Double.parseDouble(args[2]), Double.parseDouble(args[3])));
        System.out.println("Driver " + d.getName() + " is now ONLINE at location (" + args[2] + ", " + args[3] + ").");
    }

    private void handleEstimateRide(String[] args) {
        Passenger p = (Passenger) userService.viewAllUsers().stream()
                .filter(user -> user.getId().equals(args[1]) && user instanceof Passenger)
                .findFirst()
                .orElseThrow(() -> new RideShareException("Passenger not found."));
                
        Location pickup = new Location(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        Location dropoff = new Location(Double.parseDouble(args[4]), Double.parseDouble(args[5]));
        
        double distance = rideService.estimateRideDistance(pickup, dropoff);
        double fare = rideService.estimateFare(distance);
        
        System.out.println("\n[Estimate]");
        System.out.println("Distance:      " + String.format("%.2f", distance) + " km");
        System.out.println("Estimated Fare: " + String.format("%.2f", fare) + " Rs");
        System.out.println("=> Type 'confirmRide " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " " + args[5] + "' to call nearest driver.");
    }

    private void handleConfirmRide(String[] args) {
        Passenger p = (Passenger) userService.viewAllUsers().stream()
                .filter(user -> user.getId().equals(args[1]) && user instanceof Passenger)
                .findFirst()
                .orElseThrow(() -> new RideShareException("Passenger not found."));
                
        Location pickup = new Location(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        Location dropoff = new Location(Double.parseDouble(args[4]), Double.parseDouble(args[5]));
        
        Ride ride = rideService.confirmRide(p, pickup, dropoff);
        System.out.println("Ride Successfully Booked! [Ride ID: " + ride.getId() + "]");
        System.out.println("Distance: " + String.format("%.2f", ride.getDistance()) + " km");
        System.out.println("=> Type 'startRide " + ride.getId() + " " + ride.getDriver().getId() + "' to begin the trip.");
    }

    private void handleStartRide(String[] args) {
        Ride ride = rideService.startRide(args[1], args[2]);
        System.out.println("Ride " + ride.getId() + " has officially started.");
        System.out.println("=> Type 'completeRide " + ride.getId() + "' to finish the trip.");
    }

    private void handleCompleteRide(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: completeRide <rideId>");
        }
        Ride ride = rideService.completeRide(args[1], false);
        
        System.out.println("Ride " + ride.getId() + " is complete. Fare due: $" + String.format("%.2f", ride.getFare()));
        System.out.println("\n[NOTIFICATION TO PASSENGER]: Your ride has reached its destination.");
        System.out.println("=> Please submit your payment and rate your driver.");
        System.out.println("=> Type 'rateAndPay " + args[1] + " <rating(1-5)> <CASH|CARD|UPI>' to finalize the trip.");
    }

    private void handleRateAndPay(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("Usage: rateAndPay <rideId> <rating> <CASH|CARD|UPI>");
        }
        
        Ride ride = rideService.viewAllRides().stream()
                .filter(r -> r.getId().equals(args[1]))
                .findFirst()
                .orElseThrow(() -> new RideShareException("Ride not found."));
                
        // 1. Process Rating
        int rating = Integer.parseInt(args[2]);
        driverService.rateDriver(ride.getDriver().getId(), rating);
        System.out.println("Passenger rated Driver " + ride.getDriver().getName() + " " + rating + " stars!");
        
        // 2. Process Payment
        PaymentMethod method;
        switch(args[3].toUpperCase()) {
            case "CASH": method = new CashPayment(); break;
            case "CARD": method = new CardPayment(); break;
            case "UPI": method = new UpiPayment(); break;
            default: throw new IllegalArgumentException("Invalid payment method.");
        }
        
        Payment payment = paymentService.createPayment(ride.getId(), ride.getFare());
        paymentService.processPayment(payment, method);
        System.out.println("Payment of $" + String.format("%.2f", ride.getFare()) + " processed successfully via " + args[3].toUpperCase() + ".");
        System.out.println("Trip is fully concluded. Thank you for riding!");
    }

    private void handleHistory(String[] args) {
        User u = userService.viewAllUsers().stream()
            .filter(user -> user.getId().equals(args[1]))
            .findFirst()
            .orElse(null);
            
        if (u != null && u instanceof Passenger) {
            ((Passenger)u).displayHistory();
            return;
        }
        
        Driver d = driverService.viewAllDrivers().stream()
            .filter(driver -> driver.getId().equals(args[1]))
            .findFirst()
            .orElse(null);
            
        if (d != null) {
            d.displayHistory();
            return;
        }
        
        throw new RideShareException("User ID not found in system.");
    }
    private void handleDemo() {
        System.out.println("--- Bootstrapping Demo Data ---");
        
        // Register Demo Passenger
        Passenger passenger = userService.registerPassenger("Demo Passenger", "555-0000");
        System.out.println("Registered Passenger: " + passenger.getName() + " [ID: " + passenger.getId() + "]");
        
        // Register and Place Drivers on specific Delhi grid coordinates
        app.CityMap map = app.CityMap.getInstance();
        
        // Driver 1: Bottom Left (28.590, 77.160)
        Driver d1 = driverService.registerDriver("Driver Alice", "555-1111", new Vehicle("L1", "Toyota", "Camry", VehicleType.ECONOMY));
        d1.goOnline();
        driverService.updateDriverLocation(d1.getId(), new Location(28.590, 77.160));
        System.out.println("Online Driver: " + d1.getName() + " [ID: " + d1.getId() + "] (Economy)");

        // Driver 2: Middle (28.630, 77.215)
        Driver d2 = driverService.registerDriver("Driver Bob", "555-2222", new Vehicle("L2", "Honda", "CRV", VehicleType.SUV));
        d2.goOnline();
        driverService.updateDriverLocation(d2.getId(), new Location(28.630, 77.215));
        System.out.println("Online Driver: " + d2.getName() + " [ID: " + d2.getId() + "] (SUV)");

        // Driver 3: Top Right (28.670, 77.270)
        Driver d3 = driverService.registerDriver("Driver Charlie", "555-3333", new Vehicle("L3", "BMW", "5-Series", VehicleType.PREMIUM));
        d3.goOnline();
        driverService.updateDriverLocation(d3.getId(), new Location(28.670, 77.270));
        System.out.println("Online Driver: " + d3.getName() + " [ID: " + d3.getId() + "] (Premium)");
        
        System.out.println("\nDemo data populated successfully! Drivers are scattered across Delhi.");
        System.out.println("To test the Dijkstra routing engine, copy and paste this command:");
        System.out.println("estimateRide " + passenger.getId() + " 28.54 77.14 28.7 77.3");
    }
    
    private void handleBenchmark() {
        tests.AlgorithmBenchmark.main(new String[0]);
    }
}
