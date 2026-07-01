package services;

import models.Driver;
import models.Vehicle;
import repositories.DriverRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service handling driver-related business logic.
 * 
 * SOLID Principles Applied:
 * - SRP: Isolates driver rules from the rest of the application.
 * - DIP: Connects to storage only through the injected DriverRepository interface.
 */
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Registers a new driver in the system.
     * Ensures input is valid and generates a system ID before storing.
     * 
     * @param name Driver's name
     * @param phoneNumber Driver's contact number
     * @param vehicle The driver's starting vehicle (can be null if added later)
     * @return The newly registered Driver
     */
    public Driver registerDriver(String name, String phoneNumber, Vehicle vehicle) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty.");
        }

        String id = UUID.randomUUID().toString();
        Driver driver = new Driver(id, name, phoneNumber, vehicle);
        
        driverRepository.save(driver);
        return driver;
    }

    /**
     * Adds or updates a vehicle for an existing driver.
     * Contains the business logic validating that the driver actually exists 
     * before assigning a new vehicle.
     * 
     * @param driverId ID of the driver receiving the vehicle
     * @param vehicle The new vehicle object
     * @return The updated Driver
     */
    public Driver addVehicle(String driverId, Vehicle vehicle) {
        if (driverId == null || vehicle == null) {
            throw new IllegalArgumentException("Driver ID and Vehicle are required.");
        }

        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (!driverOpt.isPresent()) {
            throw new IllegalArgumentException("Driver with ID " + driverId + " not found.");
        }

        Driver driver = driverOpt.get();
        driver.setVehicle(vehicle);
        
        driverRepository.update(driver);
        return driver;
    }

    /**
     * Retrieves all registered drivers.
     * 
     * @return A list of all drivers.
     */
    public List<Driver> viewAllDrivers() {
        return driverRepository.findAll();
    }
}
