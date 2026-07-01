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

    public Driver registerDriver(String name, String phoneNumber, Vehicle vehicle) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty.");
        }

        String id = UUID.randomUUID().toString();
        Driver driver = new Driver(id, name, phoneNumber, vehicle);
        
        driverRepository.save(driver);
        return driver;
    }

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
     * Submits a new rating (1-5) for a driver after a ride, recalculating their aggregate average.
     */
    public Driver rateDriver(String driverId, int rating) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new IllegalArgumentException("Driver not found."));
            
        driver.addRating(rating);
        driverRepository.update(driver);
        return driver;
    }

    public List<Driver> viewAllDrivers() {
        return driverRepository.findAll();
    }
}
