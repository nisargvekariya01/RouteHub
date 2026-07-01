package app;

import exceptions.RideShareException;
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

import java.util.Scanner;

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
                    case "requestride":
                        handleRequestRide(args);
                        break;
                    case "startride":
                        handleStartRide(args);
                        break;
                    case "completeride":
                        handleCompleteRide(args);
                        break;
                    case "pay":
                        handlePay(args);
                        break;
                    case "history":
                        handleHistory(args);
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
        System.out.println("  requestRide <passengerId> <pickupLat> <pickupLon> <dropLat> <dropLon>");
        System.out.println("  startRide <rideId> <driverId>");
        System.out.println("  completeRide <rideId>");
        System.out.println("  pay <rideId> <CASH|CARD|UPI>");
        System.out.println("  history <userId>");
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
        d.setCurrentLocation(new Location(Double.parseDouble(args[2]), Double.parseDouble(args[3])));
        System.out.println("Driver " + d.getName() + " is now ONLINE at location (" + args[2] + ", " + args[3] + ").");
    }

    private void handleRequestRide(String[] args) {
        Passenger p = (Passenger) userService.viewAllUsers().stream()
                .filter(user -> user.getId().equals(args[1]) && user instanceof Passenger)
                .findFirst()
                .orElseThrow(() -> new RideShareException("Passenger not found."));
                
        Location pickup = new Location(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        Location dropoff = new Location(Double.parseDouble(args[4]), Double.parseDouble(args[5]));
        
        Ride ride = rideService.requestRide(p, pickup, dropoff);
        System.out.println("Ride Successfully Booked! [Ride ID: " + ride.getId() + "]");
    }

    private void handleStartRide(String[] args) {
        Ride ride = rideService.startRide(args[1], args[2]);
        System.out.println("Ride " + ride.getId() + " has officially started.");
    }

    private void handleCompleteRide(String[] args) {
        Ride ride = rideService.completeRide(args[1], false);
        System.out.println("Ride " + ride.getId() + " is complete. Fare due: $" + String.format("%.2f", ride.getFare()));
    }

    private void handlePay(String[] args) {
        Ride ride = rideService.viewAllRides().stream()
                .filter(r -> r.getId().equals(args[1]))
                .findFirst()
                .orElseThrow(() -> new RideShareException("Ride not found."));
                
        PaymentMethod method;
        switch(args[2].toUpperCase()) {
            case "CASH": method = new CashPayment(); break;
            case "CARD": method = new CardPayment(); break;
            case "UPI": method = new UpiPayment(); break;
            default: throw new IllegalArgumentException("Invalid payment method.");
        }
        
        Payment payment = paymentService.createPayment(ride.getId(), ride.getFare());
        paymentService.processPayment(payment, method);
        System.out.println("Payment processed successfully.");
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
}
