package services;

import exceptions.DriverNotFoundException;
import exceptions.DuplicateUserException;
import java.util.List;
import java.util.UUID;
import models.Driver;
import models.Location;
import models.Vehicle;
import repositories.DriverRepository;
import utils.SpatialGrid;

public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver registerDriver(String name, String phoneNumber, Vehicle vehicle) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty.");
        }

        // Utilizing custom exception for business constraints
        boolean exists = driverRepository.findAll().stream()
                .anyMatch(d -> d.getPhoneNumber().equals(phoneNumber));
        if (exists) {
            throw new DuplicateUserException("Driver with phone number " + phoneNumber + " already exists.");
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

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));

        driver.setVehicle(vehicle);
        driverRepository.update(driver);
        return driver;
    }
    
    public Driver rateDriver(String driverId, int rating) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));
            
        driver.addRating(rating);
        driverRepository.update(driver);
        return driver;
    }

    public void updateDriverLocation(String driverId, Location newLocation) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));
            
        // Updates the driver's object state AND syncs them into the correct spatial sector bucket
        SpatialGrid.getInstance().updateDriverLocation(driver, newLocation);
        driverRepository.update(driver);
    }

    public List<Driver> viewAllDrivers() {
        return driverRepository.findAll();
    }
}
